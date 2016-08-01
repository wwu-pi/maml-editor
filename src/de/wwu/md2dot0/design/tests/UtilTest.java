package de.wwu.md2dot0.design.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.wwu.md2dot0.design.Md2dot0Helper;

public class UtilTest {

	@Test
	public void testAllowedAttributeName(){
		// Should work
		String testText = "attributeName";
		assertEquals(testText, Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "attribute-name";
		assertEquals(testText, Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "attribute_Name";
		assertEquals(testText, Md2dot0Helper.getAllowedAttributeName(testText));
		
		// Default first lower
		testText = "AttributeName";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		// a-zA-Z first character
		testText = "_attributeName";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "-attributeName";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		// Spaces in name
		testText = " attributeName";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "attributeName ";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = " attributeName ";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "attribute name";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		// Filter invalid characters
		testText = "attribute.name";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
		
		testText = "attribute!#$%^&*+=name";
		assertEquals("attributeName", Md2dot0Helper.getAllowedAttributeName(testText));
	}
	
	@Test
	public void testAllowedDataTypeName(){
		// Should work
		String testText = "TypeName";
		assertEquals(testText, Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "Type-name";
		assertEquals(testText, Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "Type_Name";
		assertEquals(testText, Md2dot0Helper.getAllowedDataTypeName(testText));
		
		// Default first upper
		testText = "typeName";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		// a-zA-Z first character
		testText = "_typeName";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "-typeName";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		// Spaces in name
		testText = " typeName";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "typeName ";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = " typeName ";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "type name";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		// Filter invalid characters
		testText = "type.name";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
		
		testText = "type!#$%^&*+=name";
		assertEquals("TypeName", Md2dot0Helper.getAllowedDataTypeName(testText));
	}
}
