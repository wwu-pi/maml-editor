package de.wwu.md2dot0.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import md2dot0data.DataType;

public class TypeLiteral {
	
	public static final String ANONYMOUS_PREFIX = "__ANONYMOUS__";

	static Map<String, TypeLiteral> customTypes = new HashMap<String, TypeLiteral>();  
	static Map<String, TypeLiteral> primitives = new HashMap<String, TypeLiteral>();
	
	protected String identifier;
	protected String name;
	
	public TypeLiteral(String identifier, String name) {
		this.identifier = identifier;
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	/**
	 * Initialize primitive data type literals whenever a public method is called.
	 */
	private static void initPrimitives() {
		// Initialize primitives
		if(primitives.size() == 0){
			primitives.put("STRING", new TypeLiteral("String", "STRING"));
			primitives.put("BOOLEAN", new TypeLiteral("Boolean", "BOOLEAN"));
			primitives.put("PHONENUMBER", new TypeLiteral("PhoneNumber", "PHONENUMBER"));
			primitives.put("URL", new TypeLiteral("Url", "URL"));
			primitives.put("EMAIL", new TypeLiteral("Email", "EMAIL"));
			primitives.put("FILE", new TypeLiteral("File", "FILE"));
			primitives.put("IMAGE", new TypeLiteral("Image", "IMAGE"));
			primitives.put("LOCATION", new TypeLiteral("Location", "LOCATION"));
			primitives.put("INTEGER", new TypeLiteral("Integer", "INTEGER"));
			primitives.put("FLOAT", new TypeLiteral("Float", "FLOAT"));
			primitives.put("DATE", new TypeLiteral("Date", "DATE"));
			primitives.put("TIME", new TypeLiteral("Time", "TIME"));
			primitives.put("DATETIME", new TypeLiteral("DateTime", "DATETIME"));
		}
	}

	/**
	 * Get or create a type literal for the given string.
	 * @param string
	 * @return
	 */
	public static TypeLiteral from(String string) {
		initPrimitives();
		
		// Prepare user input
		String type = string.toUpperCase().trim();
		
		// Lookup or create new
		if(!literalExists(type)){
			customTypes.put(type, new TypeLiteral(type, string.trim()));
		}
		
		return getLiteral(type);
	}
	
	/**
	 * Internal helper to check if literal already exists as primitive or custom type.
	 * @param type
	 * @return
	 */
	private static boolean literalExists(String type){
		return getLiteral(type) != null;
	}
	
	/**
	 * Internal helper to return literal from primitive or custom literal list.
	 * @param type
	 * @return
	 */
	private static TypeLiteral getLiteral(String type){
		return primitives.get(type) != null ? primitives.get(type) : customTypes.get(type);  
	}
	
	/**
	 * List all know data type literals.
	 * @return
	 */
	public static Collection<TypeLiteral> values(){
		initPrimitives();
		
		return Stream.concat(primitives.values().stream(), customTypes.values().stream()).collect(Collectors.toList());
	}
	
	/**
	 * String representation of primitive data types.
	 * @return
	 */
	public static Collection<String> getPrimitiveDataTypesAsString(){
		return primitives.values().stream().map(elem -> elem.name).collect(Collectors.toList());
	}
	
	/**
	 * String representation of custom data types.
	 * @return
	 */
	public static Collection<String> getCustomDataTypesAsString(){
		return customTypes.values().stream().map(elem -> elem.name).collect(Collectors.toList());
	}
	
	/**
	 * String representation of all data types.
	 * @return
	 */
	public static Collection<String> getAllDataTypesAsString(){
		return values().stream().map(elem -> elem.name).collect(Collectors.toList());
	}
	
	public static DataType getDataType(){
		initPrimitives();
		return null; // TODO from inferencedatatype.getDataTypeFromString
	}
}
