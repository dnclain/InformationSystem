/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.obeonetwork.dsl.statemachine;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.common.util.EList;
import org.obeonetwork.dsl.environment.ObeoDSMObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.obeonetwork.dsl.statemachine.AbstractState#getIncomingTransitions <em>Incoming Transitions</em>}</li>
 *   <li>{@link org.obeonetwork.dsl.statemachine.AbstractState#getOutcomingTransitions <em>Outcoming Transitions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.obeonetwork.dsl.statemachine.StateMachinePackage#getAbstractState()
 * @model abstract="true"
 * @generated
 */
public interface AbstractState extends ObeoDSMObject {

	/**
	 * Returns the value of the '<em><b>Incoming Transitions</b></em>' reference list.
	 * The list contents are of type {@link org.obeonetwork.dsl.statemachine.Transition}.
	 * It is bidirectional and its opposite is '{@link org.obeonetwork.dsl.statemachine.Transition#getTo <em>To</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Transitions</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Transitions</em>' reference list.
	 * @see org.obeonetwork.dsl.statemachine.StateMachinePackage#getAbstractState_IncomingTransitions()
	 * @see org.obeonetwork.dsl.statemachine.Transition#getTo
	 * @model opposite="to"
	 * @generated
	 */
	EList<Transition> getIncomingTransitions();

	/**
	 * Returns the value of the '<em><b>Outcoming Transitions</b></em>' reference list.
	 * The list contents are of type {@link org.obeonetwork.dsl.statemachine.Transition}.
	 * It is bidirectional and its opposite is '{@link org.obeonetwork.dsl.statemachine.Transition#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outcoming Transitions</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outcoming Transitions</em>' reference list.
	 * @see org.obeonetwork.dsl.statemachine.StateMachinePackage#getAbstractState_OutcomingTransitions()
	 * @see org.obeonetwork.dsl.statemachine.Transition#getFrom
	 * @model opposite="from"
	 * @generated
	 */
	EList<Transition> getOutcomingTransitions();
} // AbstractState
