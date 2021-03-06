package de.wwu.maml.editor.diagram;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AbstractTransparentRectangle;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SiriusWrapLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class InteractionProcessElementFigure extends AbstractTransparentRectangle {

	protected Color processElementBackgroundColor = new Color(Display.getCurrent(), 107, 218, 247);
	protected Color processElementBorderColor = new Color(Display.getCurrent(), 0, 0, 0);
	protected Color processElementLabelColor = new Color(Display.getCurrent(), 0, 0, 1);

	protected Polygon arrowShape;
	protected Polyline lineUpper;
	protected Polyline lineLower;
	protected Label labelUpper;
	protected Label labelLower;
	protected SiriusWrapLabel labelMain;

	public InteractionProcessElementFigure(CustomStyle style) {
		setLayoutManager(new XYLayout());

		arrowShape = new Polygon();
		arrowShape.setBackgroundColor(processElementBackgroundColor);
		arrowShape.setFill(true);
		arrowShape.setForegroundColor(processElementBorderColor);
		arrowShape.setOutline(true);
		add(arrowShape);

		lineUpper = new Polyline();
		lineUpper.setForegroundColor(processElementBorderColor);
		lineUpper.setLineWidth(1);
		add(lineUpper);

		lineLower = new Polyline();
		lineLower.setForegroundColor(processElementBorderColor);
		lineLower.setLineWidth(1);
		add(lineLower);

		labelUpper = new Label("Test");
		add(labelUpper);

		labelLower = new Label("TestDown");
		add(labelLower);
	}

	@Override
	public void paintFigure(Graphics graphics) {
		// super.paintFigure(graphics);
		Rectangle bounds = getBounds().getCopy();
		int width = bounds.width;// <= 100 ? 100 : bounds.width;
		int height = bounds.height;// <= 50 ? 50 : bounds.height;

		int[] coordsArrow = { bounds.x + 0, bounds.y + 0, 
				bounds.x + (int) (width * 0.8), bounds.y + 0,
				bounds.x + width - 1, bounds.y + (int) (height / 2), 
				bounds.x + (int) (width * 0.8), bounds.y + height - 1, 
				bounds.x + 0, bounds.y + height - 1, 
				bounds.x + 0, bounds.y + 0 };
		arrowShape.setPoints(new PointList(coordsArrow));

		int[] coordsLineUpper = { bounds.x + 0, bounds.y + (int) (height * 0.25), 
				bounds.x + (int) (width * 0.9),
				bounds.y + (int) (height * 0.25) };
		lineUpper.setPoints(new PointList(coordsLineUpper));

		int[] coordsLineLower = { bounds.x + 0, bounds.y + (int) (height * 0.75), 
				bounds.x + (int) (width * 0.9),
				bounds.y + (int) (height * 0.75) };
		lineLower.setPoints(new PointList(coordsLineLower));

		// Use current colors
		labelUpper.setForegroundColor(processElementLabelColor);
		labelLower.setForegroundColor(processElementLabelColor);

		// Positioning
		setConstraint(arrowShape, new Rectangle(0, 0, width, height));
		setConstraint(labelUpper, new Rectangle(0, 0, (int) (width * 0.85), (int) (height * 0.25)));
		setConstraint(labelLower, new Rectangle(0, (int) (height * 0.75), (int) (width * 0.85), (int) (height * 0.25)));
		
		// Find automatically inserted Label
		if(labelMain == null){
			for(Object child : this.getChildren()){
				if(child instanceof SiriusWrapLabel){
					labelMain = (SiriusWrapLabel) child;
				}
			}
		}
		if(labelMain != null){
			setConstraint(labelMain, new Rectangle(0, (int) (height * 0.25), (int) (width * 0.85), (int) (height * 0.5)));
		}
	}

	public void setProcessElementDataType(String type) {
		if (type == null)
			type = "";
		labelLower.setText(type);
	}

	public void setProcessElementType(String type) {
		if (type == null)
			type = "";
		labelUpper.setText(type);
	}

	// setLayoutManager(new XYLayout());
	//
	// // Use provided styling if exists
	// if(style != null){
	// processElementBorderColor = new Color(Display.getCurrent(),
	// style.getBorderColor().getRed(), style.getBorderColor().getGreen(),
	// style.getBorderColor().getBlue());
	// processElementLabelColor = new Color(Display.getCurrent(),
	// style.getLabelColor().getRed(), style.getLabelColor().getGreen(),
	// style.getLabelColor().getBlue());
	// }
}
