package de.wwu.md2dot0.diagram;

import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DiagramPackage;

public class ProcessElementEditPartProvider extends AbstractEditPartProvider {

	@Override
	protected Class<?> getNodeEditPartClass(View view) {
		System.out.println("Hier");
		if (view.getElement() instanceof CustomStyle) {
			CustomStyle customStyle = (CustomStyle) view.getElement();
			if (customStyle.getId().equals("de.wwu.md2dot0.diagram.ProcessElement")) {
				return ProcessElementEditPart.class;
			}
		}
		return super.getNodeEditPartClass(view);
	}
}