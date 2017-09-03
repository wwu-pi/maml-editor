package de.wwu.maml.inference;

import java.util.regex.Pattern;
import de.wwu.maml.dsl.mamldata.DataType;

public class ModelInferenceTextInputHelper {

	public static DataType getTypeForTransform(String description, DataType inputType, MamlHypergraph<MamlHypergraphNode<?>, String> typeGraph){ // , Map<ParameterSource, DataType> elementTypes
		// Is there input to infer something from?
		if(description == null || description == "" || inputType == null) return null;
		
		DataType currentType = null;
		
		// Parse input
		String[] parts = description.split(Pattern.quote("."));
		
		for(String part : parts){
			if(part.equalsIgnoreCase("input")){
				currentType = inputType;
			} else {
				// Get first type node in existing graph that matches type and attribute 
				DataType typeMatch = typeGraph.getIncidentEdgeContents(ModelInferenceDataTypeHelper.getInstance().getDataTypeNode(currentType)).stream()
					.filter(edge -> typeGraph.getEdgeAttributeName(edge).equalsIgnoreCase(part))
					.map(edge -> typeGraph.getEdgeTargetDataType(edge))
					.findFirst().orElse(null);
				
				System.out.println("ModelInferenceTextInputHelper:"+ typeMatch);
				
				if(typeMatch != null){
					currentType = typeMatch;
				} else  {
					// Nothing found in this step -> inference error
					return null;
				}
			}
			// TODO functions such as first()...
		}
		
		return currentType;
	}
	
	public static boolean isAllowedTypeName(String typeName){
		return typeName != null && !typeName.equals("") && !typeName.startsWith("_") && !typeName.equals(ModelInferenceDataTypeHelper.ANONYMOUS_TYPE_UI);
	}
}
