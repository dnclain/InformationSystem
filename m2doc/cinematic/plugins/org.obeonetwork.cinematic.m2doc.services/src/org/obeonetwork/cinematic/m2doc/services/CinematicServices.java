package org.obeonetwork.cinematic.m2doc.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.acceleo.annotations.api.documentation.Documentation;
import org.eclipse.acceleo.annotations.api.documentation.Example;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.obeonetwork.dsl.cinematic.AbstractPackage;
import org.obeonetwork.dsl.cinematic.CinematicRoot;
import org.obeonetwork.dsl.cinematic.design.services.CinematicBindingServices;
import org.obeonetwork.dsl.cinematic.design.services.CinematicLabelServices;
import org.obeonetwork.dsl.cinematic.flow.ActionState;
import org.obeonetwork.dsl.cinematic.flow.Flow;
import org.obeonetwork.dsl.cinematic.flow.FlowAction;
import org.obeonetwork.dsl.cinematic.flow.FlowState;
import org.obeonetwork.dsl.cinematic.flow.Transition;
import org.obeonetwork.dsl.cinematic.flow.ViewState;
import org.obeonetwork.dsl.cinematic.view.ViewContainer;
import org.obeonetwork.dsl.cinematic.view.ViewElement;
import org.obeonetwork.dsl.cinematic.view.ViewEvent;
import org.obeonetwork.dsl.environment.Action;
import org.obeonetwork.dsl.environment.Annotation;
import org.obeonetwork.dsl.environment.MetaData;
import org.obeonetwork.dsl.environment.MetaDataContainer;
import org.obeonetwork.dsl.environment.ObeoDSMObject;
import org.obeonetwork.dsl.requirement.Requirement;
import org.obeonetwork.dsl.technicalid.Identifiable;
import org.obeonetwork.m2doc.element.MImage;
import org.obeonetwork.m2doc.element.impl.MImageImpl;
import org.obeonetwork.tools.doc.core.DocumentationLink;
//import org.eclipse.emf.common.util.URI;

public class CinematicServices {

	private CinematicLabelServices labelService = new CinematicLabelServices();
	private CinematicBindingServices bindingService = new CinematicBindingServices ();


	// @formatter:off
	@Documentation(
			comment = "{m:obj.isCinematicObject()}",
		    value = "Returns if the target object is a Cinematic element.",
		    examples = {
		    		@Example(
		    				expression = "{m:obj.isCinematicObject()}", 
		    				result = "True if the target object is a Cinematic element, false otherwise.")
		    }
		)
	// @formatter:on	
	public boolean isCinematicObject(EObject obj) {
		EPackage pack = obj.eClass().getEPackage();
		while(pack.eContainer() instanceof EPackage) {
			pack = (EPackage) pack.eContainer();
		}
		return pack.getName().equals("cinematic");
	}

