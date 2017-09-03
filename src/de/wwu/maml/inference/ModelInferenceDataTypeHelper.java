package de.wwu.maml.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import de.wwu.maml.dsl.maml.DataSource;
import de.wwu.maml.dsl.maml.Event;
import de.wwu.maml.dsl.maml.MamlPackage;
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
//import de.wwu.maml.dsl.mamldata.DataTypeLiteral;
import de.wwu.maml.dsl.mamldata.Enum;
import de.wwu.maml.dsl.mamldata.MamldataFactory;
import de.wwu.maml.dsl.mamldata.Multiplicity;
import de.wwu.maml.dsl.mamldata.Property;
import de.wwu.maml.dsl.mamldata.impl.CustomTypeImpl;
import de.wwu.maml.dsl.mamlgui.AccessType;
import de.wwu.maml.dsl.mamlgui.Attribute;
import de.wwu.maml.dsl.mamlgui.ComputationOperator;
import de.wwu.maml.dsl.mamlgui.GUIElement;
import de.wwu.maml.editor.service.MamlHelper;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;

public class ModelInferenceDataTypeHelper {
	
	public static final String ANONYMOUS_PREFIX = "__ANONYMOUS__";
	private static final String ANONYMOUS_TYPE_UI = "X";
	
//	protected Map<ParameterSource, DataType> elementTypes = new HashMap<ParameterSource, DataType>();
	protected Map<String, DataType> dataTypeNames = new HashMap<String, DataType>();
	
	protected MamlHypergraph<MamlHypergraphNode<?>, String> typeGraph;
	
	ArrayList<MamlHypergraphNode<DataType>> sourceTypes = new ArrayList<MamlHypergraphNode<DataType>>();
	ArrayList<MamlHypergraphTargetNode<DataType>> targetTypes = new ArrayList<MamlHypergraphTargetNode<DataType>>();

	ArrayList<HypergraphAccessNode> accessTypes = new ArrayList<HypergraphAccessNode>();
	ArrayList<HypergraphCardinalityNode> cardinalityTypes = new ArrayList<HypergraphCardinalityNode>();
	
	ArrayList<MamlHypergraphNode<String>> attributes = new ArrayList<MamlHypergraphNode<String>>();
	
	ArrayList<MamlHypergraphNode<ParameterSource>> sourceModelElements = new ArrayList<MamlHypergraphNode<ParameterSource>>();
	ArrayList<MamlHypergraphTargetNode<ParameterSource>> targetModelElements = new ArrayList<MamlHypergraphTargetNode<ParameterSource>>();
//	protected ArrayList<TypeStructureNode> typeGraph = new ArrayList<TypeStructureNode>(); // TODO join with dataTypeNames?

	/**
	 * Retrieve data type for given ProcessFlowElement
	 * @param obj
	 * @return
	 */
//	public DataType getType(ParameterSource obj) {
//		return elementTypes.get(obj);
//	}
	
	public DataType getDataTypeInstance(String dataTypeName){
		if(dataTypeName == null) return null;
		
		if(!dataTypeNames.containsKey(dataTypeName)){
			if(dataTypeName.equalsIgnoreCase("Boolean")){
				dataTypeNames.put("Boolean", MamldataFactory.eINSTANCE.createBoolean());
			} else if(dataTypeName.equalsIgnoreCase("Currency")){
				dataTypeNames.put("Currency", MamldataFactory.eINSTANCE.createCurrency());
			} else if(dataTypeName.equalsIgnoreCase("Date")){
				dataTypeNames.put("Date", MamldataFactory.eINSTANCE.createDate());
			} else if(dataTypeName.equalsIgnoreCase("DateTime")){
				dataTypeNames.put("DateTime", MamldataFactory.eINSTANCE.createDateTime());
			} else if(dataTypeName.equalsIgnoreCase("Email")){
				dataTypeNames.put("Email", MamldataFactory.eINSTANCE.createEmail());
			} else if(dataTypeName.equalsIgnoreCase("File")){
				dataTypeNames.put("File", MamldataFactory.eINSTANCE.createFile());
			} else if(dataTypeName.equalsIgnoreCase("Float")){
				dataTypeNames.put("Float", MamldataFactory.eINSTANCE.createFloat());
			} else if(dataTypeName.equalsIgnoreCase("Image")){
				dataTypeNames.put("Image", MamldataFactory.eINSTANCE.createImage());
			} else if(dataTypeName.equalsIgnoreCase("Integer")){
				dataTypeNames.put("Integer", MamldataFactory.eINSTANCE.createInteger());
			} else if(dataTypeName.equalsIgnoreCase("Location")){
				dataTypeNames.put("Location", MamldataFactory.eINSTANCE.createLocation());
			} else if(dataTypeName.equalsIgnoreCase("PhoneNumber")){
				dataTypeNames.put("PhoneNumber", MamldataFactory.eINSTANCE.createPhoneNumber());
			} else if(dataTypeName.equalsIgnoreCase("String")){
				dataTypeNames.put("String", MamldataFactory.eINSTANCE.createString());
			} else if(dataTypeName.equalsIgnoreCase("Time")){
				dataTypeNames.put("Time", MamldataFactory.eINSTANCE.createTime());
			} else if(dataTypeName.equalsIgnoreCase("Url")){
				dataTypeNames.put("Url", MamldataFactory.eINSTANCE.createUrl());
			} else {
				CustomType newType = MamldataFactory.eINSTANCE.createCustomType();
				newType.setName(dataTypeName);
				dataTypeNames.put(dataTypeName, newType);
			}
			// TODO Enum ? Collection?
		}
		
		return dataTypeNames.get(dataTypeName);
	}
	
