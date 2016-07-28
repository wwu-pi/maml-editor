package de.wwu.md2dot0.design;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import de.wwu.md2dot0.dialog.ReorderItemsDialog;
import md2dot0.AutomatedProcessElement;
import md2dot0.Call;
import md2dot0.Camera;
import md2dot0.CheckConstraint;
import md2dot0.ControlFlowElement;
import md2dot0.CreateEntity;
import md2dot0.DataSource;
import md2dot0.DeleteEntity;
import md2dot0.DisplayMessage;
import md2dot0.Event;
import md2dot0.Include;
import md2dot0.LocalDataSource;
import md2dot0.ParameterConnector;
import md2dot0.ProcessConnector;
import md2dot0.ProcessElement;
import md2dot0.ProcessEndEvent;
import md2dot0.ProcessFlowElement;
import md2dot0.RemoteDataSource;
import md2dot0.RetrieveLocation;
import md2dot0.Role;
import md2dot0.SelectEntity;
import md2dot0.ShowEntity;
import md2dot0.SingletonDataSource;
import md2dot0.Transform;
import md2dot0.UpdateEntity;
import md2dot0.UseCaseTrigger;
import md2dot0.Webservice;
import md2dot0gui.ArithmeticOperator;
import md2dot0gui.Attribute;
import md2dot0gui.AvgFunc;
import md2dot0gui.ComputedAttribute;
import md2dot0gui.CountFunc;
import md2dot0gui.MaxFunc;
import md2dot0gui.MinFunc;
import md2dot0gui.MinusOperator;
import md2dot0gui.PlusOperator;
import md2dot0gui.MultiplicationOperator;
import md2dot0gui.DivisionOperator;
import md2dot0gui.Label;
import md2dot0gui.SumFunc;

public class TestService {
	
	public static Map<Role,String> roleIconMapping = new HashMap<Role,String>();

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
	
	// Label field (direct edit!) representation for Parameter Connectors
	public String getParameterConnectorLabelEditText(EObject obj){
		if(obj != null && obj instanceof ParameterConnector){
			ParameterConnector connector = (ParameterConnector) obj;
			
			String labelText = "";
			if(connector.getDescription() != null && connector.getDescription().length() > 0) {
				labelText += connector.getDescription();
			} else if(connector.getTargetElement() != null && connector.getTargetElement().getDescription() != null){
				// Alternative default representation
				if(connector.getTargetElement() instanceof Attribute){
					labelText += Md2dot0Helper.toFirstUpper(connector.getTargetElement().getDescription());
				}
				// Computed attributes or Labels without default
			}
			return labelText;
		}
		return "";
	}
	
