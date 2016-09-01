package de.wwu.md2dot0.inference;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;

import md2dot0.ParameterSource;
import md2dot0.ProcessFlowElement;
import md2dot0data.DataTypeLiteral;

public class ModelInferenceMergeHelper {
	
	ModelInferenceDataTypeHelper inferenceDataTypeHelper;
	
	@SuppressWarnings("rawtypes")
	MamlHypergraph<MamlHypergraphNode, String> graph = new MamlHypergraph<MamlHypergraphNode, String>();

	ArrayList<HypergraphAccessNode> accessTypes = new ArrayList<HypergraphAccessNode>();
	ArrayList<HypergraphCardinalityNode> cardinalityTypes = new ArrayList<HypergraphCardinalityNode>();
	
	public void mergeProcessElements(EList<ProcessFlowElement> processFlowElements,
			ModelInferenceDataTypeHelper inferenceDataTypeHelper) {
		this.inferenceDataTypeHelper = inferenceDataTypeHelper;
		
		// TODO merge individual process flow elements
		
		// 1) Setup graph
		
		//// Predefined values
		ArrayList<MamlHypergraphNode<DataTypeLiteral>> sourceTypes = new ArrayList<MamlHypergraphNode<DataTypeLiteral>>();
		ArrayList<MamlHypergraphTargetNode<DataTypeLiteral>> targetTypes = new ArrayList<MamlHypergraphTargetNode<DataTypeLiteral>>();
		
		// Data types
		for(DataTypeLiteral literal : DynamicTypeLiteral.getTypes().values()){
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
		cardinalityTypes.add(HypergraphCardinalityNode.getCardinalityMultiNode()); // TODO others
		
		//// Process element connections -> edges
		for(ProcessFlowElement pfe : processFlowElements){
			// Get attributes (recursive)
			addAttributesRecursive(pfe);
		}
		
		// 2) Validate
		// TODO
		
		// 3) Infer/deduplicate
		
	}

	protected void addAttributesRecursive(ParameterSource source){
		// TODO
	}
}