	public boolean isPrimitive(DataType type){
		String[] primitiveTypes = { "Boolean", "String", "Currency", "Date", "DateTime", "Email", "File", "Float", 
									"Image", "Integer", "Location", "PhoneNumber", "String", "Time", "Url"};
		
		// Explicitly compare using strings to avoid different object instances
		String typeName = MamlHelper.getDataTypeName(type);
		for(String s : primitiveTypes){
			if(typeName.equals(s)) return true;
		}
		return false;
	}
	
	/**
	 * Set the respective data type to the model element.
	 * Also checks if the Use Case containment reference exists
	 * @param source
	 * @param type
	 */
	protected void setDataTypeInModel(ProcessFlowElement element, DataType type){
		// Check containment in use case data type list exists
		if(!((UseCase) element.eContainer()).getDataTypes().contains(type)){
			((UseCase) element.eContainer()).getDataTypes().add(type);
		}
		
		element.setDataType(type);
	}
	
	/**
	 * Initial call to infer the main data type for a sequence of ProcessFlowElements.
	 * @param startElement
	 * @return
	 */
	public Set<ProcessFlowElement> inferProcessFlowChain(ProcessFlowElement startElement) {
		Set<ProcessFlowElement> processed = new HashSet<ProcessFlowElement>();
		DataType lastOccurredType = null;
		
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
	public void inferProcessFlowChainRecursive(ProcessFlowElement currentElement, DataType lastOccurredType, Set<ProcessFlowElement> processed) {
		// Skip if currentElement was already processed
		if(currentElement == null || processed.contains(currentElement)){
			return;
		}
		
		// Process current item
		lastOccurredType = inferSingleItem(currentElement, lastOccurredType);
		processed.add(currentElement);
		
//		// Process attributes of current item
//		inferAttributes(currentElement);
		
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
	public DataType inferSingleItem(ProcessFlowElement processing, DataType lastOccurredType){
		
		if(processing instanceof DataSource){
			// Data Sources provide a type themselves (possibly a new one)
			DataType type = ((DataSource) processing).getDataType();
			if(type != null){
				lastOccurredType = type;
			}
			// In case no value is given, it must be the last known type
			setDataTypeInModel(processing, lastOccurredType);
//			elementTypes.put(processing, DynamicTypeLiteral.from(lastOccuredTypeName));
			
		} else if(processing instanceof Transform){ 
			// Special case for Transform elements because type changes, MUST BE HANDLED BEFORE upcoming ProcessElement superclass type
			
			// Check existing types for valid attributes
			DataType targetType = null; //tmp
//			DataType targetType = ModelInferenceTextInputHelper.getTypeForTransform(((Transform) processing).getDescription(), lastOccurredType, typeGraph);
//TODO check textinputhelper
			if(targetType != null){
//				elementTypes.put(processing, targetType);
				lastOccurredType = targetType;
				setDataTypeInModel(processing, targetType);
			} else {
				// Else inference failed -> no type information possible
				lastOccurredType = null;
			} 
			
		} else if(processing instanceof ProcessElement){
			// TODO In future check if anonymous type was explicitly set 
			DataType explicitType = null;
			if(explicitType != null && !MamlHelper.getDataTypeName(explicitType).equals(ANONYMOUS_TYPE_UI)){
				// Custom type
				lastOccurredType = explicitType;
			} else if(explicitType != null && MamlHelper.getDataTypeName(explicitType).equals(ANONYMOUS_TYPE_UI)){
				// Build a new and unique custom type name
				lastOccurredType = getDataTypeInstance(ANONYMOUS_PREFIX + processing.toString());
			} 
			// In case no value is given, it must be the last known type
//			elementTypes.put(processing, DynamicTypeLiteral.from(lastOccuredTypeName));
			setDataTypeInModel(processing, lastOccurredType);
		}
		
		//TODO enum duplicate with customType
		// Deliberately ignore events and control flows as they have no proper type
		
		return lastOccurredType;
	}
	
	/**
	 * Remove all known data type mappings and reset attribute graph structure.
	 */
	public void clearDataModel(){
		this.dataTypeNames.clear();
		this.typeGraph = new MamlHypergraph<MamlHypergraphNode<?>, String>();
	}
	
	/**
	 * Build up type list using data types already contained in the use case. 
	 * @param useCase
	 */
	public void loadDataTypes(UseCase useCase){
		for(DataType type : useCase.getDataTypes()){
			dataTypeNames.put(MamlHelper.getDataTypeName(type), type);
		}
	}
	
	public MamlHypergraphNode<DataType> getDataTypeNode(DataType type){
		if(type == null) {
			return null;
		}
		
		for(MamlHypergraphNode<DataType> node : sourceTypes){
			if(node.value.equals(type)){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphNode<DataType> newNode = new MamlHypergraphNode<DataType>(type);
		sourceTypes.add(newNode);
		return newNode;
	}
	
	public MamlHypergraphTargetNode<DataType> getDataTypeTargetNode(DataType type){
		if(type == null) {
			return null;
		}
		
		for(MamlHypergraphTargetNode<DataType> node : targetTypes){
			if(node.value.equals(type)){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphTargetNode<DataType> newNode = new MamlHypergraphTargetNode<DataType>(type);
		targetTypes.add(newNode);
		return newNode;
	}
	
	public MamlHypergraphNode<String> getAttributeNode(String qualifiedName){
		for(MamlHypergraphNode<String> node : attributes){
			if(node.value.equals(qualifiedName)){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphNode<String> newNode = new MamlHypergraphNode<String>(qualifiedName);
		attributes.add(newNode);
		return newNode;
	}
	
	public MamlHypergraphNode<ParameterSource> getModelElementNode(ParameterSource modelElement){
		if(modelElement == null) {
			return null;
		}
		
		for(MamlHypergraphNode<ParameterSource> node : sourceModelElements){
			if(node.value.equals(modelElement)){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphNode<ParameterSource> newNode = new MamlHypergraphNode<ParameterSource>(modelElement);
		sourceModelElements.add(newNode);
		return newNode;
	}
	
	public MamlHypergraphTargetNode<ParameterSource> getModelElementTargetNode(ParameterSource modelElement){
		if(modelElement == null) {
			return null;
		}
		
		for(MamlHypergraphTargetNode<ParameterSource> node : targetModelElements){
			if(node.value.equals(modelElement)){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphTargetNode<ParameterSource> newNode = new MamlHypergraphTargetNode<ParameterSource>(modelElement);
		targetModelElements.add(newNode);
		return newNode;
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
				
				// Process current connection -----------------------
				ArrayList<MamlHypergraphNode<?>> nodes = new ArrayList<MamlHypergraphNode<?>>();
				
				// data types
				if(MamlHelper.getDataType(source) == null){
					System.out.println("ERROR unknown type for " + source.toString());
					continue;
				}
				nodes.add(getDataTypeNode(MamlHelper.getDataType(source)));
				nodes.add(getDataTypeTargetNode((DataType) MamlHelper.getDataType(target)));
				// attribute
				nodes.add(getAttributeNode(MamlHelper.getDataTypeName(MamlHelper.getDataType(source)) + "." + target.getDescription()));
				// access type
				if(connector.getAccessType().equals(AccessType.WRITE)){
					nodes.add(HypergraphAccessNode.getWriteAccessNode());
				} else {
					nodes.add(HypergraphAccessNode.getReadAccessNode());
				}
				// cardinality
				nodes.add(HypergraphCardinalityNode.getCardinalityNode(target));
				// elements
				nodes.add(getModelElementNode(source));
				nodes.add(getModelElementTargetNode(target));
				
				// Add edge to graph
//				 System.out.println("Edge " + conn.toString() + ": " + nodes.toString());
				try {
					typeGraph.addEdge(connector.toString(), nodes);
				} catch(Exception e){
					// TODO maybe add connector to edge set in order to allow "multi-edge" scenario
					System.out.println("Multiple connections between the same elements detected. Ignored > 1.");
				}

				
				if(source instanceof Attribute){
//					// Keep track of source data types (only for non-PE as they are already tracked)
//					elementTypes.put(source, DynamicTypeLiteral.from(((Attribute) source).getType().toString()));
//				
					// Process attached attributes if current source is not a primitive type
					if(((Attribute) source).getType() != null && !isPrimitive(((Attribute) source).getType())){
						inferAttributes(target);
					}
				}
			} else if(connector.getTargetElement() instanceof ComputationOperator){
				// Infer attribute for Operator but relate to original source in type structure 
				inferTransitiveAttributes(transitiveSource, connector.getTargetElement());
			}
		}
	}
	
	
	// TODO replace by hypergraph query
//	/**
//	 * Retrieve all attributes for a specific data type
//	 */
//	public Collection<Attribute> getAttributesForType(DataType type){
//		return new ArrayList<Attribute>(); // TODO
//	}
//		// Either ProcessFlowElement -> compare type with target type
//		// Or GUIElement -> get type from String and compare
//		return this.typeGraph.stream().filter(elem -> !elem.equals(skipNode))
//				.filter(elem -> ((elem.getSource() instanceof ProcessFlowElement) && ((ProcessFlowElement) elem.getSource()).getDataType().equals(type)) 
//				|| ((elem.getSource() instanceof GUIElement) && DynamicTypeLiteral.from(((GUIElement) elem.getSource()).getType().toString()).equals(type)))
//		.collect(Collectors.toList());
//	}
//	
//	
//	// TODO replace by hypergraph query
//	public DataType getDataTypeForAttributeName(DataType sourceType, String attributeName){
//		Optional<TypeStructureNode> node = this.typeGraph.stream().filter(elem -> elem.getAttributeName().equals(attributeName))
//				.filter(elem -> ((elem.getSource() instanceof ProcessFlowElement) && ((ProcessFlowElement) elem.getSource()).getDataType().equals(sourceType)) 
//				|| ((elem.getSource() instanceof GUIElement) && DynamicTypeLiteral.from(((GUIElement) elem.getSource()).getType().toString()).equals(sourceType)))
//				.findFirst();
//		
//		return node.isPresent() ? node.get().getType() : null;
//	}
	
	
	public void createDataStructureInUseCase(UseCase useCase){
		return;
	}
//		// Transform type structure to items
//		
//		// First create all raw types
//		ArrayList<CustomType> typesToAdd = new ArrayList<CustomType>();
//		for(DataTypeLiteral type : DynamicTypeLiteral.getCustomDataTypes()){
//			CustomType dt = MamldataFactory.eINSTANCE.createCustomType();
//			dt.setName(type.getIdentifier());
//			typesToAdd.add(dt);
//		}
//		
//		for(DataTypeLiteral type : DynamicTypeLiteral.getAnonymousDataTypes()){
//			CustomType dt = MamldataFactory.eINSTANCE.createCustomType();
//			dt.setName(type.getIdentifier());
//			typesToAdd.add(dt);
//		}
//		
//		// Now add all attributes
//		for(CustomType type : typesToAdd){
//			// TODO
////			filterGraphBySourceDataType(DynamicTypeLiteral.from(type.getName()))
////				.forEach(node -> convertNodeToProperty(node, type, typesToAdd));
//		}
//		
//		// Reset use case datatypes (except explicitly modeled enums)
//		Collection<DataType> typesToRemove = useCase.getDataTypes().stream().filter(elem -> !(elem instanceof Enum)).collect(Collectors.toList());
//		//useCase.getDataTypes().removeAll(typesToRemove);
//				
//		// Add all types
//		useCase.getDataTypes().addAll(typesToAdd);
//	}
	
//	private Collection<TypeStructureNode> filterGraphBySourceDataType(DataTypeLiteral sourceType){
//		// Get matching source elements
//		Collection<ParameterSource> sources = elementTypes.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().equals(sourceType)).map(entry -> entry.getKey()).collect(Collectors.toList());
//		// Filter
//		return typeGraph.stream().filter(t -> sources.contains(t.source)).collect(Collectors.toList());
//	}
	
	private void convertNodeToProperty(TypeStructureNode node, CustomType targetType, ArrayList<CustomType> allTypes){
		CustomType ct = allTypes.stream().filter(dt -> dt.getName().equals(MamlHelper.getDataTypeName(node.getType()))).collect(Collectors.toList()).get(0);
		
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