	// Label representation for Parameter Connectors
	public String getParameterConnectorLabelText(EObject obj){
		if(obj != null && obj instanceof ParameterConnector){
			ParameterConnector connector = (ParameterConnector) obj;
			
			String labelText = connector.getOrder() + ": \"" + getParameterConnectorLabelEditText(obj) + "\"";
			
			// Special cases
			if(connector.getSourceElement() instanceof ControlFlowElement){
				// Don't show order and label on control flow attribute connections
				labelText = "";
			} else if(connector.getSourceElement() instanceof AutomatedProcessElement){
				// Don't show order and label on automated process flow elements
				labelText = "";
			} else if(connector.getTargetElement() instanceof Label){
				// No label for labels, only order
				labelText = connector.getOrder() + "";
			} else if(connector.getSourceElement() instanceof ComputedAttribute){
				// Don't show order and label on computed attribute
				labelText = "";
			} else if(connector.getSourceElement() instanceof ArithmeticOperator){
				// Only show order on arithmetic operator
				labelText = connector.getOrder() + "";
			} else if (labelText.equals("\"\"")) {
				// Empty text?
				labelText = "<no caption>";
			}
			
			return labelText;
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
	
	// Label representation for Parameter Connectors
	public String getDataSourceLabelText(EObject obj){
		if(obj == null || !(obj instanceof DataSource)){
			return "error";
		}
		
		String labelText = "\"";
		
		if(((DataSource) obj).getTypeName() != null ){
			// Use type given by user
			labelText += ((DataSource) obj).getTypeName();
		} else {
			// Try to retrieve inferred type from incoming elements
			ModelInferenceService service = new ModelInferenceService();
			String result = service.getDataTypeRepresentation(obj);
			if(result != null && result != "??") labelText += result;
		}
		
		labelText += "\"";
		labelText = labelText.equals("\"\"") ? "<no type>" : labelText;
		
		labelText += "\n[";
		
		if(obj instanceof RemoteDataSource) {
			labelText += "Remote";
		} else if(obj instanceof LocalDataSource) {
			labelText += "Local";
		} else if(obj instanceof SingletonDataSource) {
			labelText += "Singleton";
		}
		labelText += "]";
		
		return labelText;
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
		} else if(obj instanceof UseCaseTrigger){
			return "Use Case Trigger";
		} else if(obj instanceof CheckConstraint){
			return "Check";
		} else if(obj instanceof Include){
			return "Include";
		} else if(obj instanceof Webservice){
			return "Webservice";
		} else if(obj instanceof RetrieveLocation){
			return "Location";
		} else if(obj instanceof Transform){
			return "Transform";
		}
		return obj.getClass().getSimpleName();
	}
	
	// Process element type representation
	public static String getComputationOperatorType(EObject obj){
		if(obj instanceof CountFunc){
			return "#";
		} else if(obj instanceof SumFunc){
			return "Sum";
		} else if(obj instanceof MinFunc){
			return "Min";
		} else if(obj instanceof MaxFunc){
			return "Max";
		} else if(obj instanceof AvgFunc){
			return "Avg";
		} else if(obj instanceof PlusOperator){
			return "+";
		} else if(obj instanceof MinusOperator){
			return "-";
		} else if(obj instanceof DivisionOperator){
			return "/";
		} else if(obj instanceof MultiplicationOperator){
			return "*";
		}
		return obj.getClass().getSimpleName();
	}
	
	// Check is setting process edge is allowed
	public boolean isConnectionStartAllowed(EObject elem, EObject preSource){
		// For certain elements there is only one connection allowed. For reconnection we need to 
		// allow a second temporary connection. The old connector is passed as elem in this case.
		int maxConnections = 1;
		if(elem instanceof ProcessConnector && ((ProcessFlowElement) preSource).getNextElements().contains(elem)){
			maxConnections++;
		}
		
		if(preSource instanceof Event){
			// Events hav max 1 outgoing edge
			return ((Event) preSource).getNextElements().size() < maxConnections;
		} else if (preSource instanceof ControlFlowElement){
			// Control flow elements can have multiple outgoing edges
			return true;
		} else if (preSource instanceof ProcessElement){
			// Process elements can have max 1 outgoing edge
			return ((ProcessElement) preSource).getNextElements().size() < maxConnections;
		} else if (preSource instanceof DataSource){
			// Data source elements can have max 1 outgoing edge
			return ((DataSource) preSource).getNextElements().size() < maxConnections;
		} 
		return false;
	}
	
	public boolean isConnectionEndAllowed(EObject elem, EObject preSource, EObject preTarget){
		// Check preconditions on source side
		if(!isConnectionStartAllowed(elem, preSource)) return false;
		
		// For certain elements there is only one connection allowed.
		int maxConnections = 1;
		if(preTarget instanceof ProcessFlowElement && ((ProcessFlowElement) preTarget).getPreviousElements().contains(elem)){
			// For reconnection we need to check if the connector is already part of the previous elements 
			// (and allow a second temporary connection). The old connector is passed as elem in this case.
			maxConnections++;
		} else if(preSource instanceof ControlFlowElement){
			// XOR elements can have multiple cases.
			maxConnections++;
		}
		
		if(preTarget instanceof ProcessEndEvent){
			// Only end event have an incoming edge
			return ((ProcessEndEvent) preTarget).getPreviousElements().size() < maxConnections;
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
				return ((ProcessElement) preTarget).getPreviousElements().size() < maxConnections;
			}
		} else if (preTarget instanceof DataSource){
			// Data source elements may not link on themselves
			if(preSource instanceof DataSource && ((DataSource) preTarget).equals((DataSource) preSource)){
				return false;
			} else {
				// Data source elements can have max 1 incoming edge
				
				return ((DataSource) preTarget).getPreviousElements().size() < maxConnections;
			}
		}
		return false;
	}
	
