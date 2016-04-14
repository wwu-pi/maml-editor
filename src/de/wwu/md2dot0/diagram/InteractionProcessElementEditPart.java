package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.Square;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.FixedLayoutEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AirStyleDefaultSizeNodeFigure;
import org.eclipse.sirius.diagram.ui.tools.api.policies.LayoutEditPolicy;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;

import de.wwu.md2dot0.design.TestService;
import md2dot0.ProcessElement;

@SuppressWarnings("restriction")
public class InteractionProcessElementEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart {

	@Override
	public DragTracker getDragTracker(Request request) {
		return getParent().getDragTracker(request);
	}

	/**
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart#refreshVisuals()
	 * @not-generated
	 */
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		
		CustomStyle customStyle = (CustomStyle) this.resolveSemanticElement();
		if (getPrimaryShape() != null && customStyle.eContainer() instanceof DNode) {
			DNode node = (DNode) customStyle.eContainer();

			if (node.getTarget() instanceof ProcessElement && node.getTarget() != null) {
				ProcessElement modelElement = (ProcessElement) node.getTarget();

				// Update process element subtype
				getPrimaryShape().setProcessElementType(TestService.getProcessElementType(modelElement));

				// Update data type
				String dataTypeName = modelElement.getDataType() != null ? modelElement.getDataType().toString() : "X";
				getPrimaryShape().setProcessElementDataType(dataTypeName); // Todo infer from service class?
			}
		}

		// GraphicalEditPart part = ((GraphicalEditPart) getParent());
		//
		// EObject dde = this.resolveSemanticElement();
		// if (dde instanceof Square) {
		// Square square = (Square) dde;
		// int borderSize = 0;
		// if (square.getBorderSize() != null) {
		// borderSize = square.getBorderSize().intValue();
		// }
		// DiagramNodeEditPartOperation.refreshFigure(this);
		// DiagramElementEditPartOperation.refreshLabelAlignment(((GraphicalEditPart)
		// getParent()).getContentPane(), square);
		// }
	}

	/**
	 * @not-generated
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart#refreshBackgroundColor()
	 */
	@Override
	protected void refreshBackgroundColor() {
		if (getMetamodelType().isInstance(resolveSemanticElement())) {
			Square square = (Square) this.resolveSemanticElement();
			if (square.getColor() != null) {
				this.getPrimaryShape()
						.setBackgroundColor(VisualBindingManager.getDefault().getColorFromRGBValues(square.getColor()));
			}
		}
	}

	/**
	 * @not-generated
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart#refreshForegroundColor()
	 */
	@Override
	protected void refreshForegroundColor() {
		if (getMetamodelType().isInstance(resolveSemanticElement())) {
			Square square = (Square) this.resolveSemanticElement();
			if (square.getBorderColor() != null) {
				this.getPrimaryShape().setForegroundColor(
						VisualBindingManager.getDefault().getColorFromRGBValues(square.getBorderColor()));
			}
		}
	}

	// public static final int VISUAL_ID = 3003;

	/**
	 * @was-generated
	 */
	protected IFigure contentPane;

	/**
	 * @was-generated
	 */
	protected InteractionProcessElementFigure primaryShape;

	/**
	 * @was-generated
	 */
	public InteractionProcessElementEditPart(View view) {
		super(view);
	}

	/**
	 * @not-generated
	 */
	@Override
	protected void createDefaultEditPolicies() {
		// Do nothing.
	}

	/**
	 * @not-generated
	 */
	protected LayoutEditPolicy createLayoutEditPolicy() {
		return new FixedLayoutEditPolicy();
	}

	/**
	 * @not-generated
	 */
	protected IFigure createNodeShape() {
		InteractionProcessElementFigure arrowShape = new InteractionProcessElementFigure((CustomStyle) this.resolveSemanticElement());

		// EditPart parent = this.getParent();
		// if (parent instanceof IDiagramBorderNodeEditPart) {
		// DiagramBorderNodeEditPartOperation.updateTransparencyMode((IDiagramBorderNodeEditPart)
		// parent, square);
		// }

		return primaryShape = arrowShape;
	}

	/**
	 * @was-generated
	 */
	public InteractionProcessElementFigure getPrimaryShape() {
		return (InteractionProcessElementFigure) primaryShape;
	}

	/**
	 * @not-generated
	 */
	protected NodeFigure createNodePlate() {
		return new AirStyleDefaultSizeNodeFigure(getMapMode().DPtoLP(40), getMapMode().DPtoLP(40));
	}

	/**
	 * @was-generated
	 */
	@Override
	public EditPolicy getPrimaryDragEditPolicy() {
		EditPolicy result = super.getPrimaryDragEditPolicy();
		if (result instanceof ResizableEditPolicy) {
			ResizableEditPolicy ep = (ResizableEditPolicy) result;
			ep.setResizeDirections(PositionConstants.NONE);
		}
		return result;
	}

	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model so
	 * you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @was-generated
	 */
	@Override
	protected NodeFigure createNodeFigure() {
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new XYLayout());
		IFigure shape = createNodeShape();
		figure.add(shape);
		contentPane = setupContentPane(shape);
		return figure;
	}

	/**
	 * Default implementation treats passed figure as content pane. Respects
	 * layout one may have set for generated figure.
	 * 
	 * @param nodeShape
	 *            instance of generated figure class
	 * @was-generated
	 */
	protected IFigure setupContentPane(IFigure nodeShape) {
		return nodeShape; // use nodeShape itself as contentPane
	}

	/**
	 * @was-generated
	 */
	@Override
	public IFigure getContentPane() {
		if (contentPane != null) {
			return contentPane;
		}
		return super.getContentPane();
	}

	protected Class<?> getMetamodelType() {
		return ProcessElement.class;
	}
}