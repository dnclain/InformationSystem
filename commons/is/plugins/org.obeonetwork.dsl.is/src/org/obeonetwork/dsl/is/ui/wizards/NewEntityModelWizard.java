package org.obeonetwork.dsl.is.ui.wizards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.ecore.EObject;
import org.obeonetwork.dsl.entity.EntityFactory;
import org.obeonetwork.dsl.entity.Root;

public class NewEntityModelWizard extends AbstractISNewModelWizard {

	public NewEntityModelWizard() {
		super("New Entity Model", null);
	}

	@Override
	Collection<EObject> createInitialObjects() {
		Root rootObject = EntityFactory.eINSTANCE.createRoot();
		rootObject.setCreatedOn(new Date());
		return Arrays.asList(rootObject);
	}

	@Override
	String getWizardTitle() {
		return "New Entity model";
	}
}