	// @formatter:off
	@Documentation(
			comment = "{m:flowEvent.getAnnotations()}",
		    value = "Returns the list of metadata annotations of the target Flow Event.",
		    examples = {
		    		@Example(
		    				expression = "{m:flowEvent.getAnnotations().title->sep(',')}", 
		    				result = "A comma separated list of Flow Event's annotation titles.")
		    }
		)
	// @formatter:on	
	public List<Annotation> getAnnotations(ObeoDSMObject obj) {
		MetaDataContainer metadatas = obj.getMetadatas();
		if (metadatas != null) {
			List<Annotation> result = new ArrayList<Annotation>();
			for (MetaData md : metadatas.getMetadatas()) {
				if (md instanceof Annotation) {
					Annotation annotation = (Annotation) md;
					result.add(annotation);
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	// @formatter:off
	@Documentation(
			comment = "{m:viewContainer.getAllEvents()}",
		    value = "Returns the list of all events defined in this View Container or one of its sub View Containers.",
		    examples = {
		    		@Example(
		    				expression = "{m:viewContainer.getAllEvents()}", 
		    				result = "A list of View Events.")
		    }
		)
	// @formatter:on
	public List<ViewEvent> getAllEvents(ViewContainer viewContainer) {
		List<ViewEvent> allEvents = new ArrayList<>();
		
		collectAllEvents(allEvents, viewContainer);
		
		return allEvents;
	}

	private void collectAllEvents(List<ViewEvent> allEvents, ViewContainer viewContainer) {
		allEvents.addAll(viewContainer.getEvents());
		
		for(ViewElement subViewElement : viewContainer.getViewElements()) {
			allEvents.addAll(subViewElement.getEvents());
		}
		
		for(ViewContainer subViewContainer : viewContainer.getViewContainers()) {
			collectAllEvents(allEvents, subViewContainer);
		}
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:obj.cinematicLabel()}",
		    value = "Returns the presentation label of the target Cinematic element.",
		    examples = {
		    		@Example(
		    				expression = "{m:obj.getCinematicLabel()}", 
		    				result = "The label of the element.")
		    }
		)
	// @formatter:on	
	public String cinematicLabel(EObject obj) {
		if(obj != null) {
			return labelService.getCinematicLabel(obj);
		} else {
			return "";
		}
	}

	// @formatter:off
	@Documentation(
			comment = "{m:obj.cinematicIcon()}",
		    value = "Returns the presentation icon of the target Cinematic element.",
		    examples = {
		    		@Example(
		    				expression = "{m:obj.getCinematicIcon()}", 
		    				result = "The icon of the element.")
		    }
		)
	// @formatter:on	
	public MImage cinematicIcon(EObject obj) {
		
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		AdapterFactoryLabelProvider adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
		Image image = adapterFactoryLabelProvider.getImage(obj);
		
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };
		if(obj instanceof Identifiable) {
			Identifiable identifiable = (Identifiable) obj;
			try {
				String tempFileNamePrefix = obj.eClass().getName() + "_" + identifiable.getTechnicalid();
				File tempFile = File.createTempFile(tempFileNamePrefix, ".png");
				tempFile.deleteOnExit();
				imageLoader.save(new FileOutputStream(tempFile), SWT.IMAGE_PNG);
				
				URL imageUrl = tempFile.toURI().toURL();
				URI uri = URI.createURI(imageUrl.toString());
				return new MImageImpl(uri);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Can't get object image, unmanaged object type: " + obj.getClass().getName());
		}
		
		return null;
	}

	// @formatter:off
	@Documentation(
			comment = "{m:viewElement.isRequired()}",
		    value = "Returns the string 'Oui' if the target View Element is required, 'Non' otherwise.",
		    examples = {
		    		@Example(
		    				expression = "{m:viewElement.isRequired()}", 
		    				result = "'Oui' or 'Non'.")
		    }
		)
	// @formatter:on	
	public String isRequired(ViewElement viewElement) {
		if (viewElement.isRequired())
			return "Oui";
		else
			return "Non";
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:package.subElements()}",
		    value = "Returns the list of the target package sub elements, as seen in the Cinematic tree.",
		    examples = {
		    		@Example(
		    				expression = "{m:package.subElements()}", 
		    				result = "The list of the sub elements.")
		    }
		)
	// @formatter:on	
	public List<EObject> subElements(AbstractPackage pack){
		List<EObject>  result = new ArrayList<> ();
		result.addAll (pack.getSubPackages());
		result.addAll (pack.getViewContainers());
		if (pack instanceof CinematicRoot) {
			CinematicRoot root = (CinematicRoot) pack;
			result.addAll(root.getToolkits());
		}
		return result;
	}

	// @formatter:off
	@Documentation(
			comment = "{m:viewContainer.subElements()}",
		    value = "Returns the list of the target ViewContainer sub elements, as seen in the Cinematic tree.",
		    examples = {
		    		@Example(
		    				expression = "{m:viewContainer.subElements()}", 
		    				result = "The list of the sub elements.")
		    }
		)
	// @formatter:on	
	public List<EObject> subElements (ViewContainer viewContainer) {
		List<EObject>  result = new ArrayList<> ();
		result.addAll(viewContainer.getViewContainers());
		result.addAll(viewContainer.getViewElements());
		result.addAll(viewContainer.getViewContainerReferences());
		result.addAll(viewContainer.getEvents());
		result.addAll(viewContainer.getActions());
		result.addAll(bindingService.getCinematicBindingInfos(viewContainer));
		
		return result;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:cinematicRoot.getAllLinkedDocuments()}",
		    value = "Returns the list of all documents attached to any of the elements "
		    		+ "contained in the target Cinematic Root.",
		    examples = {
		    		@Example(
		    				expression = "{m:cinematicRoot.getAllLinkedDocuments()}", 
		    				result = "The list of all of the documents.")
		    }
		)
	// @formatter:on	
	public List<Annotation> getAllLinkedDocuments(CinematicRoot cinematicRoot) {
		Iterator<EObject> iterator = cinematicRoot.eAllContents();
		List<Annotation> result = new ArrayList<Annotation>();
		while (iterator.hasNext()) {
			EObject obj = iterator.next();
			if (isCinematicObject(obj) && obj instanceof ObeoDSMObject) {
				ObeoDSMObject dsmObject = (ObeoDSMObject) obj;
				MetaDataContainer mdContainer = dsmObject.getMetadatas();
				if (mdContainer != null) {
					for (MetaData md: mdContainer.getMetadatas()) {
						if (md instanceof Annotation) {
							Annotation annotation = (Annotation) md;
							if (annotation.getTitle() != null && annotation.getTitle().startsWith(DocumentationLink.DOCUMENTATION_ANNOTATION_TITLE)) {
								result.add((Annotation) md);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:doc.getDocumentTitle()}",
		    value = "Returns the title of the document.",
		    examples = {
		    		@Example(
		    				expression = "{m:doc.getDocumentTitle()}", 
		    				result = "The title of the document.")
		    }
		)
	// @formatter:on	
	public String getDocumentTitle(Annotation doc) {
		if(doc.getTitle() != null && doc.getTitle().length() >= DocumentationLink.DOCUMENTATION_ANNOTATION_TITLE.length() + 3) {
			return doc.getTitle().substring(DocumentationLink.DOCUMENTATION_ANNOTATION_TITLE.length() + 3);
		}
		
		return "";
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:doc.getDocumentedObject()}",
		    value = "Returns the object this document is attached to.",
		    examples = {
		    		@Example(
		    				expression = "{m:doc.getDocumentedObject()}", 
		    				result = "The documented object.")
		    }
		)
	// @formatter:on	
	public EObject getDocumentedObject(Annotation doc) {
		if(doc.eContainer() != null) {
			return doc.eContainer().eContainer();
		}
		return null;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:cinematicRoot.getAllActionStatesWithOperations()}",
		    value = "Returns the list of all the Action States contained in the target Cinematic Root, having at least one Operation.",
		    examples = {
		    		@Example(
		    				expression = "{m:cinematicRoot.getAllActionStatesWithOperations()}", 
		    				result = "The list of all the Action States having at least one Operation.")
		    }
		)
	// @formatter:on	
	public List<ActionState> getAllActionStatesWithOperations (CinematicRoot cinematicRoot){
		List<ActionState>  result = new ArrayList<ActionState> ();
		for (Flow flow : cinematicRoot.getFlows()) {
			for (FlowState actionState : flow.getStates()) {
				if (actionState instanceof ActionState) {
					ActionState as = (ActionState) actionState;
					if (getOperations(as).size() > 0) {
						result.add(as);
					}
				}
			}
		}
		
		return result;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:actionState.getOperations()}",
		    value = "Returns the list of all the Operations attached to the target Action State (by way of Flow Actions).",
		    examples = {
		    		@Example(
		    				expression = "{m:actionState.getOperations()}", 
		    				result = "The list Operations attached to the target Action State.")
		    }
		)
	// @formatter:on	
	public List<Action> getOperations(ActionState actionState) {
		List<Action> result = new ArrayList<>();
		for (FlowAction flowAction : actionState.getActions()) {
			result.addAll(flowAction.getOperations());
		}
		return result;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:obj.getTypeName()}",
		    value = "Returns the name of the given object's type, formatted with white spaces.",
		    examples = {
		    		@Example(
		    				expression = "{m:obj.getTypeName()}", 
		    				result = "A string representation of the given object's type.")
		    }
		)
	// @formatter:on	
	public String getTypeName(EObject obj) {
		StringBuffer typeName = new StringBuffer();
		
		char[] className = obj.eClass().getName().toCharArray();
		for(int i = 0; i < className.length; i++) {
			char c = className[i];
			if(Character.isUpperCase(c) && i > 0) {
				typeName.append(" ");
			}
			typeName.append(c);
		}
		
		return typeName.toString();
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:obj.getName()}",
		    value = "Returns the name of the given object if it defines one. "
		    		+ "Returns an empty string in all other cases.",
		    examples = {
		    		@Example(
		    				expression = "{m:obj.getName()}", 
		    				result = "The object name.")
		    }
		)
	// @formatter:on	
	public String getName(EObject obj) {
		String name = "";
		
		if(obj != null) {
			EStructuralFeature nameFeature = obj.eClass().getEStructuralFeature("name");
			if(nameFeature != null) {
				Object nameValue = obj.eGet(nameFeature);
				if(nameValue instanceof String) {
					name = (String) nameValue;
				}
			}
		}
		
		return name;
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:flowState.isNewInstance()}",
		    value = "If the target Flow State is a View State, returns the string 'Oui' "
		    		+ "if it is a new instance and 'Non' if it is not. Returns 'N/A' if "
		    		+ "the the target Flow State is not a View State.",
		    examples = {
		    		@Example(
		    				expression = "{m:flowState.isNewInstance()}", 
		    				result = "'Oui' or 'Non' or 'N/A'.")
		    }
		)
	// @formatter:on	
	public String isNewInstance(FlowState flowState) {
		if(flowState instanceof ViewState) {
			if (((ViewState) flowState).isNewInstance())
				return "Oui";
			else
				return "Non";
		}
		return "N/A";
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:flowState.getViewContainers()}",
		    value = "If the target Flow State is a View State, returns the list "
		    		+ "of the referenced View Containers. Returns an empty list "
		    		+ "if the the target Flow State is not a View State.",
		    examples = {
		    		@Example(
		    				expression = "{m:flowState.isNewInstance()}", 
		    				result = "The list of the referenced View Containers.")
		    }
		)
	// @formatter:on	
	public List<ViewContainer> getViewContainers(FlowState flowState) {
		if(flowState instanceof ViewState) {
			return ((ViewState) flowState).getViewContainers();
		}
		return Collections.emptyList();
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:flowState.isRefresh()}",
		    value = "If the target Flow State is a View State, returns the string 'Oui' "
		    		+ "if it is set to refresh and 'Non' if it is not. Returns 'N/A' if "
		    		+ "the the target Flow State is not a View State.",
		    examples = {
		    		@Example(
		    				expression = "{m:flowState.isRefresh()}", 
		    				result = "'Oui' or 'Non' or 'N/A'.")
		    }
		)
	// @formatter:on	
	public String isRefresh(FlowState flowState) {
		if(flowState instanceof ViewState) {
			if (((ViewState) flowState).isRefresh())
				return "Oui";
			else
				return "Non";
		}
		return "N/A";
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:transition.isModal()}",
		    value = "Returns the string 'Oui' if the target Transition is modal, 'Non' otherwise.",
		    examples = {
		    		@Example(
		    				expression = "{m:transition.isModal()}", 
		    				result = "'Oui' or 'Non'.")
		    }
		)
	// @formatter:on	
	public String isModal(Transition transition) {
		if (transition.isModal())
			return "Oui";
		else
			return "Non";
	}
	
	// @formatter:off
	@Documentation(
			comment = "{m:transition.isModal()}",
		    value = "Returns the string 'Oui' if the target Transition is modal, 'Non' otherwise.",
		    examples = {
		    		@Example(
		    				expression = "{m:transition.isModal()}", 
		    				result = "'Oui' or 'Non'.")
		    }
		)
	// @formatter:on	
	public List<EObject> getReferencedCinematicObjects(Requirement requirement) {
		List<EObject> referencedCinematicObjects = new ArrayList<>();
		for(EObject o : requirement.getReferencedObject()) {
			if(isCinematicObject(o)) {
				referencedCinematicObjects.add(o);
			}
		}
		
		return referencedCinematicObjects;
	}
	
}
