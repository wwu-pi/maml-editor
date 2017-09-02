package de.wwu.maml.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.wwu.maml.dsl.maml.DataSource;
import de.wwu.maml.dsl.maml.Event;
import de.wwu.maml.dsl.maml.ParameterConnector;
import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.ProcessConnector;
import de.wwu.maml.dsl.maml.ProcessElement;
import de.wwu.maml.dsl.maml.ProcessFlowElement;
import de.wwu.maml.dsl.maml.Transform;
import de.wwu.maml.dsl.maml.UseCase;
import de.wwu.maml.dsl.mamldata.CustomType;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.dsl.mamldata.DataTypeLiteral;
import de.wwu.maml.dsl.mamldata.Enum;
import de.wwu.maml.dsl.mamldata.MamldataFactory;
import de.wwu.maml.dsl.mamldata.Multiplicity;
import de.wwu.maml.dsl.mamldata.Property;
import de.wwu.maml.dsl.mamldata.impl.CustomTypeImpl;
import de.wwu.maml.dsl.mamlgui.Attribute;
import de.wwu.maml.dsl.mamlgui.ComputationOperator;
import de.wwu.maml.dsl.mamlgui.GUIElement;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;

public class ModelInferenceDataTypeHelper {
	
	protected Map<ParameterSource, DataTypeLiteral> elementTypes = new HashMap<ParameterSource, DataTypeLiteral>(); // TODO bidirectional map?
	protected ArrayList<TypeStructureNode> typeGraph = new ArrayList<TypeStructureNode>();

	/**
	 * Retrieve data type for given ProcessFlowElement
	 * @param obj
	 * @return
	 */
	public DataTypeLiteral getType(ParameterSource obj) {
		return elementTypes.get(obj);
	}
	
