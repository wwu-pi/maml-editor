package de.wwu.md2dot0.inference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import md2dot0.ParameterConnector;
import md2dot0.ProcessFlowElement;
import md2dot0.ProcessStartEvent;
import md2dot0.UseCase;
import md2dot0gui.Attribute;

/**
 * Main component for inferring data models from .md2dot0 models 
 * @author c_rieg01
 *
 */
public class ModelInferrer {
	
	// Manages data types
	protected ModelInferenceDataTypeHelper inferenceDataTypeHelper = new ModelInferenceDataTypeHelper(); 
	protected ModelInferenceMergeHelper inferenceMergeHelper = new ModelInferenceMergeHelper();
	
	/**
	 * Main method to trigger a full inference process of the process flow(!!).
	 *  
	 * @param useCase
	 */
	public void startInferenceProcess(UseCase useCase) {
		// Inference needed for all process elements
		Set<ProcessFlowElement> toProcess = new HashSet<ProcessFlowElement>(useCase.getProcessFlowElements());
		
		// ------------------------------------------------------------------
		// Select main process (using start event) and infer for following elements
		// ------------------------------------------------------------------
		Optional<ProcessStartEvent> start = toProcess.stream()
				.filter(elem -> elem instanceof ProcessStartEvent)
				.map(elem -> (ProcessStartEvent) elem)
				.findFirst();
		
		if(start.isPresent()){
			Set<ProcessFlowElement> processed = inferenceDataTypeHelper.inferProcessFlowChain(start.get());
			toProcess.removeAll(processed);
		}
		
		// Process remaining tangling element chains and infer subsequent elements
		Set<ProcessFlowElement> tanglingElementStarts = toProcess.stream()
				.filter(elem -> !(elem instanceof ProcessStartEvent) && elem.getPreviousElements().size() == 0)
				.collect(Collectors.toSet());
		
		for(ProcessFlowElement elem : tanglingElementStarts){
			inferenceDataTypeHelper.inferProcessFlowChain(elem);
		}
		
		// TODO infer data types from attributes
		
		// ------------------------------------------------------------------
		// Infer attributes
		// ------------------------------------------------------------------
		for(ProcessFlowElement elem : useCase.getProcessFlowElements()){
			inferenceDataTypeHelper.inferAttributes(elem);
		}

		// Process remaining tangling attributes
		Set<Attribute> connectedAttributes = useCase.eContents().stream()
				.filter(elem -> (elem instanceof ParameterConnector) && ((ParameterConnector) elem).getTargetElement() instanceof Attribute)
				.map(elem -> (Attribute) ((ParameterConnector) elem).getTargetElement())
				.collect(Collectors.toSet());
		
		Set<Attribute> tanglingAttributes = useCase.eContents().stream()
				.filter(elem -> (elem instanceof Attribute))
				.map(elem -> (Attribute) elem)
				.collect(Collectors.toSet());
		tanglingAttributes.removeAll(connectedAttributes);
		
				
		// ------------------------------------------------------------------
		// Merge individual process elements within a use case
		// ------------------------------------------------------------------
		inferenceMergeHelper.mergeProcessElements(useCase.getProcessFlowElements(), inferenceDataTypeHelper);
		
		// Output Helper
		System.out.println("Custom data types:" + getAllCustomTypes().toString());
		System.out.println("Anonymous types:" + getAllCustomTypes().toString());
	}

	/**
	 * Retrieve data type for given ProcessFlowElement.
	 * @param obj
	 * @return
	 */
	public String getType(ProcessFlowElement obj) {
		// Pass to data type helper
		return inferenceDataTypeHelper.getType(obj);
	}
	
	public Set<String> getAllCustomTypes(){
		return inferenceDataTypeHelper.customTypes.keySet();
	}
	// TODO validate model (no tangling, ...)
	// TODO build data model and validate data types

	public Collection<String> getAllPrimitiveTypes() {
		return inferenceDataTypeHelper.getPrimitiveDataTypes();
	}
}
