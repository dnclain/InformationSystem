/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.obeonetwork.graal.design.graalfeatureextensions.util;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * The <b>Resource Factory</b> associated with the package.
 * <!-- end-user-doc -->
 * @see org.obeonetwork.graal.design.graalfeatureextensions.util.GraalfeatureextensionsResourceImpl
 * @generated
 */
public class GraalfeatureextensionsResourceFactoryImpl extends ResourceFactoryImpl {
	/**
	 * Creates an instance of the resource factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GraalfeatureextensionsResourceFactoryImpl() {
		super();
	}

	/**
	 * Creates an instance of the resource.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Resource createResource(URI uri) {
		Resource result = new GraalfeatureextensionsResourceImpl(uri);
		return result;
	}

} //GraalfeatureextensionsResourceFactoryImpl
