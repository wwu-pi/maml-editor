package de.wwu.md2dot0.inference.error;

import md2dot0.ParameterSource;

public class TypeValidationError extends MamlValidationError {

	public TypeValidationError(ParameterSource element) {
		super(element);
	}
	
	public TypeValidationError(ParameterSource element, Object[] options) {
		super(element, options);
	}

	@Override
	public String getErrorText() {
		return "Attribute type inconsistent! Only one is allowed: " + options.toString();
	}	
}
