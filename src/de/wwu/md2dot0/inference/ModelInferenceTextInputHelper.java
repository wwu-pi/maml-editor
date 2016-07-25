package de.wwu.md2dot0.inference;

import java.util.ArrayList;

import md2dot0data.DataTypeLiteral;

public class ModelInferenceTextInputHelper {

	public static DataTypeLiteral getTypeForTransform(String description, DataTypeLiteral inputType, ArrayList<TypeStructureNode> typeGraph){
		// Is there input?
		if(description == null || description == "") return null;
		
		DataTypeLiteral currentType = null;
		
		// Parse input
		String[] parts = description.split(".");
		
		for(String part : parts){
			if(part.equalsIgnoreCase("input")){
				currentType = inputType;
			} else {
				// Get first type node in existing graph that matches type and attribute 
				for(TypeStructureNode node : typeGraph){
					if(node.getType() == currentType && node.getAttributeName().equalsIgnoreCase(part)){
						currentType = node.getType();
						break;
					}
				}
			}
			// TODO functions such as first()...
		}
		
		return currentType;
	}
}
