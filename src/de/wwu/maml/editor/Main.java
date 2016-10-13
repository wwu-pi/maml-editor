package de.wwu.maml.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import de.wwu.md2dot0.inference.DynamicTypeLiteral;
import de.wwu.md2dot0.inference.ModelInferrer;
import de.wwu.maml.dsl.maml.Md2dot0Package;
import de.wwu.maml.dsl.maml.UseCase;

public class Main {
	// Test model inference with existing model
	public static void main(String[] args) {
		// Register md2dot0 package, otherwise error when loading
		@SuppressWarnings("unused")
		Md2dot0Package packageInstance = Md2dot0Package.eINSTANCE;

		XMIResourceImpl resource = new XMIResourceImpl();
		File source = new File("C:/Users/c_rieg01/workspaceSirius/de.wwu.md2dot0.design/resources/UseCase2.md2dot0");
		try {
			resource.load(new FileInputStream(source), new HashMap<Object, Object>());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UseCase data = (UseCase) resource.getContents().get(0);

		DynamicTypeLiteral.setDataTypeContainer(data);
		// Start via ModelInferrer (bypassing the Sirius ModelInferenceService layer)
		ModelInferrer inferrer = new ModelInferrer();
		inferrer.startInferenceProcess(data, false);
		
		System.out.println("Erfolgreich und fertig!");
	}
}
