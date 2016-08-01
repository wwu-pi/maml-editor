package de.wwu.md2dot0.design;

public class Md2dot0Helper {

	public static String toFirstUpper(String input){
		if(input == null || input.length() == 0){
			return input;
		}
		
		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}
	
	public static String getEllipsis(String text, int maxLength){
		if(text.length() <= maxLength){
			return text;
		} else {
			return text.substring(0, maxLength - 2) + "...";
		}
	}
}
