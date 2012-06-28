/**
 * 
 */
package org.obeonetwork.tools.doc.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.FileEditorInput;
import org.obeonetwork.tools.doc.DocBridgeUI;
import org.obeonetwork.tools.doc.core.DocumentationLink;


/**
 * @author <a href="goulwen.lefur@obeo.fr">Goulwen Le Fur</a>
 *
 */
public class DocumentationLinkUIService implements DocumentationLinkCommandFactory {

	/**
	 * @return
	 */
	public static DocumentationLinkCommandFactory getUIService() {
		return new DocumentationLinkUIService();
	}

	/**
	 * {@inheritDoc}
	 * @see org.obeonetwork.tools.doc.ui.DocumentationLinkCommandFactory#createOpenCommand(org.obeonetwork.tools.doc.core.DocumentationLink)
	 */
	public IHandler createOpenCommand(DocumentationLink link) {
		if (link.isWorkspaceDocumentation()) {
			return new OpenWorkspaceEntry(link);
		} else {
			return new OpenWebEntry(link);
		}
	}

	private void reportError(Exception e) {
		Status status = new Status(IStatus.ERROR, DocBridgeUI.PLUGIN_ID, "Error during document opening.", e); //$NON-NLS-1$
		ErrorDialog.openError(DocBridgeUI.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell(), DocBridgeUI.getInstance().getString("OpeningErrorDialog_title"), DocBridgeUI.getInstance().getString("OpeningErrorDialog_msg"), status); //$NON-NLS-1$ //$NON-NLS-2$
		DocBridgeUI.getInstance().getLog().log(status);
	}

	/**
	 * Basic implementation for opening 
	 * @author <a href="goulwen.lefur@obeo.fr">Goulwen Le Fur</a>
	 */
	private class OpenWorkspaceEntry extends AbstractHandler {

		private DocumentationLink entry;

		/**
		 * @param entry
		 */
		private OpenWorkspaceEntry(DocumentationLink entry) {
			this.entry = entry;
		}

		/**
		 * {@inheritDoc}
		 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
		 */
		public Object execute(ExecutionEvent event) throws ExecutionException {
			try {
				IWorkbench workbench = DocBridgeUI.getInstance().getWorkbench();
				IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(entry.getWorkspaceRelativeValue());
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(entry.getWorkspaceRelativeValue()));
				workbench.getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				reportError(e);
			}
			return null;
		}

	}

	/**
	 * Basic implementation for opening 
	 * @author <a href="goulwen.lefur@obeo.fr">Goulwen Le Fur</a>
	 */
	private class OpenWebEntry extends AbstractHandler {

		private DocumentationLink entry;

		/**
		 * @param entry
		 */
		private OpenWebEntry(DocumentationLink entry) {
			this.entry = entry;
		}

		/**
		 * {@inheritDoc}
		 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
		 */
		public Object execute(ExecutionEvent event) throws ExecutionException {
			try {
				IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
				IWebBrowser browser = support.createBrowser("DocumentLinkerBrowser"); //$NON-NLS-1$
				browser.openURL(new URL(entry.getWebFormattedValue()));
			} catch (PartInitException e) {
				reportError(e);
			} catch (MalformedURLException e) {
				reportError(e);
			}
			return null;
		}

	}

}