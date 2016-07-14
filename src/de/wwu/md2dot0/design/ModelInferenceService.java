package de.wwu.md2dot0.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import de.wwu.md2dot0.inference.ModelInferrer;
import de.wwu.md2dot0.inference.ModelInferrerManager;
import de.wwu.md2dot0.inference.DynamicTypeLiteral;
import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;
import md2dot0data.DataTypeLiteral;
import md2dot0gui.Attribute;

public class ModelInferenceService {
	// Central inference component
	ModelInferrer inferrer;
	
	/**
	 * Trigger inference process and visual update of elements.
	 * @param obj
	 */
	public EObject updateDataType(EObject obj){
		if(!(obj.eContainer() instanceof UseCase)) return null;
		
		UseCase useCase = (UseCase) obj.eContainer();
		inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
		inferrer.startInferenceProcess(useCase); // Container is the use case itself
		
		
		//getDataTypeRepresentation(obj);
		
		// TODO better logic: Diff for changed elements only
		Collection<ProcessFlowElement> pfes = ((UseCase) obj.eContainer()).getProcessFlowElements();
		for(ProcessFlowElement pfe : pfes){
		//	pfe.setChanged(!pfe.isChanged()); // Artificial update that triggers redraw
			pfe.setDataType(inferrer.getType(pfe));
		}

		return obj;
	}
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj.eContainer() instanceof UseCase)) return "error";
		
		UseCase useCase = (UseCase) obj.eContainer();
		inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
		inferrer.startInferenceProcess(useCase); // Container is the use case itself
		
		if(obj instanceof ProcessFlowElement){
			DataTypeLiteral type = inferrer.getType((ProcessFlowElement) obj);
			return type != null ? type.toString() : "??";
		} else if(obj instanceof Attribute){
			DataTypeLiteral type = DynamicTypeLiteral.from(((Attribute) obj).getType());
			return type != null ? type.toString() : "??";
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
	
	/**
	 * Polymorphism helper in case multiple use cases are passed to infer.
	 * TODO: Currently only first use case is considered, needs to be extended to infer multiple UC and merge them
	 * @param obj
	 */
	public void startInferenceProcess(Collection<? extends EObject> obj){
		Optional<UseCase> useCase = obj.stream()
				.filter(elem -> elem instanceof UseCase)
				.map(elem -> (UseCase) elem)
				.findFirst();
		
		if(!useCase.isPresent()){
			return;
		}
		
		inferrer = ModelInferrerManager.getInstance().getModelInferrer(useCase.get());
		inferrer.startInferenceProcess((UseCase) useCase.get());
	}
	
	public String[] getDataTypeList(EObject object){
		// Refresh inferred model types
		startInferenceProcess(object);
		
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
			System.out.println(result[0]);
			return (String) result[0];
		}
		
		// Return previous value
		if(object instanceof Attribute) return ((Attribute) object).getType();
		return null; // Unknown error
	}
}
