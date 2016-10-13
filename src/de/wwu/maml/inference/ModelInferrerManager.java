package de.wwu.maml.inference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.wwu.maml.dsl.maml.UseCase;

/**
 * Manages model inferrer components (one created per use case)
 * @author c_rieg01
 *
 */
public final class ModelInferrerManager {

	private static ModelInferrerManager singleton;
	
	protected Map<UseCase, ModelInferrer> inferrers = new HashMap<UseCase, ModelInferrer>(); // TODO ConcurrentHashMap?
	
	private ModelInferrerManager(){
		// Singleton constructor
	}
	
	public synchronized static ModelInferrerManager getInstance(){
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
	public synchronized ModelInferrer getModelInferrer(UseCase useCase){
		// Create new if not exists for given use case
		if(inferrers.get(useCase) == null){
			inferrers.put(useCase, new ModelInferrer());
			DynamicTypeLiteral.setDataTypeContainer(useCase);
		}
		
		ModelInferrer inferrer = inferrers.get(useCase);
		// Refresh inference automatically when called (maximum every 0.75 second)
		// There is some unsolved issue with two Singleton objects hovering around (although 
		// synchronized, static, one classloader etc.). Therefore a short update time is 
		// used to keep the visual element refresh (one object) in sync with 
		// the inference after an observed change (other object)  
		if(inferrer.getLastInference() == null || new Date().getTime() - inferrer.getLastInference().getTime() > 750) { 
			inferrer.startInferenceProcess(useCase, true);
		} else {
			System.out.println(inferrer + " inferred at " + inferrer.getLastInference().getTime());
		}
		
		return inferrer;
	}
}
