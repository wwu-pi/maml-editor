package de.wwu.maml.inference;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.wwu.maml.dsl.maml.ParameterConnector;
import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.ProcessFlowElement;
import de.wwu.maml.dsl.maml.ProcessStartEvent;
import de.wwu.maml.dsl.maml.UseCase;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.dsl.mamldata.DataTypeLiteral;
import de.wwu.maml.dsl.mamlgui.Attribute;

/**
 * Main component for inferring data models from .md2dot0 models 
 * @author c_rieg01
 *
 */
public class ModelInferrer {
	
	// Track last inference
	private Date lastInference = null;
	
	// Manages data types
	protected ModelInferenceDataTypeHelper inferenceDataTypeHelper = new ModelInferenceDataTypeHelper(); 
	protected ModelInferenceMergeHelper inferenceMergeHelper = new ModelInferenceMergeHelper();
	
	/**
	 * Main method to trigger a full inference process of the process flow(!!).
	 *  
	 * @param useCase
	 */
	public void startInferenceProcess(UseCase useCase, boolean readOnly) {
		// Inference needed for all process elements
		Set<ProcessFlowElement> toProcess = new HashSet<ProcessFlowElement>(useCase.getProcessFlowElements());
		
		// ------------------------------------------------------------------
		// Reset type list and attribute structure
		// ------------------------------------------------------------------
		inferenceDataTypeHelper.clearDataModel();
		
		DynamicTypeLiteral.setReadOnly(readOnly);
		
		// ------------------------------------------------------------------
		// Select main process (using start event) and infer for following elements (and attributes)
		// ------------------------------------------------------------------
		Optional<ProcessStartEvent> start = toProcess.stream()
				.filter(elem -> elem instanceof ProcessStartEvent)
				.map(elem -> (ProcessStartEvent) elem)
				.findFirst();
		
		if(start.isPresent()){
			Set<ProcessFlowElement> processed = inferenceDataTypeHelper.inferProcessFlowChain(start.get());
			toProcess.removeAll(processed);
		}
		
		// Process remaining dangling element chains and infer subsequent elements
		Set<ProcessFlowElement> danglingElementStarts = toProcess.stream()
				.filter(elem -> (!start.isPresent() || !elem.equals(start.get())) && elem.getPreviousElements().size() == 0)
				.collect(Collectors.toSet());
		
		for(ProcessFlowElement elem : danglingElementStarts){
			inferenceDataTypeHelper.inferProcessFlowChain(elem);
		}
		
		// ------------------------------------------------------------------
		// Infer attributes
		// ------------------------------------------------------------------
		// Attributes attached to process flow elements have already been inferred

		// Process remaining dangling attributes
		Set<Attribute> connectedAttributes = useCase.eContents().stream()
				.filter(elem -> (elem instanceof ParameterConnector) && ((ParameterConnector) elem).getTargetElement() instanceof Attribute)
				.map(elem -> (Attribute) ((ParameterConnector) elem).getTargetElement())
				.collect(Collectors.toSet());
		
		Set<Attribute> danglingAttributes = useCase.eContents().stream()
				.filter(elem -> (elem instanceof Attribute))
				.map(elem -> (Attribute) elem)
				.collect(Collectors.toSet());
		danglingAttributes.removeAll(connectedAttributes);
		
		for(ParameterSource elem : danglingAttributes){
			inferenceDataTypeHelper.inferAttributes(elem);
		}
				
		// ------------------------------------------------------------------
		// Merge individual process elements within a use case
		// ------------------------------------------------------------------
		inferenceMergeHelper.mergeProcessElements(useCase.getProcessFlowElements(), inferenceDataTypeHelper);
		
		// Track the last time of inference for eventual throttling
		setLastInference(new Date());
		System.out.println(this + " NEW INFERENCE at " + getLastInference().getTime());
		
		// TODO remove unused data types from UseCase dataType list
		
		// Output Helper
		System.out.println("Custom data types:" + DynamicTypeLiteral.getCustomDataTypesAsString().toString());
		System.out.println("Anonymous data types:" + DynamicTypeLiteral.getAnonymousDataTypesAsString().toString());
	}

	/**
	 * Retrieve data type for given ProcessFlowElement.
	 * @param obj
	 * @return
	 */
	public DataTypeLiteral getType(ProcessFlowElement obj) {
		// Pass to data type helper
		DataTypeLiteral type = inferenceDataTypeHelper.getType(obj);
		return type;
	}
	
	public Date getLastInference() {
		return lastInference;
	}

	public void setLastInference(Date lastInference) {
		this.lastInference = lastInference;
	}
	
	public Collection<TypeStructureNode> getAttributesForType(DataType type, TypeStructureNode skipNode){
		return inferenceDataTypeHelper.getAttributesForType(type, skipNode);
	}
	
	public DataType getDataTypeFromParameterSource(ParameterSource source){
		return inferenceDataTypeHelper.getDataTypeFromParameterSource(source);
	}
	
	public DataType getDataTypeForAttributeName(DataType sourceType, String attributeName){
		return inferenceDataTypeHelper.getDataTypeForAttributeName(sourceType, attributeName);
	}
	
	// TODO validate model (no dangling, ...)
	// TODO build data model and validate data types
}
