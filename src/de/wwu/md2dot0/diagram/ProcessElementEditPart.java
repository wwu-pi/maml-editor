package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.Square;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramBorderNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramBorderNodeEditPartOperation;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramElementEditPartOperation;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramNodeEditPartOperation;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.FixedLayoutEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AbstractTransparentRectangle;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AirStyleDefaultSizeNodeFigure;
import org.eclipse.sirius.diagram.ui.tools.api.policies.LayoutEditPolicy;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.gmf.runtime.common.core.util.Log;
import org.eclipse.gmf.runtime.common.core.util.Trace;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.internal.DiagramUIDebugOptions;
import org.eclipse.gmf.runtime.diagram.ui.internal.DiagramUIPlugin;
import org.eclipse.gmf.runtime.diagram.ui.internal.DiagramUIStatusCodes;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;

import md2dot0.ProcessElement;

public class ProcessElementEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart { //AbstractNotSelectableShapeNodeEditPart

	/*******************************************************************************
	 * Copyright (c) 2007, 2008 THALES GLOBAL SERVICES.
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html
	 *
	 * Contributors:
	 *    Obeo - initial API and implementation
	 *******************************************************************************/
	
	    /**
	     * @not-generated : prevent drag of elements
	     */
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
	        GraphicalEditPart part = ((GraphicalEditPart) getParent());
	        
	        EObject dde = this.resolveSemanticElement();
	        if (dde instanceof Square) {
	            Square square = (Square) dde;
	            int borderSize = 0;
	            if (square.getBorderSize() != null) {
	                borderSize = square.getBorderSize().intValue();
	            }
//	            this.getPrimaryShape().setLineWidth(borderSize);
	            DiagramNodeEditPartOperation.refreshFigure(this);
	            DiagramElementEditPartOperation.refreshLabelAlignment(((GraphicalEditPart) getParent()).getContentPane(), square);
	        }
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
	                this.getPrimaryShape().setBackgroundColor(VisualBindingManager.getDefault().getColorFromRGBValues(square.getColor()));
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
	                this.getPrimaryShape().setForegroundColor(VisualBindingManager.getDefault().getColorFromRGBValues(square.getBorderColor()));
	            }
	        }
	    }

	    /**
	     * @was-generated
	     */
	    //public static final int VISUAL_ID = 3003;

	    /**
	     * @was-generated
	     */
	    protected IFigure contentPane;

	    /**
	     * @was-generated
	     */
	    protected ProcessElementFigure2 primaryShape;

	    /**
	     * @was-generated
	     */
	    public ProcessElementEditPart(View view) {
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
//	        SquareFigure square = new SquareFigure();
//	        EditPart parent = this.getParent();
//	        if (parent instanceof IDiagramBorderNodeEditPart) {
//	            DiagramBorderNodeEditPartOperation.updateTransparencyMode((IDiagramBorderNodeEditPart) parent, square);
//	        }
	    	ProcessElementFigure2 square = new ProcessElementFigure2((CustomStyle) this.resolveSemanticElement());
	        return primaryShape = square;
	    }

	    /**
	     * @was-generated
	     */
	    public ProcessElementFigure2 getPrimaryShape() {
	        return (ProcessElementFigure2) primaryShape;
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
//	        figure.setLayoutManager(new StackLayout());
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

	    /**
	     * @not-generated
	     */
//	    public static class SquareFigure extends AbstractTransparentRectangle {
//
//	        /**
//	         * @was-generated
//	         */
//	        public SquareFigure() {
//	            this.setLineWidth(3);
//	        }
//	    }

	    protected Class<?> getMetamodelType() {
	        return Square.class;
	    }

	}

