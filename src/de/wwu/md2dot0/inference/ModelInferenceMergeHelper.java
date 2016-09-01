package de.wwu.md2dot0.inference;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import de.wwu.md2dot0.inference.error.CardinalityValidationError;
import de.wwu.md2dot0.inference.error.MamlValidationError;
import de.wwu.md2dot0.inference.error.TypeValidationError;
import md2dot0.ParameterConnector;
import md2dot0.ParameterSource;
import md2dot0.ProcessFlowElement;
import md2dot0data.DataTypeLiteral;
import md2dot0data.Multiplicity;
import md2dot0gui.AccessType;
import md2dot0gui.Attribute;

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

	public void mergeProcessElements(EList<ProcessFlowElement> processFlowElements,
			ModelInferenceDataTypeHelper inferenceDataTypeHelper) {
		this.inferenceDataTypeHelper = inferenceDataTypeHelper;

		// TODO merge individual process flow elements

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
		for(MamlHypergraphNode<DataTypeLiteral> dataTypeNode : sourceTypes){
			for(MamlHypergraphNode<String> attribute :attributes) { // TODO all combinations not nice for performance
				// System.out.println("Check " + dataTypeNode.toString() + " + " + attribute.toString() + ": " + (graph.findEdgeSet(dataTypeNode, attribute) != null ? graph.findEdgeSet(dataTypeNode, attribute).size() : 0) + " edge(s)");

				@SuppressWarnings("rawtypes")
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
		for(ParameterConnector conn : source.getParameters()){
			if(conn.getTargetElement() instanceof Attribute) {
				
				@SuppressWarnings("rawtypes")
				ArrayList<MamlHypergraphNode> nodes = new ArrayList<MamlHypergraphNode>();
				
				// data types
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
				graph.addEdge(conn.toString(), nodes);
			}
			
			// Recursive call
			addAttributesRecursive(conn.getTargetElement());
		}
	}
	
	public MamlHypergraphNode<DataTypeLiteral> getDataTypeNode(DataTypeLiteral type){
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
