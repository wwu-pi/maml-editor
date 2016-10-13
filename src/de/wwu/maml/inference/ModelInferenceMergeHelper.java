package de.wwu.maml.inference;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import de.wwu.maml.inference.error.CardinalityValidationError;
import de.wwu.maml.inference.error.MamlValidationError;
import de.wwu.maml.inference.error.TypeValidationError;
import de.wwu.maml.dsl.maml.ParameterConnector;
import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.ProcessFlowElement;
import de.wwu.maml.dsl.mamldata.DataTypeLiteral;
import de.wwu.maml.dsl.mamldata.Multiplicity;
import de.wwu.maml.dsl.mamlgui.AccessType;
import de.wwu.maml.dsl.mamlgui.Attribute;

public class ModelInferenceMergeHelper {

	ModelInferenceDataTypeHelper inferenceDataTypeHelper;

	@SuppressWarnings("rawtypes")
	MamlHypergraph<MamlHypergraphNode, String> graph = new MamlHypergraph<MamlHypergraphNode, String>();

	ArrayList<MamlHypergraphNode<DataTypeLiteral>> sourceTypes = new ArrayList<MamlHypergraphNode<DataTypeLiteral>>();
	ArrayList<MamlHypergraphTargetNode<DataTypeLiteral>> targetTypes = new ArrayList<MamlHypergraphTargetNode<DataTypeLiteral>>();

	ArrayList<HypergraphAccessNode> accessTypes = new ArrayList<HypergraphAccessNode>();
	ArrayList<HypergraphCardinalityNode> cardinalityTypes = new ArrayList<HypergraphCardinalityNode>();
	
	ArrayList<MamlHypergraphNode<String>> attributes = new ArrayList<MamlHypergraphNode<String>>();
	
	ArrayList<MamlValidationError> errorList = new ArrayList<MamlValidationError>();

