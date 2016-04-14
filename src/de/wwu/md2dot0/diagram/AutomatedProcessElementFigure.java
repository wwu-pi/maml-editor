package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.sirius.diagram.CustomStyle;

public class AutomatedProcessElementFigure extends InteractionProcessElementFigure {

	public AutomatedProcessElementFigure(CustomStyle style) {
		super(style);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		Rectangle bounds = getBounds().getCopy();
		int width = bounds.width;// <= 100 ? 100 : bounds.width;
		int height = bounds.height;// <= 50 ? 50 : bounds.height;

		int[] coordsArrow = { bounds.x + 0, bounds.y + 0, 
				bounds.x + width - 1, bounds.y + 0, 
				bounds.x + width - 1, bounds.y + height - 1, 
				bounds.x + 0, bounds.y + height - 1, 
				bounds.x + 0, bounds.y + 0 };
		arrowShape.setPoints(new PointList(coordsArrow));

		int[] coordsLineUpper = { bounds.x + 0, bounds.y + (int) (height * 0.25), 
				bounds.x + width - 1,
				bounds.y + (int) (height * 0.25) };
		lineUpper.setPoints(new PointList(coordsLineUpper));

		int[] coordsLineLower = { bounds.x + 0, bounds.y + (int) (height * 0.75), 
				bounds.x + width - 1,
				bounds.y + (int) (height * 0.75) };
		lineLower.setPoints(new PointList(coordsLineLower));

		// Use current colors
		labelUpper.setForegroundColor(processElementLabelColor);
		labelLower.setForegroundColor(processElementLabelColor);

		// Positioning
		setConstraint(arrowShape, new Rectangle(0, 0, width, height));
		setConstraint(labelUpper, new Rectangle(0, 0, width - 1, (int) (height * 0.25)));
		setConstraint(labelLower, new Rectangle(0, (int) (height * 0.75), width - 1, (int) (height * 0.25)));
		
		// Find automatically inserted Label
//		if(labelMain == null){
//			for(Object child : this.getChildren()){
//				if(child instanceof SiriusWrapLabel){
//					labelMain = (SiriusWrapLabel) child;
//				}
//			}
//		}
//		if(labelMain != null){
//			setConstraint(labelMain, new Rectangle(0, (int) (height * 0.25), (int) (width * 0.85), (int) (height * 0.5)));
//		}
	}

}
