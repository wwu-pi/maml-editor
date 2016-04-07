package de.wwu.md2dot0.diagram;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.internal.editpolicies.LabelSnapBackEditPolicy;

@SuppressWarnings("restriction")
public class ProcessElementEditPolicy extends LabelSnapBackEditPolicy {
	
	public ProcessElementEditPolicy(){
		super();
	}
	
	@Override
	public Command getCommand(Request request) {
		// TODO Auto-generated method stub
		System.out.println(request.getType());
		return super.getCommand(request);
	}

}
