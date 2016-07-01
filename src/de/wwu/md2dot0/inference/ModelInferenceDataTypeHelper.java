package de.wwu.md2dot0.inference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import md2dot0.DataSource;
import md2dot0.ProcessConnector;
import md2dot0.ProcessElement;
import md2dot0.ProcessFlowElement;
import md2dot0.Transform;
import md2dot0data.CustomType;

public class ModelInferenceDataTypeHelper {
	protected Map<ProcessFlowElement, String> elementTypes = new HashMap<ProcessFlowElement, String>();
	protected Set<String> customTypes = new HashSet<String>();
	protected Map<String, ProcessFlowElement> anonymousTypes = new HashMap<String, ProcessFlowElement>();
	
	/**
	 * Retrieve data type for given ProcessFlowElement
	 * @param obj
	 * @return
	 */
	public String getType(ProcessFlowElement obj) {
		return elementTypes.get(obj);
	}
	
	/**
	 * Initial call to infer the main data type for a sequence of ProcessFlowElements.
	 * @param startElement
	 * @return
	 */
	public Set<ProcessFlowElement> inferProcessFlowChain(ProcessFlowElement startElement) {
		Set<ProcessFlowElement> processed = new HashSet<ProcessFlowElement>();
		String lastOccurredType = null;
		
		// Recursively iterate through chain (control flow elements may have multiple followers)
		inferProcessFlowChainRecursive(startElement, lastOccurredType, processed);
		
		return processed;
	}
	
	/**
	 * Recursive call to infer the main data type for a sequence of ProcessFlowElements. 
	 * @param currentElement
	 * @param lastOccurredType
	 * @param processed
	 */
	public void inferProcessFlowChainRecursive(ProcessFlowElement currentElement, String lastOccurredType, Set<ProcessFlowElement> processed) {
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
	
	/**
	 * Infer the main data type for an individual ProcessFlowElement.
	 * @param processing
	 * @param lastOccurredType
	 * @return
	 */
	public String inferSingleItem(ProcessFlowElement processing, String lastOccurredType){
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
			// If a type is explicitly set (anonymous or not) then use it, else use previous known type
			String typeName = ((ProcessElement) processing).getDataType() != null ? ((CustomType) ((ProcessElement) processing).getDataType()).getName() : null;
			if(typeName != null && !typeName.equals("X")){
				customTypes.add(typeName);
				lastOccurredType = typeName;
			} else if(typeName != null && typeName.equals("X")){
				// Build a new and unique custom type name
				String newAnonType = "ANONYMOUS__" + processing.toString();
				customTypes.add(newAnonType);
				anonymousTypes.put(newAnonType, processing);
				lastOccurredType = newAnonType;
			} 
			// In case no value is given, it must be the last known type
			elementTypes.put(processing, lastOccurredType);
		}
		// TODO Webservice erzeugt ggf. neuen Typ?
		
		// Do nothing for events and control flows as they have no proper type
		
		return lastOccurredType;
	}
}
