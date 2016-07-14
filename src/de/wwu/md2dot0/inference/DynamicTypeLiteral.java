package de.wwu.md2dot0.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import md2dot0.UseCase;
import md2dot0data.DataTypeLiteral;
import md2dot0data.impl.DataTypeLiteralImpl;

public class DynamicTypeLiteral extends DataTypeLiteralImpl {
	
	public static final String ANONYMOUS_PREFIX = "__ANONYMOUS__";
	private static final String ANONYMOUS_TYPE_UI = "X";

	static UseCase container;
	
	public DynamicTypeLiteral(String identifier, String name, boolean isPrimitive) {
		this.identifier = identifier;
		this.name = name;
		this.primitive = isPrimitive;
		
		if(container != null){
			container.getDataTypes().add(this);
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
		if(getTypes().values().stream().filter(elem -> elem.isPrimitive()).collect(Collectors.toList()).size() == 0){
			getTypes().put("STRING", new DynamicTypeLiteral("STRING", "String", true));
			getTypes().put("BOOLEAN", new DynamicTypeLiteral("BOOLEAN", "Boolean", true));
//			getTypes().put("PHONENUMBER", new DynamicTypeLiteral("PHONENUMBER", "PhoneNumber", true));
//			getTypes().put("URL", new DynamicTypeLiteral("URL", "Url", true));
//			getTypes().put("EMAIL", new DynamicTypeLiteral("EMAIL", "Email", true));
//			getTypes().put("FILE", new DynamicTypeLiteral("FILE", "File", true));
//			getTypes().put("IMAGE", new DynamicTypeLiteral("IMAGE", "Image", true));
//			getTypes().put("LOCATION", new DynamicTypeLiteral("LOCATION", "Location", true));
//			getTypes().put("INTEGER", new DynamicTypeLiteral("INTEGER", "Integer", true));
//			getTypes().put("FLOAT", new DynamicTypeLiteral("FLOAT", "Float", true));
//			getTypes().put("DATE", new DynamicTypeLiteral("DATE", "Date", true));
//			getTypes().put("TIME", new DynamicTypeLiteral("TIME", "Time", true));
//			getTypes().put("DATETIME", new DynamicTypeLiteral("DATETIME", "DateTime", true));
		}
	}

	/**
	 * Get or create a type literal for the given string.
	 * @param string
	 * @return
	 */
	public static DataTypeLiteral from(String string) {
		initPrimitives();
		
		// Prepare user input
		String type = string.toUpperCase().trim();
		
		// Catch invalid types
		if(!isAllowedTypeName(type)) return null;
				
		// Lookup or create new
		if(!literalExists(type)){
			getTypes().put(type, new DynamicTypeLiteral(type, string.trim(), false));
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
	private static DataTypeLiteral getLiteral(String type){
		return getTypes().get(type);  
	}
	
	/**
	 * List all know data type literals.
	 * @return
	 */
	public static Collection<DataTypeLiteral> values(){
		initPrimitives();
		
		return getTypes().values();
	}
	
	/**
	 * String representation of primitive data getTypes().
	 * @return
	 */
	public static Collection<String> getPrimitiveDataTypesAsString(){
		initPrimitives();
		
		return getTypes().values().stream().filter(elem -> elem.isPrimitive()).map(elem -> elem.getName()).collect(Collectors.toList());
	}
	
	/**
	 * String representation of custom data getTypes().
	 * @return
	 */
	public static Collection<String> getCustomDataTypesAsString(){
		return getTypes().values().stream()
				.filter(elem -> !elem.isPrimitive())
				.map(elem -> elem.getName())
				.filter(elem -> !elem.startsWith(ANONYMOUS_PREFIX))
				.collect(Collectors.toList());
	}

	/**
	 * String representation of all data getTypes().
	 * @return
	 */
	public static Collection<String> getAllDataTypesAsString(){
		initPrimitives();
		
		return values().stream().map(elem -> elem.getName()).collect(Collectors.toList());
	}

	public boolean isPrimitive(){
		return primitive;
	}
	
	public static boolean isAllowedTypeName(String typeName){
		return typeName != null && !typeName.equals("") && !typeName.startsWith("_") && !typeName.equals(ANONYMOUS_TYPE_UI);
	}

	/**
	 * String representation of anonymous data getTypes().
	 * @return
	 */
	public static Collection<String> getAnonymousDataTypesAsString() {
		return getTypes().values().stream()
				.map(elem -> elem.getName())
				.filter(elem -> elem.startsWith(ANONYMOUS_PREFIX))
				.collect(Collectors.toList());
	}
	
	/**
	 * Remove all complex types to prepare for fresh inference run.
	 */
	public static void clearTypeList(){
		getTypes().clear();// TODO reflect clearing in full model
	}
	
	public static Map<String,DataTypeLiteral> getTypes(){
		if(container == null) throw new IllegalStateException("No container initialized in DynamicTypeLiteral!");
		
		Map<String,DataTypeLiteral> types = container.getDataTypes().stream()
				.filter(elem -> elem instanceof DataTypeLiteral)
				.map(elem -> (DataTypeLiteral) elem)
				.collect(Collectors.toMap(DataTypeLiteral::getIdentifier, Function.<DataTypeLiteral>identity()));
		return types;
	}
}