	@SuppressWarnings("rawtypes")
	public void mergeProcessElements(EList<ProcessFlowElement> processFlowElements,
			ModelInferenceDataTypeHelper inferenceDataTypeHelper) {
		this.inferenceDataTypeHelper = inferenceDataTypeHelper;

		graph = new MamlHypergraph<MamlHypergraphNode, String>();
		
		// 1) Setup graph
		// Data types
		for (DataTypeLiteral literal : DynamicTypeLiteral.getTypes().values()) {
			// As source
			sourceTypes.add(new MamlHypergraphNode<DataTypeLiteral>(literal));
			// As target
			targetTypes.add(new MamlHypergraphTargetNode<DataTypeLiteral>(literal));
		}

		// Accessibility
		accessTypes.add(HypergraphAccessNode.getReadAccessNode());
		accessTypes.add(HypergraphAccessNode.getWriteAccessNode());

		// Cardinality
		cardinalityTypes.add(HypergraphCardinalityNode.getCardinalityOneNode());
		cardinalityTypes.add(HypergraphCardinalityNode.getCardinalityManyNode()); // TODO
																					// others
		//// Process element connections -> edges
		for (ProcessFlowElement pfe : processFlowElements) {
			// Get attributes (recursive)
			addAttributesRecursive(pfe);
		}

		// 2) Validate
		// TODO validate bidirectional relationship
		for(MamlHypergraphNode<DataTypeLiteral> dataTypeNode : sourceTypes){
			for(MamlHypergraphNode<String> attribute :attributes) { // TODO all combinations not nice for performance
				Collection<MamlHypergraphNode> edgeContent = graph.findEdgeSetFlatContent(dataTypeNode, attribute);
				
				Object[] cardinalities = edgeContent.stream().filter(node -> node instanceof HypergraphCardinalityNode).toArray();
				if(cardinalities.length > 1) {
					//System.out.println("ERROR: Cardinality error!");
					for(Object elem : edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof ParameterSource).toArray()) {
						errorList.add(new CardinalityValidationError((ParameterSource) ((MamlHypergraphTargetNode) elem).getValue(), cardinalities));
					}
				}
				
				Object[] types = edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof DataTypeLiteral).toArray();
				if(types.length > 1) {
					//System.out.println("ERROR: Type error!");
					for(Object elem : edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof ParameterSource).toArray()) {
						errorList.add(new TypeValidationError((ParameterSource) ((MamlHypergraphTargetNode) elem).getValue(), types));
					}
				}
			}
		}

		// 3) Infer/deduplicate
		if(errorList.size() > 0){
			for(MamlValidationError error : errorList) System.out.println(error.getErrorText() + "/ " + error.getElement().toString());
		} else {
			//TODO
		}
	}

	protected void addAttributesRecursive(ParameterSource source){
		// TODO add bidirectional relationship
		for(ParameterConnector conn : source.getParameters()){
			if(conn.getTargetElement() instanceof Attribute) {
				
				@SuppressWarnings("rawtypes")
				ArrayList<MamlHypergraphNode> nodes = new ArrayList<MamlHypergraphNode>();
				
				// data types
				if(inferenceDataTypeHelper.getType(conn.getSourceElement()) == null){
					System.out.println("ERROR unknown type");
					continue;
				}
				nodes.add(getDataTypeNode(inferenceDataTypeHelper.getType(conn.getSourceElement())));
				nodes.add(getDataTypeTargetNode((DataTypeLiteral) inferenceDataTypeHelper.getDataTypeFromParameterSource(conn.getTargetElement())));
				// attribute
				nodes.add(getAttributeNode(inferenceDataTypeHelper.getType(conn.getSourceElement()).getIdentifier() + "." + ((Attribute) conn.getTargetElement()).getDescription()));
				// access type
				if(conn.getAccessType().equals(AccessType.WRITE)){
					nodes.add(HypergraphAccessNode.getWriteAccessNode());
				} else {
					nodes.add(HypergraphAccessNode.getReadAccessNode());
				}
				// cardinality
				if(((Attribute) conn.getTargetElement()).getMultiplicity().equals(Multiplicity.ONE)){
					nodes.add(HypergraphCardinalityNode.getCardinalityOneNode());
				} else { // TODO others
					nodes.add(HypergraphCardinalityNode.getCardinalityManyNode());
				}
				// elements
				nodes.add(new MamlHypergraphNode<ParameterSource>(source)); //TODO maybe duplicates
				nodes.add(new MamlHypergraphTargetNode<ParameterSource>((Attribute) conn.getTargetElement())); //TODO maybe duplicates
				
				// Add edge to graph
				// System.out.println("Edge " + conn.toString() + ": " + nodes.toString());
				try {
					graph.addEdge(conn.toString(), nodes);
				} catch(Exception e){
					// TODO maybe add connector to edge set in order to allow "multi-edge" scenario
					System.out.println("Multiple connections between the same elements detected. Ignored > 1.");
				}
			}
			
			// Recursive call
			addAttributesRecursive(conn.getTargetElement());
		}
	}
	
	public MamlHypergraphNode<DataTypeLiteral> getDataTypeNode(DataTypeLiteral type){
		if(type == null) {
			return null;
		}
		
		for(MamlHypergraphNode<DataTypeLiteral> node : sourceTypes){
			if(node.value.getIdentifier().equals(type.getIdentifier())){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphNode<DataTypeLiteral> newNode = new MamlHypergraphNode<DataTypeLiteral>(type);
		sourceTypes.add(newNode);
		return newNode;
	}
	
	public MamlHypergraphTargetNode<DataTypeLiteral> getDataTypeTargetNode(DataTypeLiteral type){
		if(type == null) {
			return null;
		}
		
		for(MamlHypergraphTargetNode<DataTypeLiteral> node : targetTypes){
			if(node.value.getIdentifier().equals(type.getIdentifier())){
				return node;
			}
		}
		
		// Not found -> add new
		MamlHypergraphTargetNode<DataTypeLiteral> newNode = new MamlHypergraphTargetNode<DataTypeLiteral>(type);
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
}
