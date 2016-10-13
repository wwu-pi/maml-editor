package de.wwu.maml.editor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Md2dot0Helper {

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
	
	/**
	 * Allowed attribute names consist of an initial lowercase alphabetic character followed 
	 * by arbitrary alphanumeric characters as well as '-' and '_' 
	 *   
	 * @param text
	 * @return
	 */
	public static String getAllowedAttributeName(String text){
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
}
