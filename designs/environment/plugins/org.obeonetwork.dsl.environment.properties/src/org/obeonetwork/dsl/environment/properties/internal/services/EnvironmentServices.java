package org.obeonetwork.dsl.environment.properties.internal.services;

import org.eclipse.emf.ecore.EObject;
import org.obeonetwork.dsl.environment.EnvironmentFactory;
import org.obeonetwork.dsl.environment.MetaDataContainer;
import org.obeonetwork.dsl.environment.ObeoDSMObject;

public class EnvironmentServices {
	
	public MetaDataContainer getMetadataContainer(EObject self) {
		MetaDataContainer metadatas = null;
		if (self instanceof ObeoDSMObject) {
			metadatas = ((ObeoDSMObject) self).getMetadatas();
			if (metadatas == null) {
				metadatas = EnvironmentFactory.eINSTANCE.createMetaDataContainer();
				((ObeoDSMObject) self).setMetadatas(metadatas);
			}
		}
		return metadatas;
	}
}