	/**
	 * Initial call to infer the main data type for a sequence of ProcessFlowElements.
	 * @param startElement
	 * @return
	 */
	public Set<ProcessFlowElement> inferProcessFlowChain(ProcessFlowElement startElement) {
		Set<ProcessFlowElement> processed = new HashSet<ProcessFlowElement>();
		DataTypeLiteral lastOccurredType = null;
		
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
	public void inferProcessFlowChainRecursive(ProcessFlowElement currentElement, DataTypeLiteral lastOccurredType, Set<ProcessFlowElement> processed) {
		// Skip if currentElement was already processed
		if(currentElement == null || processed.contains(currentElement)){
			return;
		}
		
		// Process current item
		lastOccurredType = inferSingleItem(currentElement, lastOccurredType);
		processed.add(currentElement);
		
		// Process attributes of current item
		inferAttributes(currentElement);
		
		// Process subsequent connected items
		if(currentElement.getNextElements() != null && currentElement.getNextElements().size() > 0){
			// Control flow elements: may have >1 outgoing connections!
			for(ProcessConnector next : currentElement.getNextElements()){
				inferProcessFlowChainRecursive(next.getTargetProcessFlowElement(), lastOccurredType, processed);
			}
		}
		
		// In addition check none/single/multi events
		if(currentElement instanceof ProcessElement){
			for(Event event : ((ProcessElement) currentElement).getEvents()){
				for(ProcessConnector next : event.getNextElements()){
					inferProcessFlowChainRecursive(next.getTargetProcessFlowElement(), lastOccurredType, processed);
				}
			}
		}
	}
	
	/**
	 * Infer the main data type for an individual ProcessFlowElement.
	 * @param processing
	 * @param lastOccurredType
	 * @return
	 */
	public DataTypeLiteral inferSingleItem(ProcessFlowElement processing, DataTypeLiteral lastOccurredType){
		String lastOccuredTypeName = lastOccurredType != null ? lastOccurredType.getIdentifier() : "";
		
		if(processing instanceof DataSource){
			// Data Sources provide a type themselves (possibly a new one)
			String typeName = ((DataSource) processing).getTypeName();
			if(typeName != null){
				lastOccuredTypeName = typeName;
			}
			// In case no value is given, it must be the last known type
			elementTypes.put(processing, DynamicTypeLiteral.from(lastOccuredTypeName));
			
		} else if(processing instanceof Transform){ 
			// Special case for Transform elements because type changes, MUST BE BEFORE upcoming ProcessElement superclass type
			
			// Check existing types for valid attributes
			DataTypeLiteral targetType = ModelInferenceTextInputHelper.getTypeForTransform(((Transform) processing).getDescription(), lastOccurredType, typeGraph, elementTypes);

			if(targetType != null){
				elementTypes.put(processing, targetType);
				lastOccuredTypeName = targetType.getIdentifier();
			} else {
				// Else inference failed -> no type information possible
				lastOccuredTypeName = null;
			} 
			
		} else if(processing instanceof ProcessElement){
			// If a type is explicitly set (anonymous or not) then use it, else use previous known type
			String typeName = ((ProcessElement) processing).getDataType() != null && (processing.getDataType() instanceof CustomType) ? ((CustomType) ((ProcessElement) processing).getDataType()).getName() : null;
			if(typeName != null && !typeName.equals("X")){
				lastOccuredTypeName = typeName;
			} else if(typeName != null && typeName.equals("X")){
				// Build a new and unique custom type name
				lastOccuredTypeName = DynamicTypeLiteral.ANONYMOUS_PREFIX + processing.toString();
			} 
			// In case no value is given, it must be the last known type
			elementTypes.put(processing, DynamicTypeLiteral.from(lastOccuredTypeName));
		}
		// TODO Webservice erzeugt ggf. neuen Typ?
		//TODO enum
		// Do nothing for events and control flows as they have no proper type
		
		return DynamicTypeLiteral.from(lastOccuredTypeName);
	}
	
	/**
	 * Infer additional data types from the graph of linked attributes.
	 * 
	 * @param source
	 */
	public void inferAttributes(ParameterSource source){
		// Pass to real inference method that can handle transitive attributes without 
		// direct connection such as (ProcessFlowElement)->(Sum)->(Attribute).  
		inferTransitiveAttributes(source, source);
	}
	
	/**
	 * Main method to infer attribute types. In addition to the ParameterSource to which the
	 * detected attributes should be related a second parameter specifies the current position 
	 * from which to find new attributes from.
	 * This is especially relevant for computed attributes that have no element in the type
	 * structure but potentially connect to further attributes.
	 * 
	 * @param source
	 * @param transitiveSource
	 */
	public void inferTransitiveAttributes(ParameterSource source, ParameterSource transitiveSource){
		// Process all connected attributes
		for(ParameterConnector connector : transitiveSource.getParameters()){
			// Check that target is a concrete Attribute
			if(connector.getTargetElement() instanceof Attribute){
			
				Attribute target = (Attribute) connector.getTargetElement();
				
				// Check that target has a non-anonymous type
				if(!DynamicTypeLiteral.isAllowedTypeName(target.getType().toString())) continue;
				
				// Process current connection
				DataTypeLiteral targetType = DynamicTypeLiteral.from(target.getType().toString());
				TypeStructureNode node = new TypeStructureNode(target.getDescription(), targetType, target.getMultiplicity(), source);
				if(source instanceof Attribute){
					// Keep track of source data types (only for non-PE as they are already tracked)
					elementTypes.put(source, DynamicTypeLiteral.from(((Attribute) source).getType().toString()));
				}
				typeGraph.add(node);
				
				// Process attached attributes if current source is not a primitive type
				if(targetType != null && !targetType.isPrimitive()){
					inferAttributes(target);
				}
			} else if(connector.getTargetElement() instanceof ComputationOperator){
				// Infer attribute for Operator but relate to original source in type structure 
				inferTransitiveAttributes(transitiveSource, connector.getTargetElement());
			}
		}
	}
	
	/**
	 * Remove all known data type mappings and reset attribute graph structure.
	 */
	public void clearDataModel(){
		this.elementTypes.clear();
		this.typeGraph.clear();
	}
	
	/**
	 * Retrieve all attributes for a specific data type
	 */
	public Collection<TypeStructureNode> getAttributesForType(DataType type, TypeStructureNode skipNode){
		// Either ProcessFlowElement -> compare type with target type
		// Or GUIElement -> get type from String and compare
		return this.typeGraph.stream().filter(elem -> !elem.equals(skipNode))
				.filter(elem -> ((elem.getSource() instanceof ProcessFlowElement) && ((ProcessFlowElement) elem.getSource()).getDataType().equals(type)) 
				|| ((elem.getSource() instanceof GUIElement) && DynamicTypeLiteral.from(((GUIElement) elem.getSource()).getType().toString()).equals(type)))
		.collect(Collectors.toList());
	}
	
	public DataType getDataTypeFromParameterSource(ParameterSource source){
		if(source instanceof ProcessFlowElement) {
			return ((ProcessFlowElement) source).getDataType();
		} else if(source instanceof GUIElement) {
			return DynamicTypeLiteral.from(((GUIElement) source).getType().toString());
		}
		return null;
	}
	
	public DataType getDataTypeForAttributeName(DataType sourceType, String attributeName){
		Optional<TypeStructureNode> node = this.typeGraph.stream().filter(elem -> elem.getAttributeName().equals(attributeName))
				.filter(elem -> ((elem.getSource() instanceof ProcessFlowElement) && ((ProcessFlowElement) elem.getSource()).getDataType().equals(sourceType)) 
				|| ((elem.getSource() instanceof GUIElement) && DynamicTypeLiteral.from(((GUIElement) elem.getSource()).getType().toString()).equals(sourceType)))
				.findFirst();
		
		return node.isPresent() ? node.get().getType() : null;
	}
	
//	public DataType getDataTypeFromString(String type){ // TODO Overhead beim mergen auf neue Struktur?
//		if(anonymousTypes.containsKey(type)){
//			AnonymousType instance =  MamldataFactory.eINSTANCE.createAnonymousType();
//			instance.setName(type);
//			return instance;
//		} else if(customTypes.containsKey(type)){
//			// Anonymous types are also contained here but are treated differently in first clause
//			return customTypes.get(type);
//		} else if (type.equalsIgnoreCase("String")){
//			return MamldataFactory.eINSTANCE.createString();
//		} else if (type.equalsIgnoreCase("Boolean")){
//			return MamldataFactory.eINSTANCE.createBoolean();
//		} else if (type.equalsIgnoreCase("PhoneNumber")){
//			return MamldataFactory.eINSTANCE.createPhoneNumber();
//		} else if (type.equalsIgnoreCase("Url")){
//			return MamldataFactory.eINSTANCE.createUrl();
//		} else if (type.equalsIgnoreCase("Email")){
//			return MamldataFactory.eINSTANCE.createEmail();
//		} else if (type.equalsIgnoreCase("File")){
//			return MamldataFactory.eINSTANCE.createFile();
//		} else if (type.equalsIgnoreCase("Image")){
//			return MamldataFactory.eINSTANCE.createImage();
//		} else if (type.equalsIgnoreCase("Location")){
//			return MamldataFactory.eINSTANCE.createLocation();
//		} else if (type.equalsIgnoreCase("Integer")){
//			return MamldataFactory.eINSTANCE.createInteger();
//		} else if (type.equalsIgnoreCase("Float")){
//			return MamldataFactory.eINSTANCE.createFloat();
//		} else if (type.equalsIgnoreCase("Date")){
//			return MamldataFactory.eINSTANCE.createDate();
//		} else if (type.equalsIgnoreCase("Time")){
//			return MamldataFactory.eINSTANCE.createTime();
//		} else if (type.equalsIgnoreCase("DateTime")){
//			return MamldataFactory.eINSTANCE.createDateTime();
//		}
//		
//		return MamldataFactory.eINSTANCE.createString(); // Default
//	}
	
	public void createDataStructureInUseCase(UseCase useCase){
		// Transform type structure to items
		
		// First create all raw types
		ArrayList<CustomType> typesToAdd = new ArrayList<CustomType>();
		for(DataTypeLiteral type : DynamicTypeLiteral.getCustomDataTypes()){
			CustomType dt = MamldataFactory.eINSTANCE.createCustomType();
			dt.setName(type.getIdentifier());
			typesToAdd.add(dt);
		}
		
		for(DataTypeLiteral type : DynamicTypeLiteral.getAnonymousDataTypes()){
			CustomType dt = MamldataFactory.eINSTANCE.createCustomType();
			dt.setName(type.getIdentifier());
			typesToAdd.add(dt);
		}
		
		// Now add all attributes
		for(CustomType type : typesToAdd){
			filterGraphBySourceDataType(DynamicTypeLiteral.from(type.getName()))
				.forEach(node -> convertNodeToProperty(node, type, typesToAdd));
		}
		
		// Reset use case datatypes (except explicitly modeled enums)
		Collection<DataType> typesToRemove = useCase.getDataTypes().stream().filter(elem -> !(elem instanceof Enum)).collect(Collectors.toList());
		//useCase.getDataTypes().removeAll(typesToRemove);
				
		// Add all types
		useCase.getDataTypes().addAll(typesToAdd);
	}
	
	private Collection<TypeStructureNode> filterGraphBySourceDataType(DataTypeLiteral sourceType){
		// Get matching source elements
		Collection<ParameterSource> sources = elementTypes.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().equals(sourceType)).map(entry -> entry.getKey()).collect(Collectors.toList());
		// Filter
		return typeGraph.stream().filter(t -> sources.contains(t.source)).collect(Collectors.toList());
	}
	
	private void convertNodeToProperty(TypeStructureNode node, CustomType targetType, ArrayList<CustomType> allTypes){
		CustomType ct = allTypes.stream().filter(dt -> dt.getName().equals(node.getType().getIdentifier())).collect(Collectors.toList()).get(0);
		
		Property prop = MamldataFactory.eINSTANCE.createProperty();
		prop.setName(node.getAttributeName());
		
		if(node.getMultiplicity().equals(Multiplicity.MANY)){
			de.wwu.maml.dsl.mamldata.Collection collection = MamldataFactory.eINSTANCE.createCollection();
			collection.setType(ct);
			prop.setType(collection);
		} else {
			prop.setType(ct);	
		}
		
		targetType.getAttributes().add(prop);
	}
}
