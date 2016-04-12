package de.wwu.md2dot0.design;

import org.eclipse.emf.ecore.EObject;

import md2dot0.Call;
import md2dot0.Camera;
import md2dot0.CreateEntity;
import md2dot0.DeleteEntity;
import md2dot0.DisplayMessage;
import md2dot0.ParameterConnector;
import md2dot0.ProcessElement;
import md2dot0.SelectEntity;
import md2dot0.ShowEntity;
import md2dot0.UpdateEntity;

public class TestService {

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
	
	// Label representation for Parameter Connectors
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
	
	// Process element type representation
	public static String getProcessElementType(ProcessElement obj){
		if(obj instanceof ShowEntity){
			return ShowEntity.class.getSimpleName();
		} else if(obj instanceof SelectEntity){
			return SelectEntity.class.getSimpleName();
		} else if(obj instanceof CreateEntity){
			return CreateEntity.class.getSimpleName();
		} else if(obj instanceof UpdateEntity){
			return UpdateEntity.class.getSimpleName();
		} else if(obj instanceof DeleteEntity){
			return DeleteEntity.class.getSimpleName();
		} else if(obj instanceof DisplayMessage){
			return DisplayMessage.class.getSimpleName();
		} else if(obj instanceof Call){
			return Call.class.getSimpleName();
		} else if(obj instanceof Camera){
			return Camera.class.getSimpleName();
		}
		return obj.getClass().getSimpleName();
	}
}
