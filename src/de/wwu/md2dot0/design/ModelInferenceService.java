package de.wwu.md2dot0.design;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;
import md2dot0data.DataType;

public class ModelInferenceService {
	
	Map<ProcessFlowElement, DataType> elementTypes = new HashMap<ProcessFlowElement, DataType>();

	public String getDataTypeRepresentation(EObject obj){
		if(!(obj instanceof ProcessFlowElement)) return "error";
		
		startInferenceProcess((UseCase) obj.eContainer()); // Container is the use case itself
		
		DataType type = getType((ProcessFlowElement) obj);
		if(type != null){
			return type.getClass().getSimpleName();
		}
			
		return "??";
	}

	protected DataType getType(ProcessFlowElement obj) {
		return elementTypes.get(obj);
	}
	
	protected void startInferenceProcess(UseCase useCase) {
		// TODO Auto-generated method stub
		
	}
	
}