//	/**
//	 * the content pane.
//	 */
//	protected IFigure contentPane;
//
//	/**
//	 * the primary shape.
//	 */
//	protected ProcessElementFigure arrowShape;
//
//	/**
//	 * Create a new {@link ChangingImageEditPart}.
//	 *
//	 * @param view
//	 *            the view.
//	 */
//	public ProcessElementEditPart(View view) {
//		super(view);
//	}
//
//	public DragTracker getDragTracker(Request request) {
//		return getParent().getDragTracker(request);
//	}
//
//	protected NodeFigure createNodeFigure() {
//		NodeFigure figure = createNodePlate();
//		figure.setLayoutManager(new XYLayout());
//		IFigure shape = createArrowShape();
//		figure.add(shape);
//
//		contentPane = setupContentPane(shape);
//		return figure;
//	}
//
//	private NodeFigure createNodePlate() {
//		DefaultSizeNodeFigure result = new AirStyleDefaultSizeNodeFigure(20, 10);
//		return result;
//	}
//
//	protected IFigure createArrowShape() {
//		if (arrowShape == null) {
//			arrowShape = new ProcessElementFigure((CustomStyle) this.resolveSemanticElement());
//		}
//		return arrowShape;
//	}
//
//	/**
//	 * Return the instance role figure.
//	 *
//	 * @return the instance role figure.
//	 */
//	public IFigure getPrimaryShape() {
//		return arrowShape;
//	}
//
//	/**
//	 * Default implementation treats passed figure as content pane. Respects
//	 * layout one may have set for generated figure.
//	 *
//	 * @param nodeShape instance of generated figure class
//	 * @return the figure
//	 */
//	protected IFigure setupContentPane(IFigure nodeShape) {
//		return nodeShape; // use nodeShape itself as contentPane
//	}
//
//	public IFigure getContentPane() {
//		if (contentPane != null) {
//			return contentPane;
//		}
//		return super.getContentPane();
//	}
//
//	protected void refreshVisuals() {
//		CustomStyle customStyle = (CustomStyle) this.resolveSemanticElement();
//		if (arrowShape != null && customStyle.eContainer() instanceof DNode) {
//			DNode node = (DNode) customStyle.eContainer();
//			
//			// Update description
//			arrowShape.setProcessElementDescription(node.getName());
//			
//			if(node.getTarget() instanceof ProcessElement && node.getTarget() != null){
//				ProcessElement modelElement = (ProcessElement) node.getTarget();
//				
//				// Update process element subtype
//				arrowShape.setProcessElementType(modelElement.getClass().getSimpleName());
//				
//				// Update data type
//				String dataTypeName = modelElement.getDataType() != null ? modelElement.getDataType().toString() : "X";
//				arrowShape.setProcessElementDataType(dataTypeName); // Todo infer from service class?
//			}
//		}
//	}
//	
//	
//		
//	
//	
////	@Override
////	protected void createDefaultEditPolicies() {
////		// empty
////		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ProcessElementEditPolicy());
////    }
//	
////	protected void createDefaultEditPolicies(){
////		//super.createEditPolicies();
//////		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ProcessElementEditPolicy());
////	}
//	
////	public void performRequest(Request request){
////	if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
////	if(manager == null)
////	manager = new LogicLabelEditManager(this,
////	TextCellEditor.class, new
////	LabelCellEditorLocator((Label)getFigure()));
////	manager.show();
////	}
////	}
//	
////	@Override
////	protected void performDirectEditRequest(Request request) {
////		EditPart editPart = this;
////		if (request instanceof DirectEditRequest){
////			Point p = new Point(((DirectEditRequest)request).getLocation());
////			getFigure().translateToRelative(p);
////			IFigure fig = getFigure().findFigureAt(p);
////			editPart =(EditPart) getViewer().getVisualPartMap().get(fig);
////		}
////		if (editPart == this) {
////			try {
////				editPart = (EditPart) getEditingDomain().runExclusive(
////					new RunnableWithResult.Impl() {
////
////						public void run() {
////							setResult(getPrimaryChildEditPart());
////						}
////					});
////			} catch (InterruptedException e) {
////				Trace.catching(DiagramUIPlugin.getInstance(),
////					DiagramUIDebugOptions.EXCEPTIONS_CATCHING, getClass(),
////					"performDirectEditRequest", e); //$NON-NLS-1$
////				Log.error(DiagramUIPlugin.getInstance(),
////					DiagramUIStatusCodes.IGNORED_EXCEPTION_WARNING,
////					"performDirectEditRequest", e); //$NON-NLS-1$
////			}
////			if (editPart != null){
////				editPart.performRequest(request);
////			}
////		}
////	}
////	
////	public EditPart getTargetEditPart(Request request) {
////
////        if (RequestConstants.REQ_DIRECT_EDIT == request.getType()){
////        	 return super.getTargetEditPart(request);
////        }
////        return super.getTargetEditPart(request);
////	}
//}
