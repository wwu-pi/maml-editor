package de.wwu.maml.inference;

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
import de.wwu.maml.dsl.mamlgui.Attribute;
import de.wwu.maml.editor.service.MamlHelper;

/**
 * Main component for inferring data models from .maml models 
 * @author c_rieg01
 *
 */
public class ModelInferrer {
	
	// Track last inference
	private Date lastInference = null;
	
	// Manages data types
	protected ModelInferenceDataTypeHelper inferenceDataTypeHelper = ModelInferenceDataTypeHelper.getInstance(); 
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
		// Prepare data types
		// ------------------------------------------------------------------
		
		// Reset type list and synchronize with use case
		inferenceDataTypeHelper.clearDataModel();
		inferenceDataTypeHelper.loadDataTypes(useCase);
		// TODO detect and remove unused data types in useCase.dataTypes
		
//		DynamicTypeLiteral.setReadOnly(readOnly);
		
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
		
		// Process remaining dangling element chains and infer subsequent elements
		Set<ProcessFlowElement> danglingElementStarts = toProcess.stream()
				.filter(elem -> (!start.isPresent() || !elem.equals(start.get())) && elem.getPreviousElements().size() == 0)
				.collect(Collectors.toSet());
		
		for(ProcessFlowElement elem : danglingElementStarts){
			inferenceDataTypeHelper.inferProcessFlowChain(elem);
		}
		
		// ------------------------------------------------------------------
		// Infer type relations
		// ------------------------------------------------------------------
		// Attributes attached to process flow elements
		for(ProcessFlowElement elem : useCase.getProcessFlowElements()){
			inferenceDataTypeHelper.inferAttributes(elem);
		}
		
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
		// merging necessary?? (rename to validation instead?)
		inferenceMergeHelper.mergeProcessElements(useCase.getProcessFlowElements(), inferenceDataTypeHelper.getTypeGraph());
		
		// Track the last time of inference for eventual throttling
		setLastInference(new Date());
		System.out.println(this + " NEW INFERENCE at " + getLastInference().getTime());
		
		// TODO remove unused data types from UseCase dataType list
		if(!readOnly){
			inferenceMergeHelper.createDataStructureInUseCase(useCase, inferenceDataTypeHelper.getTypeGraph());
		}
		
		// Output Helper
		System.out.println("Custom data types:" + inferenceDataTypeHelper.getCustomDataTypesAsString().toString());
	}

	/**
	 * Retrieve data type for given ProcessFlowElement.
	 * @param obj
	 * @return
	 */
	public DataType getType(ProcessFlowElement obj) {
		DataType type = MamlHelper.getDataType(obj);
		return type;
	}
	
	public DataType getType(String typeName){
		DataType type =  inferenceDataTypeHelper.getDataTypeInstance(MamlHelper.getAllowedDataTypeName(typeName));
		return type;
	}
	
	public Date getLastInference() {
		return lastInference;
	}

	public void setLastInference(Date lastInference) {
		this.lastInference = lastInference;
	}
	
//	public Collection<Attribute> getAttributesForType(DataType type){
//		return inferenceDataTypeHelper.getAttributesForType(type);
//	}
	
	public DataType getDataTypeFromParameterSource(ParameterSource source){
		return MamlHelper.getDataType(source);
	}
	
//	public DataType getDataTypeForAttributeName(DataType sourceType, String attributeName){
//		return inferenceDataTypeHelper.getDataTypeForAttributeName(sourceType, attributeName);
//	}

	// TODO validate model (no dangling, ...)
	// TODO build data model and validate data types
}
