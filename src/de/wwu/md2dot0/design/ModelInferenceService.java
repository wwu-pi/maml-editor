package de.wwu.md2dot0.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import de.wwu.md2dot0.inference.ModelInferrer;
import de.wwu.md2dot0.inference.ModelInferrerManager;
import de.wwu.md2dot0.inference.TypeStructureNode;
import de.wwu.md2dot0.dialog.ObjectListSelectionDialog;
import de.wwu.md2dot0.inference.DynamicTypeLiteral;
import md2dot0.Connector;
import md2dot0.ParameterConnector;
import md2dot0.ParameterSource;
import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;
import md2dot0data.DataType;
import md2dot0data.DataTypeLiteral;
import md2dot0gui.Attribute;

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
		
		// No problem of resetting everything: View elements are only updated for actually changed types
		Collection<ProcessFlowElement> pfes = useCase.getProcessFlowElements();
		for(ProcessFlowElement pfe : pfes){
			pfe.setDataType(inferrer.getType(pfe));
		}

//		initialInference = true;
		
		return useCase;
	}
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj.eContainer() instanceof UseCase)) return "error";
		
		ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
	
		if(obj instanceof ProcessFlowElement){
			DataTypeLiteral type = inferrer.getType((ProcessFlowElement) obj);
			return type != null ? type.getName() : "??";
		} else if(obj instanceof Attribute){
			DataTypeLiteral type = DynamicTypeLiteral.from(((Attribute) obj).getType());
			return type != null ? type.getName() : "??";
		} 
		return "??";
	}
    
	/** 
	 * Perform the data model inference without returning a value
	 * @param obj
	 */
	public void startInferenceProcess(EObject obj){
		getDataTypeRepresentation(obj);
	}
	
	private String[] getDataTypeList(EObject object){
		// Refresh inferred model types
		//startInferenceProcess(object);
		
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(DynamicTypeLiteral.getPrimitiveDataTypesAsString());
		list.addAll(DynamicTypeLiteral.getCustomDataTypesAsString());
		return list.toArray(new String[list.size()]);
	}
	
	public String openDataTypeSelectionWizard(EObject object){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		
		dialog.setElements(getDataTypeList(object)); //new String[] { "Linux", "Mac", "Windows" });
		dialog.setTitle("Select desired data type");
		
		// user pressed cancel
		if (dialog.open() != Window.OK) {
			// Return previous value
			if(object instanceof Attribute) return ((Attribute) object).getType();
		}
		Object[] result = dialog.getResult();
		
		// Value given and does not start with invalid character?
		if(result.length > 0 && !((String) result[0]).startsWith("__")){
			return (String) result[0];
		}
		
		// Return previous value
		if(object instanceof Attribute) return ((Attribute) object).getType();
		return null; // Unknown error
	}
	
	private Object[] getAttributeList(ParameterSource source){
		// Refresh inferred model types
		//startInferenceProcess(source);
		
		ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) source.eContainer());
		DataType type = inferrer.getDataTypeFromParameterSource(source);
		Collection<TypeStructureNode> nodes = inferrer.getAttributesForType(type);
		
		System.out.println(nodes);
		
		return nodes.stream().filter(Md2dot0Helper.distinctByKey(elem -> ((TypeStructureNode) elem).getAttributeName())).toArray();
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
		
		Object[] attributeList = getAttributeList(((ParameterConnector) connector.get()).getSourceElement());
		if(attributeList.length == 0) return (attribute.getDescription()); // Skip, nothing to select
		
		dialog.setElements(attributeList, elem -> ((TypeStructureNode) elem).getAttributeName());
		dialog.setTitle("Select from known attribute names");
		
		// user pressed cancel
		if (dialog.open() != Window.OK) {
			// Return previous value
			return attribute.getDescription();
		}
		Object[] result = dialog.getResult();
		
		// Value given?
		if(result.length > 0 && result[0] instanceof TypeStructureNode){
			// Set type
			for(Object o: attributeList){
				if(((TypeStructureNode) o).getAttributeName().equals(((TypeStructureNode) result[0]).getAttributeName())){
					attribute.setType(((TypeStructureNode) o).getType().getName());
				}
			}
			// Return attributeName
			return ((TypeStructureNode) result[0]).getAttributeName();
		}
		
		// Return previous value
		return attribute.getDescription();
	}
}
