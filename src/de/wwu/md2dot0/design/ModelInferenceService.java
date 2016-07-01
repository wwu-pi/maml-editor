package de.wwu.md2dot0.design;

import java.util.Collection;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;

import de.wwu.md2dot0.inference.ModelInferrer;
import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;

public class ModelInferenceService {
	// Inference component
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
}
