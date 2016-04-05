package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrapLabel;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.IStyleConfigurationProvider;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.SimpleStyleConfiguration;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.StyleConfiguration;
import org.eclipse.sirius.viewpoint.Style;

public class StyleConfigurationProvider implements IStyleConfigurationProvider {

	public StyleConfigurationProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean provides(DiagramElementMapping mapping, Style style) {
		/*if (mapping.getName().toLowerCase().startsWith("artifact"))*/
		System.out.println(mapping.getName());
		if (mapping.getName().toLowerCase().startsWith("artifact"))
			return true;
		else
			return true; //false;
	}

	@Override
	public StyleConfiguration createStyleConfiguration(
			DiagramElementMapping mapping, Style style) {
		return new ArtifactStyleConfiguration();
	}

	void adaptNodeLabel(DNode node, WrapLabel nodeLabel){
		Triangle t = new Triangle();
		t.setBounds(new Rectangle(nodeLabel.getParent().getBounds().x, nodeLabel.getParent().getBounds().y, nodeLabel.getParent().getBounds().height, nodeLabel.getParent().getBounds().width));
		
				System.out.println("Triangle created");
		nodeLabel.add(t);
		
	}
}
