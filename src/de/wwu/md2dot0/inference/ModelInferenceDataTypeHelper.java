package de.wwu.md2dot0.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import md2dot0.DataSource;
import md2dot0.ProcessConnector;
import md2dot0.ProcessElement;
import md2dot0.ProcessFlowElement;
import md2dot0.Transform;
import md2dot0data.AnonymousType;
import md2dot0data.CustomType;
import md2dot0data.DataType;
import md2dot0data.Md2dot0dataFactory;
import md2dot0data.Multiplicity;
import md2dot0gui.Attribute;

public class ModelInferenceDataTypeHelper {
	// TODO bidirectional map?
	protected Map<ProcessFlowElement, String> elementTypes = new HashMap<ProcessFlowElement, String>();
	protected Map<String, CustomType> customTypes = new HashMap<String, CustomType>();
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
				CustomType type = Md2dot0dataFactory.eINSTANCE.createCustomType();
				type.setName(typeName);
				customTypes.put(typeName, type);
				lastOccurredType = typeName;
			}
			// In case no value is given, it must be the last known type
			elementTypes.put(processing, lastOccurredType);
			
		} else if(processing instanceof Transform){
			// Special case because type changes
			String typeName = ((Transform) processing).getDataType() != null ? ((CustomType) ((Transform) processing).getDataType()).getName() : null;
			if(typeName != null){
				CustomType type = Md2dot0dataFactory.eINSTANCE.createCustomType();
				type.setName(typeName);
				customTypes.put(typeName, type);
			}
			// In case no type is given, we cannot infer anything
			lastOccurredType = typeName;
			elementTypes.put(processing, typeName);
			
		} else if(processing instanceof ProcessElement){
			// If a type is explicitly set (anonymous or not) then use it, else use previous known type
			String typeName = ((ProcessElement) processing).getDataType() != null ? ((CustomType) ((ProcessElement) processing).getDataType()).getName() : null;
			if(typeName != null && !typeName.equals("X")){
				CustomType type = Md2dot0dataFactory.eINSTANCE.createCustomType();
				type.setName(typeName);
				customTypes.put(typeName, type);
				lastOccurredType = typeName;
			} else if(typeName != null && typeName.equals("X")){
				// Build a new and unique custom type name
				String newAnonType = "ANONYMOUS__" + processing.toString();
				AnonymousType type = Md2dot0dataFactory.eINSTANCE.createAnonymousType();
				type.setName(newAnonType);
				customTypes.put(newAnonType, type);
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
	
	public void inferAttributes(ProcessFlowElement processing){
		// Get (only) attached attributes
		Collection<Attribute> params = processing.getParameters().stream()
				.map(elem -> elem.getTargetElement())
				.filter(elem -> elem instanceof Attribute)
				.map(elem -> (Attribute) elem)
				.collect(Collectors.toList());
		
		if(processing instanceof ProcessElement){
			String typeName = elementTypes.get(processing);
			
			// Only continue attribute inference if it is not a basic type 
			if(typeName == null || !customTypes.containsKey(typeName)) return;

			// Go through attributes
			ArrayList<DataType> linkedAttributes = new ArrayList<DataType>(); 
			for(Attribute param : params){ 
				if(param.getMultiplicity().equals(Multiplicity.ONE)){
					// Consider related attribute directly
					linkedAttributes.add(param.getType()); // TODO Overhead beim mergen auf neue Struktur?					
				} else {
					// Create collection to add with nested type
					md2dot0data.Collection collection = Md2dot0dataFactory.eINSTANCE.createCollection();
					collection.getValues().add(param.getType());
					collection.setMultiplicity(param.getMultiplicity());
					linkedAttributes.add(collection);
				}
			}
			
			// Add attributes to type
			customTypes.get(typeName).getAttributes().addAll(linkedAttributes);
			
		} else if(processing instanceof Transform){
			// TODO
		}
		// TODO Control flows 
		// TODO Webservice?
		
		// Do nothing for datasource and events as they have no attributes
	}
}
