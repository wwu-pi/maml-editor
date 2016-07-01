package de.wwu.md2dot0.design;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import md2dot0.DataSource;
import md2dot0.ProcessConnector;
import md2dot0.ProcessElement;
import md2dot0.ProcessFlowElement;
import md2dot0.ProcessStartEvent;
import md2dot0.Transform;
import md2dot0.UseCase;
import md2dot0data.CustomType;

public class ModelInferenceService {
	
	protected Map<ProcessFlowElement, String> elementTypes = new HashMap<ProcessFlowElement, String>();
	protected Set<String> customTypes = new HashSet<String>();
	protected Map<String, ProcessFlowElement> anonymousTypes = new HashMap<String, ProcessFlowElement>();
	
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj instanceof ProcessFlowElement)) return "error";
		
		startInferenceProcess((UseCase) obj.eContainer()); // Container is the use case itself
		
		String type = getType((ProcessFlowElement) obj);
		return type != null ? type : "??";
	}
	
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

	protected String getType(ProcessFlowElement obj) {
		return elementTypes.get(obj);
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
			Set<ProcessFlowElement> processed = inferProcessFlowChain(start.get());
			toProcess.remove(processed);
		}
		
		// Process remaining tangling element chains and infer subsequent elements
		Set<ProcessFlowElement> tanglingElementStarts = toProcess.stream()
				.filter(elem -> elem.getPreviousElements() == null)
				.collect(Collectors.toSet());
		
		for(ProcessFlowElement elem : tanglingElementStarts){
			inferProcessFlowChain(elem);
		}
		
		// TODO Merge process element inference pieces
	}

	protected Set<ProcessFlowElement> inferProcessFlowChain(ProcessFlowElement startElement) {
		Set<ProcessFlowElement> processed = new HashSet<ProcessFlowElement>();
		String lastOccurredType = null;
		
		// Recursively iterate through chain (control flow elements may have multiple followers)
		inferProcessFlowChainRecursive(startElement, lastOccurredType, processed);
		
		return processed;
	}
	
	protected void inferProcessFlowChainRecursive(ProcessFlowElement currentElement, String lastOccurredType, Set<ProcessFlowElement> processed) {
		// Skip if currentElement was already processed
		if(currentElement == null || processed.contains(currentElement)){
			return;
		}
		
		// Process current item
		lastOccurredType = inferSingleItem(currentElement, lastOccurredType);
		processed.add(currentElement);
		
		// Process subsequent connected items
		if(currentElement.getNextElements() != null && currentElement.getNextElements().size() > 0){
			// Control flow elements: may have >1 outgoing connections!
			for(ProcessConnector next : currentElement.getNextElements()){
				inferProcessFlowChainRecursive(next.getTargetProcessFlowElement(), lastOccurredType, processed);
			}
		}
	}
	
	protected String inferSingleItem(ProcessFlowElement processing, String lastOccurredType){
		if(processing instanceof DataSource){
			// Data Sources provide a type themselves (possibly a new one)
			String typeName = ((DataSource) processing).getTypeName();
			if(typeName != null){
				customTypes.add(typeName);
				lastOccurredType = typeName;
			}
			// In case no value is given, it must be the last known type
			elementTypes.put(processing, lastOccurredType);
			
		} else if(processing instanceof Transform){
			// Special case because type changes
			String typeName = ((Transform) processing).getDataType() != null ? ((CustomType) ((Transform) processing).getDataType()).getName() : null;
			if(typeName != null){
				customTypes.add(typeName);
			}
			// In case no type is given, we cannot infer anything
			lastOccurredType = typeName;
			elementTypes.put(processing, typeName);
			
		} else if(processing instanceof ProcessElement){
			// If a type is explicitly set then use it, else use previous known type
			String typeName = ((ProcessElement) processing).getDataType() != null ? ((CustomType) ((ProcessElement) processing).getDataType()).getName() : null;
			if(typeName != null && !typeName.equals("X")){
				customTypes.add(typeName);
				lastOccurredType = typeName;
			} else {
				// Build a new and unique custom type name
				String newAnonType = "ANONYMOUS__" + processing.toString();
				customTypes.add(newAnonType);
				anonymousTypes.put(newAnonType, processing);
				lastOccurredType = newAnonType;
			}
			elementTypes.put(processing, lastOccurredType);
			
		}
		// Do nothing for events and control flows as they have no proper type
		
		return lastOccurredType;
	}
	
	// TODO validate model (no tangling, ...)
	// TODO build data model and validate data types
}
