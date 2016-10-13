package de.wwu.maml.inference.error;

import de.wwu.maml.dsl.maml.ParameterSource;

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
