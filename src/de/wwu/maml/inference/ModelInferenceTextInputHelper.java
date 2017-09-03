package de.wwu.maml.inference;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.editor.service.MamlHelper;

public class ModelInferenceTextInputHelper {

	public static DataType getTypeForTransform(String description, DataType inputType, ArrayList<TypeStructureNode> typeGraph){ // , Map<ParameterSource, DataType> elementTypes
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
				boolean found = false;
				for(TypeStructureNode node : typeGraph){
					if(MamlHelper.getDataType(node.getSource()) != null && MamlHelper.getDataType(node.getSource()).equals(currentType) 
							&& node.getAttributeName() != null && node.getAttributeName().equalsIgnoreCase(part)){
						currentType = node.getType();
						found = true;
						break;
					}
				}
				
				if(!found) {
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
