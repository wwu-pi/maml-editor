package de.wwu.md2dot0.design;

import org.eclipse.emf.ecore.EObject;

import md2dot0.ParameterConnector;
import md2dot0.ProcessElement;

public class TestService {

	public String getProcessElementType(ProcessElement ePackage) {
		return "hallo";
	}

	public boolean debugTrue(EObject elem) {
		System.out.println(elem);
		return true;
	}
	
	public boolean debugTwo(EObject elem1, EObject elem2) {
		System.out.println(elem1 + "..." + elem2);
		return true;
	}

	public String debugString(EObject elem) {
		System.out.println(elem);
		return "Test";
	}
	
	public String getParameterConnectorLabelText(EObject obj){
		if(obj != null && obj instanceof ParameterConnector){
			ParameterConnector connector = (ParameterConnector) obj;
			
			String labelText = connector.getOrder() + ": \"";
			if(connector.getDescription() != null && connector.getDescription().length() > 0) {
				labelText += connector.getDescription();
			} else if(connector.getTargetElement() != null && connector.getTargetElement().getDescription() != null){
				labelText += connector.getTargetElement().getDescription();
			}
			return labelText + "\"";
		}
		return "error";
	}
}
