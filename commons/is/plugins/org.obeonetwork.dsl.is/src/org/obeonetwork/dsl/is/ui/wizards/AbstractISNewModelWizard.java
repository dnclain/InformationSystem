package org.obeonetwork.dsl.is.ui.wizards;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sirius.business.api.modelingproject.ModelingProject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.obeonetwork.dsl.is.util.SiriusSessionUtils;

abstract public class AbstractISNewModelWizard extends Wizard implements INewWizard {

	protected String windowTitle;
	protected ImageDescriptor imageDescriptor;
	
	protected IStructuredSelection selection;
	protected IWorkbench workbench;
	private NewModelCreationPage modelCreationPage;
	
	private Resource createdResource;
	
	public AbstractISNewModelWizard(String windowTitle, ImageDescriptor imageDescriptor) {
		setWindowTitle(windowTitle);
		this.imageDescriptor = imageDescriptor;
	}

	@Override
	public void addPages() {
		modelCreationPage = new NewModelCreationPage("ModelCreationPage", selection);
		addPage(modelCreationPage);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setDefaultPageImageDescriptor(imageDescriptor);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, (monitor) -> {
				if (isDataValid(monitor)) {
					setCreatedResource(createResource(monitor));
				}
			});
		} catch (Exception e) {
			// TODO Report error
			e.printStackTrace();
			return false;
		}
		return getCreatedResource() != null;
	}
	
	
	protected boolean isDataValid(IProgressMonitor monitor) {
		NewModelWizardData data = modelCreationPage.getData();
		if (data.getTargetContainer() instanceof IProject) {
			// We have to check the case of a resource creation at the root of
			// a modeling project which has not been loaded yet
			// If the modeling project is a shared one, it would mean the resource should be create
			// in remote root CDOResourceFolder
			// We have to load the session to make sure
			ModelingProject targetModelingProject = data.getTargetModelingProject();
			if (targetModelingProject != null && targetModelingProject.getSession() == null) {
				// Project is a modeling project but has not been loaded yet
				boolean loaded = SiriusSessionUtils.loadModelingProject(targetModelingProject.getProject(), true, getShell());
				if (!loaded) {
					return false;
				}
				CDOResourceFolder remoteRootFolder = SiriusSessionUtils.getRemoteRootFolder(targetModelingProject.getSession());
				if (remoteRootFolder != null  && SiriusSessionUtils.remoteResourceAlreadyExistsInFolder(remoteRootFolder, data.getTargetResourceName())) {
					MessageDialog.openError(getShell(), getWizardTitle(), "A resource with this name already exists");
					modelCreationPage.validatePage();
					return false;
				}
			}
		}
		
		return true;
	}
	
	protected Resource createResource(IProgressMonitor monitor) {
		NewModelWizardData data = modelCreationPage.getData();
		ModelingProject targetModelingProject = data.getTargetModelingProject();
		
		if (targetModelingProject != null && data.getTargetContainer() instanceof IFolder) {
			// Local file in modeling project
			return SiriusSessionUtils.createResource(targetModelingProject, (IFolder)data.getTargetContainer(), data.getTargetResourceName(), createInitialObjects(), monitor);
		} else if (targetModelingProject != null && data.getTargetContainer() instanceof CDOResourceFolder) {
			// Remote modeling project
			return SiriusSessionUtils.createResource(targetModelingProject, (CDOResourceFolder)data.getTargetContainer(), data.getTargetResourceName(), createInitialObjects(), monitor);
		} else if (targetModelingProject == null && data.getTargetContainer() instanceof IContainer) {
			// Local file in non modeling project
			return SiriusSessionUtils.createResource((IContainer)data.getTargetContainer(), data.getTargetResourceName(), createInitialObjects(), monitor);
		} else if (targetModelingProject != null && data.getTargetContainer() instanceof IProject) {
			// New resource at the root of a modeling project
			if (SiriusSessionUtils.isSharedModelingProjectSession(targetModelingProject.getSession())) {
				CDOResourceFolder remoteRootFolder = SiriusSessionUtils.getRemoteRootFolder(targetModelingProject.getSession());
				if (remoteRootFolder != null) {
					return SiriusSessionUtils.createResource(targetModelingProject, remoteRootFolder, data.getTargetResourceName(), createInitialObjects(), monitor);
				}
			} else {
				return SiriusSessionUtils.createResource(targetModelingProject, (IProject)data.getTargetContainer(), data.getTargetResourceName(), createInitialObjects(), monitor);
			}
		}
		return null;
	}

	abstract Collection<EObject> createInitialObjects();
	
	abstract String getWizardTitle();

	public Resource getCreatedResource() {
		return createdResource;
	}

	public void setCreatedResource(Resource createdResource) {
		this.createdResource = createdResource;
	}
	
}
