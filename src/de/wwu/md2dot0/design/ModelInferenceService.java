package de.wwu.md2dot0.design;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import de.wwu.md2dot0.inference.ModelInferenceDataTypeHelper;
import md2dot0.ProcessFlowElement;
import md2dot0.ProcessStartEvent;
import md2dot0.UseCase;

public class ModelInferenceService {
	
	protected ModelInferenceDataTypeHelper inferenceDataTypeHelper = new ModelInferenceDataTypeHelper(); 
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj instanceof ProcessFlowElement)) return "error";
		
		startInferenceProcess((UseCase) obj.eContainer()); // Container is the use case itself
		
		String type = inferenceDataTypeHelper.getType((ProcessFlowElement) obj);
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
		
		startInferenceProcess((UseCase) useCase.get());
	}


	
	/**
	 * Main method to trigger a full inference process of the process flow(!!).
	 *  
	 * @param useCase
	 */
	protected void startInferenceProcess(UseCase useCase) {
		// Inference needed for all process elements
		Set<ProcessFlowElement> toProcess = new HashSet<ProcessFlowElement>(useCase.getProcessFlowElements());
		
		// Select main process (using start event) and infer for following elements
		Optional<ProcessStartEvent> start = toProcess.stream()
				.filter(elem -> elem instanceof ProcessStartEvent)
				.map(elem -> (ProcessStartEvent) elem)
				.findFirst();
		
		if(start.isPresent()){
			Set<ProcessFlowElement> processed = inferenceDataTypeHelper.inferProcessFlowChain(start.get());
			toProcess.remove(processed);
		}
		
		// Process remaining tangling element chains and infer subsequent elements
		Set<ProcessFlowElement> tanglingElementStarts = toProcess.stream()
				.filter(elem -> elem.getPreviousElements() == null)
				.collect(Collectors.toSet());
		
		for(ProcessFlowElement elem : tanglingElementStarts){
			inferenceDataTypeHelper.inferProcessFlowChain(elem);
		}
		
		// TODO Merge process element inference pieces
	}

	
	
	// TODO validate model (no tangling, ...)
	// TODO build data model and validate data types
}
