package de.wwu.maml.editor.diagram;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

//import de.wwu.maml.editor.service.ModelInferenceService;
import de.wwu.maml.dsl.maml.UseCase;

public class ModelInferenceAction implements IExternalJavaAction {

	public ModelInferenceAction() {
		
	}

	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		Optional<UseCase> hasUseCase = arg0.stream()
				.filter(elem -> elem instanceof UseCase)
				.map(elem -> (UseCase) elem)
				.findFirst();
		return hasUseCase.isPresent();
	}

	@Override
	public void execute(Collection<? extends EObject> arg0, Map<String, Object> arg1) {
		//ModelInferenceService inferenceService = new ModelInferenceService();
		//inferenceService.startInferenceProcess(arg0);
		System.out.println("HALLO TEST");
	}

}