	public boolean isProcessSourceReconnectionAllowed(EObject elem, EObject preSource){
		if(!(elem instanceof ProcessConnector)){
			return false;
		}
		
		ProcessConnector edge = (ProcessConnector) elem;
		
		return isConnectionEndAllowed(elem, preSource, edge.getTargetProcessFlowElement());
	}
	
	public boolean isProcessTargetReconnectionAllowed(EObject elem, EObject preTarget){
		if(!(elem instanceof ProcessConnector)){
			return false;
		}
		
		ProcessConnector edge = (ProcessConnector) elem;
		
		return isConnectionEndAllowed(elem, edge.getSourceProcessFlowElement(), preTarget);
	}
	
	public String getRoleImageBitmap(EObject obj){
		return getRoleImage(obj, false);
	}
	
	public String getRoleImageVector(EObject obj){
		return getRoleImage(obj, true);
	}
	
	public String getRoleImage(EObject obj, boolean vector){
		if(!(obj instanceof Role)) return null;
		Role role = (Role) obj;
		
		// Return role icon if known
		if(roleIconMapping.get(role) == null) {
			// Add role mapping 
			ArrayList<String> roleIcons = new ArrayList<String>();
			roleIcons.add("/de.wwu.md2dot0.design/icons/woman");
			roleIcons.add("/de.wwu.md2dot0.design/icons/man");
			roleIcons.add("/de.wwu.md2dot0.design/icons/woman_green");
			roleIcons.add("/de.wwu.md2dot0.design/icons/man_orange");
			
			// Modulo to cycle through icons if not enough
			roleIconMapping.put(role, roleIcons.get(roleIconMapping.size() % roleIcons.size()));
		}
		
		if(vector){
			return roleIconMapping.get(role) + ".svg";
		} else {
			return roleIconMapping.get(role) + ".png";
		}
	}
	
	/**
	 * Get pixel width of an element based on its textual content.
	 * CANNOT be used currently because size computation renders square field from weird integer value 
	 * @param content
	 * @return
	 */
//	public int getSize(EObject content){
//		GC gc = new GC(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		FontMetrics fontMetrics = gc.getFontMetrics();
//		gc.dispose();
//		int width = fontMetrics.getAverageCharWidth() * ((content != null) ? content.toString().toCharArray().length : 0);
//		System.out.println(width);
//		return width;
//	}
	
	/**
	 * Reorder elements
	 */
	public EObject openReorderAttributeWizard(EObject object){
		if(!(object instanceof ParameterConnector)) return object;
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		ReorderItemsDialog dialog = new ReorderItemsDialog(shell);
		
		List<Object> parameters = ((ParameterConnector) object).getSourceElement().getParameters()
				.stream().sorted(new Comparator<ParameterConnector>(){
					@Override
					public int compare(ParameterConnector arg0, ParameterConnector arg1) {
						return arg0.getOrder() - arg1.getOrder();
					}
					
				}).collect(Collectors.toList());
		
		Function<Object, String> itemHumanDescription = elem -> getParameterConnectorLabelEditText((ParameterConnector) elem);
		dialog.setElements(parameters, itemHumanDescription);
		dialog.setTitle("Reorder attributes");
		dialog.setLabelText("Please bring the attached GUI elements into the desired order or appearance on the screen:\n\n");
		
		// User pressed cancel
		if (dialog.open() != Window.OK) {
			return object; // Do nothing
		}
		
		// Reorder parameters
		Object[] result = dialog.getResult();
		for(int i=0; i < result.length; i++){
			((ParameterConnector) result[i]).setOrder(i+1); 
		}
		
		return object;
	}
}
