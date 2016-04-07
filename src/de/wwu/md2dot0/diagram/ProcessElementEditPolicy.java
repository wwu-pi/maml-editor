package de.wwu.md2dot0.diagram;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gmf.runtime.diagram.ui.internal.editpolicies.LabelSnapBackEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;

@SuppressWarnings("restriction")
public class ProcessElementEditPolicy extends DirectEditPolicy {
	
	public ProcessElementEditPolicy(){
		super();
	}
	
	@Override
	public Command getCommand(Request request) {
		// TODO Auto-generated method stub
		System.out.println(request.getType());
		return super.getCommand(request);
	}
	


	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		// TODO Auto-generated method stub
		
	}

}
