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
import de.wwu.md2dot0.inference.TypeLiteral;
import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;
import md2dot0gui.Attribute;

public class ModelInferenceService {
	// Central inference component
	ModelInferrer inferrer = new ModelInferrer();
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj instanceof ProcessFlowElement)) return "error";
		
		inferrer.startInferenceProcess((UseCase) obj.eContainer()); // Container is the use case itself
		
		String type = inferrer.getType((ProcessFlowElement) obj);
		return type != null ? type : "??";
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
		
		inferrer.startInferenceProcess((UseCase) useCase.get());
	}
	
	public String[] getDataTypeList(){
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(TypeLiteral.getPrimitiveDataTypesAsString());
		list.addAll(TypeLiteral.getCustomDataTypesAsString());
		return list.toArray(new String[list.size()]);
	}
	
//	public EObject instantiateEcoreStringWrapper(String string){
//		md2dot0data.String wrapper = Md2dot0dataFactory.eINSTANCE.createString();
//		wrapper.setValue(string);
//		return wrapper;
//	}
	
	public String openDataTypeSelectionWizard(EObject object){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		
		dialog.setElements(getDataTypeList()); //new String[] { "Linux", "Mac", "Windows" });
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
