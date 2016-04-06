package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AirStyleDefaultSizeNodeFigure;

public class ProcessElementEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart {

	/**
	 * the content pane.
	 */
	protected IFigure contentPane;

	/**
	 * the primary shape.
	 */
	protected ProcessElementFigure arrowShape;

	/**
	 * Create a new {@link ChangingImageEditPart}.
	 *
	 * @param view
	 *            the view.
	 */
	public ProcessElementEditPart(View view) {
		super(view);
	}

	public DragTracker getDragTracker(Request request) {
		return getParent().getDragTracker(request);
	}

	protected NodeFigure createNodeFigure() {
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new XYLayout());
		IFigure shape = createArrowShape();
		figure.add(shape);

		contentPane = setupContentPane(shape);
		return figure;
	}

	private NodeFigure createNodePlate() {
		DefaultSizeNodeFigure result = new AirStyleDefaultSizeNodeFigure(20, 10);
		return result;
	}

	protected IFigure createArrowShape() {
		if (arrowShape == null) {
			arrowShape = new ProcessElementFigure((CustomStyle) this.resolveSemanticElement());
		}
		return arrowShape;
	}

	/**
	 * Return the instance role figure.
	 *
	 * @return the instance role figure.
	 */
	public IFigure getPrimaryShape() {
		return arrowShape;
	}

	/**
	 * Default implementation treats passed figure as content pane. Respects
	 * layout one may have set for generated figure.
	 *
	 * @param nodeShape instance of generated figure class
	 * @return the figure
	 */
	protected IFigure setupContentPane(IFigure nodeShape) {
		return nodeShape; // use nodeShape itself as contentPane
	}

	public IFigure getContentPane() {
		if (contentPane != null) {
			return contentPane;
		}
		return super.getContentPane();
	}

	protected void refreshVisuals() {
		CustomStyle customStyle = (CustomStyle) this.resolveSemanticElement();
		if (arrowShape != null && customStyle.eContainer() instanceof DNode) {
			DNode node = (DNode) customStyle.eContainer();

			arrowShape.setProcessElementDescription(node.getName());
		}
	}
}
