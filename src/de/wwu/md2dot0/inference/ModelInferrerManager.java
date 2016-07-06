package de.wwu.md2dot0.inference;

import java.util.HashMap;
import java.util.Map;

import md2dot0.UseCase;

/**
 * Manages model inferrer components (one created per use case)
 * @author c_rieg01
 *
 */
public class ModelInferrerManager {

	protected static ModelInferrerManager singleton;
	protected Map<UseCase, ModelInferrer> inferrers = new HashMap<UseCase, ModelInferrer>();
	
	private ModelInferrerManager(){
		// Singleton constructor
	}
	
	public static ModelInferrerManager getInstance(){
		if(singleton == null){
			singleton = new ModelInferrerManager();
		}
		
		return singleton;
	}
	
	/**
	 * Retrieve inference component for the specified use case or create a new one if none is active
	 * @param useCase
	 * @return
	 */
	public ModelInferrer getModelInferrer(UseCase useCase){
		// Create new if not exists for given use case
		if(inferrers.get(useCase) == null){
			inferrers.put(useCase, new ModelInferrer());
		}
		return inferrers.get(useCase);
	}
}
