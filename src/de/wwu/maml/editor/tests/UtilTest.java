package de.wwu.maml.editor.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.wwu.maml.editor.service.MamlHelper;

public class UtilTest {

	@Test
	public void testAllowedAttributeName(){
		// Should work
		String testText = "attributeName";
		assertEquals(testText, MamlHelper.getAllowedAttributeName(testText));
		
		testText = "attribute-name";
		assertEquals(testText, MamlHelper.getAllowedAttributeName(testText));
		
		testText = "attribute_Name";
		assertEquals(testText, MamlHelper.getAllowedAttributeName(testText));
		
		// Default first lower
		testText = "AttributeName";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		// a-zA-Z first character
		testText = "_attributeName";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		testText = "-attributeName";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		// Spaces in name
		testText = " attributeName";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		testText = "attributeName ";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		testText = " attributeName ";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		testText = "attribute name";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		// Filter invalid characters
		testText = "attribute.name";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
		
		testText = "attribute!#$%^&*+=name";
		assertEquals("attributeName", MamlHelper.getAllowedAttributeName(testText));
	}
	
	@Test
	public void testAllowedDataTypeName(){
		// Should work
		String testText = "TypeName";
		assertEquals(testText, MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "Type-name";
		assertEquals(testText, MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "Type_Name";
		assertEquals(testText, MamlHelper.getAllowedDataTypeName(testText));
		
		// Default first upper
		testText = "typeName";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		// a-zA-Z first character
		testText = "_typeName";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "-typeName";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		// Spaces in name
		testText = " typeName";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "typeName ";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		testText = " typeName ";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "type name";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		// Filter invalid characters
		testText = "type.name";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
		
		testText = "type!#$%^&*+=name";
		assertEquals("TypeName", MamlHelper.getAllowedDataTypeName(testText));
	}
}
