package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
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
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.provider.SiriusEditPlugin;

public class ProcessElementStyleEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart {

	    /**
	     * the content pane.
	     */
	    protected IFigure contentPane;

	    /**
	     * the primary shape.
	     */
	    protected ImageFigure primaryShape;

	    /**
	     * Create a new {@link ChangingImageEditPart}.
	     *
	     * @param view
	     *            the view.
	     */
	    public ProcessElementStyleEditPart(View view) {
	        super(view);
	    }

	    public DragTracker getDragTracker(Request request) {
	        return getParent().getDragTracker(request);
	    }

	    protected NodeFigure createNodeFigure() {
	        NodeFigure figure = createNodePlate();
	        figure.setLayoutManager(new XYLayout());
	        IFigure shape = createNodeShape();
	        figure.add(shape);
	        contentPane = setupContentPane(shape);
	        return figure;
	    }

	    private NodeFigure createNodePlate() {
	        DefaultSizeNodeFigure result = new AirStyleDefaultSizeNodeFigure(getMapMode().DPtoLP(40), getMapMode().DPtoLP(40));
	        return result;
	    }

	    /**
	     * Create the instance role figure.
	     *
	     * @return the created figure.
	     */
	    protected ImageFigure createNodeShape() {
	        if (primaryShape == null) {
	            primaryShape = new ImageFigure();
	        }
	        return primaryShape;
	    }

	    /**
	     * Return the instance role figure.
	     *
	     * @return the instance role figure.
	     */
	    public ImageFigure getPrimaryShape() {
	        return primaryShape;
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
	        if (customStyle.eContainer() instanceof DNode) {
	            this.getPrimaryShape().setImage(SiriusEditPlugin.getPlugin().getBundledImage(((DNode) customStyle.eContainer()).getName()));
	        }
	    }

	    protected void createDefaultEditPolicies() {
	        // empty.
	    }
	}