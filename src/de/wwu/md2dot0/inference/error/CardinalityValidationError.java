package de.wwu.md2dot0.inference.error;

import md2dot0.ParameterSource;

public class CardinalityValidationError extends MamlValidationError {

	public CardinalityValidationError(ParameterSource element) {
		super(element);
	}
	
	public CardinalityValidationError(ParameterSource element, Object[] options) {
		super(element, options);
	}

	@Override
	public String getErrorText() {
		return "Cardinality of the attribute inconsistent! Only one is allowed: " + options.toString();
	}	
}
