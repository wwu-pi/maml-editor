package de.wwu.md2dot0.design;

import org.eclipse.emf.ecore.EObject;

import md2dot0.ProcessElement;

public class TestService {

	public String getProcessElementType(ProcessElement ePackage) {
		return "hallo";
	}

	public boolean debugTrue(EObject elem) {
		System.out.println(elem);
		return true;
	}

	public String debugString(EObject elem) {
		System.out.println(elem);
		return "Test";
	}
}
