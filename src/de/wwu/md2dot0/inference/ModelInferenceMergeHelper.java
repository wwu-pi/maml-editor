package de.wwu.md2dot0.inference;

import org.eclipse.emf.common.util.EList;

import md2dot0.ProcessFlowElement;

public class ModelInferenceMergeHelper {
	
	ModelInferenceDataTypeHelper inferenceDataTypeHelper;

	public void mergeProcessElements(EList<ProcessFlowElement> processFlowElements,
			ModelInferenceDataTypeHelper inferenceDataTypeHelper) {
		this.inferenceDataTypeHelper = inferenceDataTypeHelper;
		
		// TODO merge individual process flow elements
	}

}
