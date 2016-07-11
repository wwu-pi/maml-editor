package de.wwu.md2dot0.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import md2dot0.UseCase;
import md2dot0data.impl.DataTypeLiteralImpl;

public class DynamicTypeLiteral extends DataTypeLiteralImpl {
	
	public static final String ANONYMOUS_PREFIX = "__ANONYMOUS__";
	private static final String ANONYMOUS_TYPE_UI = "X";

	static UseCase container;
	static Map<String, DynamicTypeLiteral> complexTypes = new HashMap<String, DynamicTypeLiteral>();  
	static Map<String, DynamicTypeLiteral> primitives = new HashMap<String, DynamicTypeLiteral>();
	
	public DynamicTypeLiteral(String identifier, String name) {
		this.identifier = identifier;
		this.name = name;
		if(container != null){
			container.getDatatypes().add(this);
		} else {
			System.out.println("no container exists");
		}
	}
	
	public static void setDataTypeContainer(UseCase useCase){
		container = useCase;
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
			primitives.put("STRING", new DynamicTypeLiteral("STRING", "String"));
			primitives.put("BOOLEAN", new DynamicTypeLiteral("BOOLEAN", "Boolean"));
			primitives.put("PHONENUMBER", new DynamicTypeLiteral("PHONENUMBER", "PhoneNumber"));
			primitives.put("URL", new DynamicTypeLiteral("URL", "Url"));
			primitives.put("EMAIL", new DynamicTypeLiteral("EMAIL", "Email"));
			primitives.put("FILE", new DynamicTypeLiteral("FILE", "File"));
			primitives.put("IMAGE", new DynamicTypeLiteral("IMAGE", "Image"));
			primitives.put("LOCATION", new DynamicTypeLiteral("LOCATION", "Location"));
			primitives.put("INTEGER", new DynamicTypeLiteral("INTEGER", "Integer"));
			primitives.put("FLOAT", new DynamicTypeLiteral("FLOAT", "Float"));
			primitives.put("DATE", new DynamicTypeLiteral("DATE", "Date"));
			primitives.put("TIME", new DynamicTypeLiteral("TIME", "Time"));
			primitives.put("DATETIME", new DynamicTypeLiteral("DATETIME", "DateTime"));
		}
	}

	/**
	 * Get or create a type literal for the given string.
	 * @param string
	 * @return
	 */
	public static DynamicTypeLiteral from(String string) {
		initPrimitives();
		
		// Prepare user input
		String type = string.toUpperCase().trim();
		
		// Catch invalid types
		if(!isAllowedTypeName(type)) return null;
				
		// Lookup or create new
		if(!literalExists(type)){
			complexTypes.put(type, new DynamicTypeLiteral(type, string.trim()));
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
	private static DynamicTypeLiteral getLiteral(String type){
		return primitives.get(type) != null ? primitives.get(type) : complexTypes.get(type);  
	}
	
	/**
	 * List all know data type literals.
	 * @return
	 */
	public static Collection<DynamicTypeLiteral> values(){
		initPrimitives();
		
		return Stream.concat(primitives.values().stream(), complexTypes.values().stream()).collect(Collectors.toList());
	}
	
	/**
	 * String representation of primitive data types.
	 * @return
	 */
	public static Collection<String> getPrimitiveDataTypesAsString(){
		initPrimitives();
		
		return primitives.values().stream().map(elem -> elem.name).collect(Collectors.toList());
	}
	
	/**
	 * String representation of custom data types.
	 * @return
	 */
	public static Collection<String> getCustomDataTypesAsString(){
		return complexTypes.values().stream()
				.map(elem -> elem.name)
				.filter(elem -> !elem.startsWith(ANONYMOUS_PREFIX))
				.collect(Collectors.toList());
	}

	/**
	 * String representation of all data types.
	 * @return
	 */
	public static Collection<String> getAllDataTypesAsString(){
		initPrimitives();
		
		return values().stream().map(elem -> elem.name).collect(Collectors.toList());
	}

	public boolean isPrimitive(){
		initPrimitives();
		
		return primitives.containsValue(this);
	}
	
	public static boolean isAllowedTypeName(String typeName){
		return typeName != null && !typeName.equals("") && !typeName.startsWith("_") && !typeName.equals(ANONYMOUS_TYPE_UI);
	}

	/**
	 * String representation of anonymous data types.
	 * @return
	 */
	public static Object getAnonymousDataTypesAsString() {
		return complexTypes.values().stream()
				.map(elem -> elem.name)
				.filter(elem -> elem.startsWith(ANONYMOUS_PREFIX))
				.collect(Collectors.toList());
	}
	
	/**
	 * Remove all complex types to prepare for fresh inference run.
	 */
	public static void clearTypeList(){
		complexTypes.clear();// TODO reflect clearing in full model
	}
}
