package de.wwu.maml.editor.diagram;

import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.CustomStyle;

public class InteractionProcessElementEditPartProvider extends AbstractEditPartProvider {

	@Override
	protected Class<?> getNodeEditPartClass(View view) {
		if (view.getElement() instanceof CustomStyle) {
			CustomStyle customStyle = (CustomStyle) view.getElement();
			if (customStyle.getId().equals("de.wwu.md2dot0.diagram.InteractionProcessElement")) {
				return InteractionProcessElementEditPart.class;
			}
		}
		return super.getNodeEditPartClass(view);
	}
}