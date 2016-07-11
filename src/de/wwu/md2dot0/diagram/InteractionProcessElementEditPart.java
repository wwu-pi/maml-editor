package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gmf.runtime.diagram.core.listener.DiagramEventBroker;
import org.eclipse.gmf.runtime.diagram.core.listener.NotificationListener;
import org.eclipse.gmf.runtime.diagram.core.listener.NotificationPreCommitListener;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.business.api.dialect.command.RefreshRepresentationsCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.Square;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeSpec;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramElementEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.FixedLayoutEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AirStyleDefaultSizeNodeFigure;
import org.eclipse.sirius.diagram.ui.tools.api.policies.LayoutEditPolicy;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.DView;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import de.wwu.md2dot0.design.ModelInferenceService;
import de.wwu.md2dot0.design.TestService;
import de.wwu.md2dot0.inference.ModelInferrer;
import de.wwu.md2dot0.inference.ModelInferrerManager;
import md2dot0.ProcessElement;
import md2dot0.UseCase;

@SuppressWarnings("restriction")
public class InteractionProcessElementEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart, NotificationListener {

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
				//ModelInferrer inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) modelElement.eContainer());
				//String dataTypeName = inferrer.getType(modelElement);
				String dataTypeName = new ModelInferenceService().getDataTypeRepresentation(modelElement);
				getPrimaryShape().setProcessElementDataType(dataTypeName);
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
	
	@Override
	public void activate(){
		super.activate();
		InteractionProcessElementEditPart self = this;
		TransactionalEditingDomain domain = this.getEditingDomain();
		DiagramEventBroker broker = DiagramEventBroker.getInstance(domain);
		EObject elem =  ((DNodeSpec) this.resolveSemanticElement().eContainer()).getTarget();
		broker.addNotificationListener(elem, this); 
//		new NotificationPreCommitListener() {
//
//            @SuppressWarnings("unchecked")
//			@Override
//            public Command transactionAboutToCommit(final Notification msg) {
//            	System.out.println("call");
//                return new RefreshRepresentationsCommand(domain, null, self.getDiagramView().getChildren());
//            }
//		});
	}
	
	private DDiagramElement getDDiagramElement(IGraphicalEditPart graphicalEditPart) {
        DDiagramElement dDiagramElement = null;
        if (graphicalEditPart instanceof IDiagramElementEditPart) {
            dDiagramElement = ((IDiagramElementEditPart) graphicalEditPart).resolveDiagramElement();
        } else if (graphicalEditPart.getParent() instanceof IDiagramElementEditPart) {
            dDiagramElement = ((IDiagramElementEditPart) graphicalEditPart.getParent()).resolveDiagramElement();
        }
        return dDiagramElement;
}
	
	@Override
	public void notifyChanged(Notification notification) {
		System.out.println(notification);
		System.out.println("TESTs");
		
//		Session session = SessionManager.INSTANCE.getSession((EObject) notification.getNotifier());
//		TransactionalEditingDomain domain = this.getEditingDomain();
//		
//		for(DView view : session.getOwnedViews()){
//		//	new SetCommand(domain, view, null, model);
//			
//			Command cmd = new RefreshRepresentationsCommand(domain, null, view.getOwnedRepresentations());//domain, null, DialectManager.INSTANCE.getAllRepresentations(session));
//			if (cmd.canExecute()) {
//				domain.getCommandStack().execute(cmd);
////			} else {
////				System.out.println("Nope");
//			}
//		}
		refreshVisuals();
	}
	
	
}