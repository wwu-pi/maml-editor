package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.sirius.diagram.CustomStyle;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.AirStyleDefaultSizeNodeFigure;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.provider.SiriusEditPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class ProcessElementEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart  {

		/**
	     * the content pane.
	     */
	    protected IFigure contentPane;

	    /**
	     * the primary shape.
	     */
	//    protected Triangle primaryShape;
	    protected ProcessElementFigure arrowShape;
//	    protected final Color processElementColor = new Color(Display.getCurrent(), 107, 218, 247);

//	    final int width = getMapMode().DPtoLP(30);
//		final int height = getMapMode().DPtoLP(14);


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
	        IFigure shape = createArrowShape(); //createNodeShape();
	        figure.add(shape);
	        
	        //createArrowShape();
	        
	        contentPane = setupContentPane(shape);
	        return figure;
	    }

	    private NodeFigure createNodePlate() {
	        DefaultSizeNodeFigure result = new AirStyleDefaultSizeNodeFigure(20, 10);
//	        System.out.println("plate");
//	        System.out.println(result.getBounds());
//	        System.out.println(result.getPreferredSize());
	        return result;
	    }

	    /**
	     * Create the instance role figure.
	     *
	     * @return the created figure.
	     */
	/*    protected Triangle createNodeShape() {
	        if (primaryShape == null) {
	            primaryShape = new Triangle(); //  new ImageFigure();
	            primaryShape.setDirection(PositionConstants.EAST);
	        }
	        return primaryShape;
	    }
	    */
	    protected IFigure createArrowShape(){
	    	if(arrowShape == null) {
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
	     * @param nodeShape
	     *            instance of generated figure class
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
	        	
	        	System.out.println(customStyle.getId());
//	        	System.out.println(((DNode) customStyle.eContainer()).getName());
//	        	System.out.println(((DNode) customStyle.eContainer()).getHeight());
//	        	System.out.println(((DNode) customStyle.eContainer()).getWidth());
//	            this.getPrimaryShape().setBackgroundColor(processElementColor); //setImage(SiriusEditPlugin.getPlugin().getBundledImage(((DNode) customStyle.eContainer()).getName()));
	      //      this.arrowShape.setBackgroundColor(new Color(Display.getCurrent(), 0, 255, 0));
	        }
//	        System.out.println(customStyle.getLabelSize());
//	        View v = this.getNotationView();
//	        Object o = getModel();
//	        System.out.println("stop");
	    }

	    protected void createDefaultEditPolicies() {
	        // empty.
	    }
	}
