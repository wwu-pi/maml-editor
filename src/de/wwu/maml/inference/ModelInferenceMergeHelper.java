package de.wwu.maml.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;

import de.wwu.maml.inference.error.CardinalityValidationError;
import de.wwu.maml.inference.error.MamlValidationError;
import de.wwu.maml.inference.error.TypeValidationError;
import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.ProcessFlowElement;
import de.wwu.maml.dsl.maml.UseCase;
import de.wwu.maml.dsl.mamldata.CustomType;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.dsl.mamldata.MamldataFactory;
import de.wwu.maml.dsl.mamldata.Multiplicity;
import de.wwu.maml.dsl.mamldata.Property;

public class ModelInferenceMergeHelper {

	ModelInferenceDataTypeHelper inferenceDataTypeHelper;

	ArrayList<MamlValidationError> errorList = new ArrayList<MamlValidationError>();

	public void mergeProcessElements(EList<ProcessFlowElement> processFlowElements,
			MamlHypergraph<MamlHypergraphNode<?>, String> typeGraph) {
		this.inferenceDataTypeHelper = ModelInferenceDataTypeHelper.getInstance();

		Collection<MamlHypergraphNode<DataType>> dataTypeNodes = inferenceDataTypeHelper.getAllDataTypes().stream().map(elem -> inferenceDataTypeHelper.getDataTypeNode(elem)).collect(Collectors.toList());
		Collection<MamlHypergraphNode<?>> attributes = typeGraph.getVertices().stream().filter(node -> node.getValue() instanceof String).collect(Collectors.toList()); // TODO better attribute retrieval
		
		// 1) Validate graph
		// TODO validate bidirectional relationship
		for(MamlHypergraphNode<DataType> dataTypeNode : dataTypeNodes){
			for(MamlHypergraphNode<?> attribute : attributes) { // TODO all combinations not nice for performance
				Collection<MamlHypergraphNode<?>> edgeContent = typeGraph.findEdgeSetFlatContent(dataTypeNode, attribute);
				
				Object[] cardinalities = edgeContent.stream().filter(node -> node instanceof HypergraphCardinalityNode).toArray();
				if(cardinalities.length > 1) {
					//System.out.println("ERROR: Cardinality error!");
					for(Object elem : edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof ParameterSource).toArray()) {
						errorList.add(new CardinalityValidationError((ParameterSource) ((MamlHypergraphTargetNode<?>) elem).getValue(), cardinalities));
					}
				}
				
				Object[] types = edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof DataType).toArray();
				if(types.length > 1) {
					//System.out.println("ERROR: Type error!");
					for(Object elem : edgeContent.stream().filter(node -> node instanceof MamlHypergraphTargetNode<?> && node.getValue() instanceof ParameterSource).toArray()) {
						errorList.add(new TypeValidationError((ParameterSource) ((MamlHypergraphTargetNode<?>) elem).getValue(), types));
					}
				}
			}
		}

		// 2) Infer/deduplicate
		if(errorList.size() > 0){
			for(MamlValidationError error : errorList) System.out.println(error.getErrorText() + "/ " + error.getElement().toString());
		} else {
			//TODO
		}
		
		// TODO use error list
	}
	
	public void createDataStructureInUseCase(UseCase useCase, MamlHypergraph<MamlHypergraphNode<?>, String> typeGraph){
		// Get relations for each data type within the use case
		Collection<DataType> dataTypes = useCase.getDataTypes();
		
		for(CustomType type : dataTypes.stream().filter(elem -> elem instanceof CustomType).map(elem -> (CustomType) elem).collect(Collectors.toList())){
			// Relation edge
			Collection<Collection<MamlHypergraphNode<?>>> edges = typeGraph.getIncidentEdgeContents(inferenceDataTypeHelper.getDataTypeNode(type));
			
			if(edges == null) continue;
			
			for(Collection<MamlHypergraphNode<?>> edge : edges){
				// Check for duplicates
				if(type.getAttributes().stream().anyMatch(elem -> elem.getName().equals(typeGraph.getEdgeAttributeName(edge)))) continue;
				
				// Convert relation to property and add to use case
				Property prop = convertEdgeToProperty(type, edge, typeGraph);
				type.getAttributes().add(prop);
			}
		}
	}
	
	private Property convertEdgeToProperty(CustomType type, Collection<MamlHypergraphNode<?>> edge, MamlHypergraph<MamlHypergraphNode<?>, String> typeGraph){
		
		Property prop = MamldataFactory.eINSTANCE.createProperty();
		prop.setName(typeGraph.getEdgeAttributeName(edge));
		
		if(typeGraph.getEdgeCardinality(edge).equals(Multiplicity.MANY) || typeGraph.getEdgeCardinality(edge).equals(Multiplicity.ZEROMANY)){
			de.wwu.maml.dsl.mamldata.Collection collection = MamldataFactory.eINSTANCE.createCollection();
			collection.setType(typeGraph.getEdgeTargetDataType(edge));
			prop.setType(collection);
		} else {
			prop.setType(typeGraph.getEdgeTargetDataType(edge));	
		}
		
		return prop;
	}
}
