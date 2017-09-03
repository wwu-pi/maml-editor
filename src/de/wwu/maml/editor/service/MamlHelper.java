package de.wwu.maml.editor.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import de.wwu.maml.dsl.maml.ParameterSource;
import de.wwu.maml.dsl.maml.ProcessFlowElement;
import de.wwu.maml.dsl.mamldata.CustomType;
import de.wwu.maml.dsl.mamldata.DataType;
import de.wwu.maml.dsl.mamldata.DataTypeLiteral;
import de.wwu.maml.dsl.mamldata.PrimitiveType;
import de.wwu.maml.dsl.mamlgui.GUIElement;

public class MamlHelper {

	public static String toFirstUpper(String input){
		if(input == null || input.length() == 0){
			return input;
		}
		
		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}
	
	public static String toFirstLower(String input){
		if(input == null || input.length() == 0){
			return input;
		}
		
		return Character.toLowerCase(input.charAt(0)) + input.substring(1);
	}
	
	public static String getEllipsis(String text, int maxLength){
		if(text.length() <= maxLength){
			return text;
		} else {
			return text.substring(0, maxLength - 2) + "...";
		}
	}
	
	public static String camelCaseToSpacedString(String text){
		List<String> words = Arrays.asList(text.split("(?<=[a-z])(?=[A-Z])"));
		return String.join(" ", words.stream().map(word -> toFirstLower(word)).toArray(String[]::new));
	}
	
	/**
	 * Allowed attribute names consist of an initial lowercase alphabetic character followed 
	 * by arbitrary alphanumeric characters as well as '-' and '_' 
	 *   
	 * @param text
	 * @return
	 */
	public static String getAllowedAttributeName(String text){
		// Replace umlauts
		text = text.replaceAll("�", "Ae");
		text = text.replaceAll("�", "Oe");
		text = text.replaceAll("�", "Ue");
		text = text.replaceAll("�", "ae");
		text = text.replaceAll("�", "oe");
		text = text.replaceAll("�", "ue");
		text = text.replaceAll("�", "ss");
		
		// Only alphabetic characters in front
		if(!text.matches("[a-zA-Z].*")){
			return text.length() == 1 ? "" : getAllowedAttributeName(text.substring(1));
		}

		// Replace spaces by camel cased name (and trim)
		if(text.contains(" ")){
			String[] parts = text.split(" ");
			text = "";
			for(String part : parts){
				text += toFirstUpper(part);
			}
		}
		
		// Filter only allowed characters and replace by camel cased name 
		// At the same time trim trailing spaces
		String filteredText = "";
		boolean nextUpper = false;
		for(char c : text.toCharArray()){
			if((c + "").matches("[a-zA-Z0-9_\\-]")) {
				filteredText += nextUpper ? (c + "").toUpperCase() : c;
				nextUpper = false;
			} else {
				nextUpper = true;
			}
		}
		
		// First character lowercase
		return toFirstLower(filteredText);
	}
	
	/**
	 * Allowed data type names consist of an initial uppercase alphabetic character followed 
	 * by arbitrary alphanumeric characters as well as '-' and '_' 
	 *   
	 * @param text
	 * @return
	 */
	public static String getAllowedDataTypeName(String text){
		// Basically the same as for attributes but with first uppercase letter
		return toFirstUpper(getAllowedAttributeName(text));
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	/**
	 * Retrieve data type depending on the subtype of the parameter source.
	 * @param source
	 * @return
	 */
	public static DataType getDataType(ParameterSource source){
		if(source instanceof ProcessFlowElement) {
			return ((ProcessFlowElement) source).getDataType();
		} else if(source instanceof GUIElement) {
			return ((GUIElement) source).getType();
		}
		return null;
	}
	
	/**
	 * Retrieve data type name depending on the subtype of the data type.
	 * @param source
	 * @return
	 */
	public static String getDataTypeName(DataType type){
		if(type == null) return "";
		
		if(type instanceof PrimitiveType){
			return ((PrimitiveType) type).getClass().getSimpleName().replace("Impl", "");
		} else if (type instanceof CustomType){
			return ((CustomType) type).getName();
		}
		return type.getClass().getSimpleName();
	}
}
