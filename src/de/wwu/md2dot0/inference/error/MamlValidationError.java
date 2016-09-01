package de.wwu.md2dot0.inference.error;

import md2dot0.ParameterSource;

public abstract class MamlValidationError {

	protected ParameterSource element;
	protected Object[] options;
	
	public MamlValidationError(ParameterSource element) {
		this.element = element;
	}
	
	public MamlValidationError(ParameterSource element, Object[] options) {
		this.element = element;
		this.options = options;
	}
	
	public ParameterSource getElement() {
		return element;
	}

	public void setElement(ParameterSource element) {
		this.element = element;
	}

	public abstract String getErrorText();
}
