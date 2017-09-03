package de.wwu.maml.editor.service;

import java.util.HashSet;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import de.wwu.maml.inference.ModelInferrer;
import de.wwu.maml.inference.ModelInferrerManager;
import de.wwu.maml.editor.dialog.ObjectListSelectionDialog;
import de.wwu.maml.inference.ModelInferenceDataTypeHelper;
import de.wwu.maml.dsl.maml.Connector;
import de.wwu.maml.dsl.maml.ParameterConnector;
import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.UseCase;
import de.wwu.maml.dsl.mamldata.CustomType;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.dsl.mamlgui.Attribute;

/**
 * Service class for type-related editor methods.  
 * @author Christoph
 *
 */
public class ModelInferenceService {
	
	/**
	 * Trigger inference process and visual update of elements.
	 * @param obj
	 */
	public EObject updateAllDataTypes(EObject obj){
		if(obj instanceof UseCase){
			return updateAllDataTypes(obj);
		} else if(obj.eContainer() instanceof UseCase){
			return updateAllDataTypes((UseCase) obj.eContainer());
		}
		return null;
	}
	
	public EObject updateAllDataTypes(UseCase useCase){
		if(useCase == null) return null;
		
		// Check if we are in read-only mode
		Session session = SessionManager.INSTANCE.getSession(useCase);
		TransactionalEditingDomain editingDomain = session.getTransactionalEditingDomain();
		
		ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer(useCase);
		inferrer.startInferenceProcess(useCase, editingDomain.isReadOnly(useCase.eResource())); // Container is the use case itself
		
//		// No problem of resetting everything: View elements are only updated for actually changed types
//		Collection<ProcessFlowElement> pfes = useCase.getProcessFlowElements();
//		for(ProcessFlowElement pfe : pfes){
//			pfe.setDataType(inferrer.getType(pfe));
//		}
		
		return useCase;
	}
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj instanceof ParameterSource)) return null;
		
		DataType type = MamlHelper.getDataType((ParameterSource) obj);
		return type != null ? MamlHelper.getDataTypeName(type) : "??";
		
//		if(!(obj.eContainer() instanceof UseCase)) return "error";
//		
//		ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
//	
//		if(obj instanceof ProcessFlowElement){
//			DataType type = inferrer.getType((ProcessFlowElement) obj);
//			return type != null ? MamlHelper.getDataTypeName(type) : "??";
//		} else if(obj instanceof Attribute){
//			// TODO remove
//			DataTypeLiteral type = DynamicTypeLiteral.from(((Attribute) obj).getType().toString());
//			return type != null ? type.getName() : "??";
//		} 
//		return "??";
	}
    
	/** 
	 * Perform the data model inference without returning a value
	 * @param obj
	 */
	public void startInferenceProcess(EObject obj){
		// Automatically starts initial inference
		ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
	}
	
	private String[] getDataTypeList(EObject object){
		// Refresh inferred model types
		//startInferenceProcess(object);
		
		HashSet<String> set = new HashSet<String>();
		set.addAll(ModelInferenceDataTypeHelper.getPrimitiveDataTypesAsString());
		set.addAll(ModelInferenceDataTypeHelper.getInstance().getCustomDataTypesAsString());
		return set.toArray(new String[set.size()]);
	}
	
	public DataType openDataTypeSelectionWizard(EObject object){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		
		dialog.setElements(getDataTypeList(object));
		dialog.setTitle("Select desired data type");
		
		// user pressed cancel
		if (dialog.open() != Window.OK) {
			// Return previous value
			if(object instanceof Attribute) return ((Attribute) object).getType();
		}
		Object[] result = dialog.getResult();
		
		// Value given and does not start with invalid character?
		if(result.length > 0 && !((String) result[0]).startsWith("__")){
			return getDataType(object, (String) result[0]);
		}
		
		// Return previous value
		if(object instanceof Attribute) return ((Attribute) object).getType();
		return null; // Unknown error
	}
	
	private Object[] getAttributeList(ParameterSource source, Attribute skipMe){
		// Refresh inferred model types
		//startInferenceProcess(source);
		
		ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) source.eContainer());
		DataType type = inferrer.getDataTypeFromParameterSource(source);
		
		if(type instanceof CustomType) {
			return ((CustomType) type).getAttributes().stream().filter(MamlHelper.distinctByKey(elem -> elem.getName())).toArray();
		}
		
		return new Object[] {};
////		TypeStructureNode skipNode = new TypeStructureNode(skipMe.getDescription(), DynamicTypeLiteral.from(skipMe.getType().toString()), skipMe.getMultiplicity(), source);
//		Collection<Attribute> nodes = inferrer.getAttributesForType(type);
//		nodes.remove(skipMe);
//		
//		System.out.println(nodes);
//		
//		return nodes.stream().filter(MamlHelper.distinctByKey(elem -> ((TypeStructureNode) elem).getAttributeName())).toArray();
	}
	
	public String openAttributeSelectionWizard(EObject object){
		if(!(object instanceof Attribute)) return null;
		Attribute attribute = (Attribute) object;
		
		// Get source element
		Optional<Connector> connector = ((UseCase) ((Attribute) object).eContainer()).getProcessFlowConnections().stream()
			.filter(elem -> elem instanceof ParameterConnector && ((ParameterConnector) elem).getTargetElement().equals(object)).findFirst();
		if(!connector.isPresent()) {
			// No source -> impossible to set
			return attribute.getDescription();
		}
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ObjectListSelectionDialog dialog = new ObjectListSelectionDialog(shell, new LabelProvider());
		
		Object[] attributeList = getAttributeList(((ParameterConnector) connector.get()).getSourceElement(), attribute);
		if(attributeList.length == 0) return (attribute.getDescription()); // Skip, nothing to select
		
//TODO
//		dialog.setElements(attributeList, elem -> ((TypeStructureNode) elem).getAttributeName() + " (" + MamlHelper.getDataTypeName(((TypeStructureNode) elem).getType()) + ")");
//		dialog.setTitle("Select from known attribute names");
//		
//		// user pressed cancel
//		if (dialog.open() != Window.OK) {
//			// Return previous value
//			return attribute.getDescription();
//		}
//		Object[] result = dialog.getResult();
//		
//		// Value given?
//		if(result.length > 0 && result[0] instanceof TypeStructureNode){
//			// Set type and multiplicity
//			attribute.setType(((TypeStructureNode) result[0]).getType());//.getName());
//			attribute.setMultiplicity(((TypeStructureNode) result[0]).getMultiplicity());
//			
//			// Return attributeName
//			return ((TypeStructureNode) result[0]).getAttributeName();
//		}
		
		// Return previous value
		return attribute.getDescription();
	}

	public DataType getDataType(EObject obj, String input){
		try {
			//updateAllDataTypes(obj);
			ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
			return inferrer.getType(input);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DataType setDataType(EObject obj, String input){
		try {
//			updateAllDataTypes(obj);
			DataType type = getDataType(obj, input);
			
			if(!((UseCase) obj.eContainer()).getDataTypes().contains(type)){
				((UseCase) obj.eContainer()).getDataTypes().add(type);
			}
			return type;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
