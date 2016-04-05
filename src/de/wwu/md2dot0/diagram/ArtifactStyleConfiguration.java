package de.wwu.md2dot0.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.gmf.runtime.diagram.ui.figures.BorderItemLocator;
import org.eclipse.gmf.runtime.diagram.ui.figures.IBorderItemLocator;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrapLabel;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.notation.Image;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.ResizeKind;
import org.eclipse.sirius.diagram.Square;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.NodeMapping;
import org.eclipse.sirius.diagram.ui.part.SiriusDiagramEditor;
import org.eclipse.sirius.diagram.ui.tools.api.figure.anchor.AnchorProvider;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.BorderItemLocatorProvider;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.DefaultBorderItemLocatorProvider;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.SimpleStyleConfiguration;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.description.tool.MappingBasedToolDescription;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class ArtifactStyleConfiguration extends SimpleStyleConfiguration {
	
	
	  /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.diagram.tools.api.graphical.edit.styles.SimpleStyleConfiguration#getBorderDimension(org.eclipse.sirius.viewpoint.DNode)
     */
  /*  @Override
    public Insets getBorderDimension(final DNode node) {
        final Insets result = new Insets(0, 0, 0, 0);
        if (node.getStyle() instanceof Square) {
            final Square square = (Square) node.getStyle();
            result.left = square.getBorderSize().intValue();
            result.right = square.getBorderSize().intValue();
            result.top = 40; 
            result.bottom = square.getBorderSize().intValue();
        }
        return result;
    } */
    
	public void adaptNodeLabel(DNode node, WrapLabel nodeLabel) {
        if (nodeLabel.getParent() != null) {
            Rectangle constraint = nodeLabel.getParent().getBounds().getCopy();

            Insets borderDimension = this.getBorderDimension(node);
            constraint.height = 5;// -= (borderDimension.top + borderDimension.bottom);
            constraint.width -= (borderDimension.left + borderDimension.right);
            constraint.x += borderDimension.left;
            constraint.y += borderDimension.bottom - constraint.height; //borderDimension.top;

            nodeLabel.setBounds(constraint);
            nodeLabel.getParent().setConstraint(nodeLabel, constraint);
            
            nodeLabel.setBackgroundColor(new Color(Display.getCurrent(), 255, 0, 0));
        }
    }

//    public int adaptViewNodeSizeWithLabel(DNode viewNode, WrapLabel nodeLabel, int nodeWidth) {
//        if (viewNode.getResizeKind() != ResizeKind.NONE_LITERAL) {
//
//        }
//        return nodeWidth;
//    }
//
//    public AnchorProvider getAnchorProvider() {
//        return null;
//    }
//
//    public BorderItemLocatorProvider getBorderItemLocatorProvider() {
//        return DefaultBorderItemLocatorProvider.getInstance();
//    }
//
//    public IBorderItemLocator getNameBorderItemLocator(DNode node, IFigure mainFigure) {
//        BorderItemLocator locator = new BorderItemLocator(mainFigure); //new AirBorderItemLocator(mainFigure, PositionConstants.NSEW);
//        locator.setBorderItemOffset(new Dimension(1, 1));
//        return locator;
//    }
//
//    public Image getLabelIcon(DDiagramElement vpElement) {
//        EObject target = vpElement;
//        if (vpElement instanceof DSemanticDecorator) {
//            target = ((DSemanticDecorator) vpElement).getTarget();
//        }
//        if (isShowIcon(vpElement)) {
//            if (target != null) {
//               /* IfItemProvider labelProvider = null; //(IItemLabelProvider) SiriusDiagramEditor.getInstance()
//                		//.getItemProvidersAdapterFactory().adapt(target, IItemLabelProvider.class);
//                if (labelProvider != null) {
//                    ImageDescriptor descriptor = ExtendedImageRegistry.getInstance().getImageDescriptor(labelProvider.getImage(target));
//                    if (descriptor == null) {
//                        descriptor = ImageDescriptor.getMissingImageDescriptor();
//                    }
//                    return SiriusDiagramEditorPlugin.getInstance().getImage(descriptor);
//                }*/
//            }
//        }
//        return null;
//    }
//
//    protected boolean isShowIcon(DDiagramElement vpElement) {
//       /* if (vpElement instanceof MappingBasedToolDescription) {
//            DiagramElementMapping vpElementMapping = ((MappingBased) vpElement).getMapping();
//            if (vpElementMapping instanceof NodeMapping) {
//                return ((NodeMapping) vpElementMapping).isShowIcon();
//            }
//            if (vpElementMapping instanceof EdgeMapping) {
//                return ((EdgeMapping) vpElementMapping).isShowIcon();
//            }
//            if (vpElementMapping instanceof ContainerMapping) {
//                return true;
//            }
//        }*/
//        return false;
//    }
//
//    public Dimension fitToText(DNode node, WrapLabel nodeLabel, DefaultSizeNodeFigure defaultSizeNodeFigure) {
//        if (nodeLabel.getFont() != null) {
//            String text = node.getName();
//
//            int charHeight = FigureUtilities.getStringExtents("ABCDEF", nodeLabel.getFont()).height + 5;
//            int charWidth = FigureUtilities.getTextWidth("ABCDEFGHIJKLMNOPQRSTUVWXYZ", nodeLabel.getFont()) / 26;
//
//            double ratio = charHeight / charWidth;
//
//            int nbLines = (int) (Math.sqrt(text.length()) / ratio) + 1;
//            int nbCols = (int) (Math.sqrt(text.length()) * ratio) + 1;
//
//            int longestWord = this.getTheLongestWord(text.split("\\s"));
//            nbCols = Math.max(nbCols, longestWord);
//
//            int hHeight = nbLines * charHeight;
//            int hWidth = nbCols * charWidth;
//
//            Dimension size = nodeLabel.getPreferredSize(hWidth + nodeLabel.getIconBounds().width +
//            		nodeLabel.getIconTextGap(), hHeight).getCopy();
//
//            size.width += 20;
//            size.height += 30;
//
//            Insets borderDimension = this.getBorderDimension(node);
//            size.width += (borderDimension.left + borderDimension.right);
//            size.height += (borderDimension.top + borderDimension.bottom);
//
//            //
//            // Square ?
//            if (node.getHeight().intValue() == node.getWidth().intValue()) {
//                // size.width = Math.max(size.height, size.width);
//                // size.height = Math.max(size.height, size.width);
//            }
//
//            return size;
//        }
//        return defaultSizeNodeFigure.getBounds().getSize().getCopy();
//    }
//
//    private int getTheLongestWord(String[] strings) {
//        int max = -1;
//        for (int i = 0; i < strings.length; i++) {
//            if (strings[i].length() > max) {
//                max = strings[i].length();
//            }
//        }
//        return max;
//    }
//
//    /**
//     * Return the dimension of the border.
//     *
//     * @param nodeth
//     *            view node.
//     * @return the dimension of the border.
//     */
//    public Insets getBorderDimension(DNode node) {
//        return new Insets(0, 0, 0, 0);
//    }
//    
    
}
