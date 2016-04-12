package de.wwu.md2dot0.design;

import org.eclipse.emf.ecore.EObject;

import md2dot0.Call;
import md2dot0.Camera;
import md2dot0.ControlFlowElement;
import md2dot0.CreateEntity;
import md2dot0.DeleteEntity;
import md2dot0.DisplayMessage;
import md2dot0.Event;
import md2dot0.ParameterConnector;
import md2dot0.ProcessConnector;
import md2dot0.ProcessElement;
import md2dot0.ProcessEndEvent;
import md2dot0.SelectEntity;
import md2dot0.ShowEntity;
import md2dot0.UpdateEntity;

public class TestService {

	public boolean debugTrue(EObject elem) {
		System.out.println(elem);
		return true;
	}
	
	public EObject debugObject(EObject elem) {
		System.out.println(elem);
		return elem;
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
			labelText += "\"";
			
			return labelText.equals("\"\"") ? "" : labelText;
		}
		return "error";
	}
	
	// Label representation for Parameter Connectors
	public String getProcessConnectorLabelText(EObject obj){
		if(obj != null && obj instanceof ProcessConnector){
			ProcessConnector connector = (ProcessConnector) obj;
			
			String labelText = "\"";
			if(connector.getDescription() != null && connector.getDescription().length() > 0) {
				labelText += connector.getDescription();
			}
			labelText += "\"";
			
			return labelText.equals("\"\"") ? "" : labelText;
		}
		return "error";
	}
	
	// Process element type representation
	public static String getProcessElementType(ProcessElement obj){
		if(obj instanceof ShowEntity){
			return "Show entity";
		} else if(obj instanceof SelectEntity){
			return "Select entity";
		} else if(obj instanceof CreateEntity){
			return "Create entity";
		} else if(obj instanceof UpdateEntity){
			return "Update entity";
		} else if(obj instanceof DeleteEntity){
			return "Delete entity";
		} else if(obj instanceof DisplayMessage){
			return "Display message";
		} else if(obj instanceof Call){
			return "Call";
		} else if(obj instanceof Camera){
			return "Camera";
		}
		return obj.getClass().getSimpleName();
	}
	
	// Check is setting process edge is allowed
	public boolean isConnectionStartAllowed(EObject elem, EObject preSource){
		if(preSource instanceof Event){
			// Events hav max 1 outgoing edge
			return ((Event) preSource).getNextElements().size() < 1;
		} else if (preSource instanceof ControlFlowElement){
			// Control flow elements can have multiple outgoing edges
			return true;
		} else if (preSource instanceof ProcessElement){
			// Process elements can have max 1 outgoing edge
			return ((ProcessElement) preSource).getNextElements().size() < 1;
		} 
		return false;
	}
	
	public boolean isConnectionEndAllowed(EObject elem, EObject preSource, EObject preTarget){
		// Check preconditions on source side
		if(!isConnectionStartAllowed(elem, preSource)) return false;
		
		if(preTarget instanceof ProcessEndEvent){
			// Only end event have an incoming edge
			return ((ProcessEndEvent) preTarget).getPreviousElements().size() < 1;
		} else if(preTarget instanceof Event){
			// Other events have no incoming edge
			return false;
		} else if (preTarget instanceof ControlFlowElement){
			// Control flow elements can have multiple incoming edges
			return true;
		} else if (preTarget instanceof ProcessElement){
			// Process elements may not link on themselves
			if(preSource instanceof ProcessElement && ((ProcessElement) preTarget).equals((ProcessElement) preSource)){
				return false;
			} else {
				// Process elements can have max 1 incoming edge
				return ((ProcessElement) preTarget).getPreviousElements().size() < 1;
			}
		}
		return false;
	}
}
