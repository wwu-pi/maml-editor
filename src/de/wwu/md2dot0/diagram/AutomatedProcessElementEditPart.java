package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.CustomStyle;

public class AutomatedProcessElementEditPart extends InteractionProcessElementEditPart {

	public AutomatedProcessElementEditPart(View view) {
		super(view);
	}

	@Override
	protected IFigure createNodeShape() {
		InteractionProcessElementFigure arrowShape = new AutomatedProcessElementFigure((CustomStyle) this.resolveSemanticElement());
		
		return primaryShape = arrowShape;
	}
}
