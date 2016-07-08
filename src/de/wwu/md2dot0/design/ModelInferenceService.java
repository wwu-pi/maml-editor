package de.wwu.md2dot0.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DSemanticDiagramSpec;
import org.eclipse.sirius.diagram.ui.internal.refresh.GMFHelper;
import org.eclipse.sirius.ui.business.api.dialect.DialectEditor;
import org.eclipse.sirius.ui.business.api.session.IEditingSession;
import org.eclipse.sirius.ui.business.api.session.SessionUIManager;
import org.eclipse.sirius.viewpoint.DAnalysis;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationElement;
import org.eclipse.sirius.viewpoint.DView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.sirius.business.api.dialect.command.RefreshRepresentationsCommand;

import de.wwu.md2dot0.inference.ModelInferrer;
import de.wwu.md2dot0.inference.ModelInferrerManager;
import de.wwu.md2dot0.inference.TypeLiteral;
import md2dot0.ProcessFlowElement;
import md2dot0.UseCase;
import md2dot0gui.Attribute;

public class ModelInferenceService {
	// Central inference component
	ModelInferrer inferrer;
	
	/**
	 * Method called by Sirius to get type for specific process element 
	 * @param obj
	 * @return
	 */
	public String getDataTypeRepresentation(EObject obj){
		if(!(obj.eContainer() instanceof UseCase)) return "error";
		
		UseCase useCase = (UseCase) obj.eContainer();
		inferrer = ModelInferrerManager.getInstance().getModelInferrer((UseCase) obj.eContainer());
		inferrer.startInferenceProcess(useCase); // Container is the use case itself
		
		Session session = SessionManager.INSTANCE.getSession(obj);
		TransactionalEditingDomain domain = session.getTransactionalEditingDomain();//TransactionUtil.getEditingDomain(obj);//
		
		CommandStack stack = domain.getCommandStack();
		for(DView view : session.getOwnedViews()){
			Command cmd = new RefreshRepresentationsCommand(domain, null, view.getOwnedRepresentations());//domain, null, DialectManager.INSTANCE.getAllRepresentations(session));
			if (cmd.canExecute()) {
				domain.getCommandStack().execute(cmd);
			} else {
				System.out.println("Nope");
			}
		}
		
//		Collection<DRepresentation> c = DialectManager.INSTANCE.getAllRepresentations(session);
//		
//		CommandStack stack = domain.getCommandStack();
//		for (DView view : session.getOwnedViews()) {
//			for (DRepresentation rep : view.getOwnedRepresentations()) {
//				// Find correct diagram (for the semantic element we consider)
//				if (!(rep instanceof DSemanticDiagramSpec)
//						|| !obj.eContainer().equals(((DSemanticDiagramSpec) rep).getTarget())) {
//					System.out.println("skip" + rep);
//					continue;
//				}
//				
//				Command cmd = new RefreshRepresentationsCommand(domain, true, null, rep);
//				if (cmd.canExecute()) {
//					domain.getCommandStack().execute(cmd);
//				} else {
//					System.out.println("Cannot execute");
//				}
//			}
//		}
		
//		for (IEditingSession uiSession : SessionUIManager.INSTANCE.getUISessions()) {
//		for(DView view : session.getOwnedViews()){
//			for(DRepresentation rep : view.getOwnedRepresentations()){
//			System.out.println(rep);
//			
//		//	DiagramEditor dialectEditor = uiSession.getEditor((DDiagram) rep);
//         //   ((DiagramEditor) dialectEditor).getDiagramGraphicalViewer().getEditPartRegistry();
//             
//			}
//		 }
//		}
//		
		
		
		
		
		
//		Collection<DView> views = session.getOwnedViews();
//		for(DView view : views){
//			Collection<DRepresentation> c = view.getOwnedRepresentations();
//			RecordingCommand cmd = new org.eclipse.sirius.business.api.dialect.command.RefreshRepresentationsCommand(editingDomain, true, new NullProgressMonitor());//, c);//domain, null, DialectManager.INSTANCE.getAllRepresentations(session));
//			//if (cmd.canExecute()) {
//			stack.execute(cmd);
//			
			
//			try
//			{
//				editingDomain.runExclusive(new Runnable()
//			{
//			public void run ()
//			{
//			Display display = PlatformUI.getWorkbench().getDisplay();
//			display.asyncExec(new Runnable()
//			{
//			public void run ()
//			{
//				editingDomain.getCommandStack().execute(cmd);
//			}
//			});
//			}
//			});
//			}
//			catch (InterruptedException e)
//			{
//			e.printStackTrace();
//			}
			
			//} 
//			for(DRepresentation rep : view.getOwnedRepresentations()){
//				System.out.println(rep);
//				
////				for(DRepresentationElement repElem : rep.getOwnedRepresentationElements()){
////				//	System.out.println(rep);
////					
////				}
//				
//			}
//		}
//		EObject root =session.getSessionResource().getContents().get(0);
//		View dView = root.getOwnedViews().get(0);
//		DRepresentation myRepresentation = dView.getOwnedRepresentations().get(0);
		
		//session.getModelAccessor();
		//session.get
//		for (IEditingSession uiSession : SessionUIManager.INSTANCE.getUISessions()) {
//            DialectEditor dialectEditor = uiSession.getEditors().iterator().next();// .getEditor((DDiagram) gmfDiagram.getElement());
//            System.out.println(dialectEditor);
//            
////          //  if (isEditorFor(dialectEditor, gmfDiagram)) {
////            GraphicalEditPart part = getGraphicalEditPart(view, (DiagramEditor) dialectEditor);
//           // }
//        }
		
		// Update visual elements
//		Session session = SessionManager.INSTANCE.getSession(obj);
//		TransactionalEditingDomain editingDomain = session.getTransactionalEditingDomain();
		
		
		
		//stack.execute(updateCommand);
//		EditPart.;
		//DialectManager.INSTANCE.getRepresentations(arg0, arg1)
		
		//org.eclipse.sirius.diagram.ui.business.api.view.SiriusGMFHelper.
		
		//refresh(laneSetContainer.getParentDiagram(),
		//		new NullProgressMonitor());
		
		
		if(obj instanceof ProcessFlowElement){
			String type = inferrer.getType((ProcessFlowElement) obj);
			return type != null ? type : "??";
		} else if(obj instanceof Attribute){
			TypeLiteral type = TypeLiteral.from(((Attribute) obj).getType());
			return type != null ? type.toString() : "??";
		} 
		return "??";
	}
	
	public static GraphicalEditPart getGraphicalEditPart(View view) {
        if (view != null) {
            Diagram gmfDiagram = view.getDiagram();
            // Try the active editor first (most likely case in practice)
            IEditorPart editor = null;// EclipseUIUtil.getActiveEditor();
//            //if (isEditorFor(editor, gmfDiagram)) {
//             //   return getGraphicalEditPart(view, (DiagramEditor) editor);
//            //} else
            	if (gmfDiagram.getElement() instanceof DDiagram) {
//                // Otherwise check all active Sirius editors
                for (IEditingSession uiSession : SessionUIManager.INSTANCE.getUISessions()) {
                    DialectEditor dialectEditor = uiSession.getEditor((DDiagram) gmfDiagram.getElement());
                    if (((DiagramEditor) editor).getDiagram() == gmfDiagram) {
                    	((DiagramEditor) editor).getDiagramGraphicalViewer().getEditPartRegistry();
//                        return getGraphicalEditPart(view, (DiagramEditor) dialectEditor);
//                    	final Map<?, ?> editPartRegistry = editor.getDiagramGraphicalViewer().getEditPartRegistry();
//                        final EditPart targetEditPart = (EditPart) editPartRegistry.get(view);
//                        if (targetEditPart instanceof GraphicalEditPart) {
//                        	GraphicalEditPart result = (GraphicalEditPart) targetEditPart;
//                        }
//                        return result;
                    }
                }
            }
        }
        return null;//Options.<GraphicalEditPart>newNone();
    }
    
    private static boolean issEditorFor(IEditorPart editor, Diagram diagram) {
        return editor instanceof DiagramEditor && ((DiagramEditor) editor).getDiagram() == diagram;
    }
    
	/** 
	 * Perform the data model inference without returning a value
	 * @param obj
	 */
	public void startInferenceProcess(EObject obj){
		getDataTypeRepresentation(obj);
	}
	
	/**
	 * Polymorphism helper in case multiple use cases are passed to infer.
	 * TODO: Currently only first use case is considered, needs to be extended to infer multiple UC and merge them
	 * @param obj
	 */
	public void startInferenceProcess(Collection<? extends EObject> obj){
		Optional<UseCase> useCase = obj.stream()
				.filter(elem -> elem instanceof UseCase)
				.map(elem -> (UseCase) elem)
				.findFirst();
		
		if(!useCase.isPresent()){
			return;
		}
		
		inferrer = ModelInferrerManager.getInstance().getModelInferrer(useCase.get());
		inferrer.startInferenceProcess((UseCase) useCase.get());
	}
	
	public String[] getDataTypeList(EObject object){
		// Refresh inferred model types
		startInferenceProcess(object);
		
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(TypeLiteral.getPrimitiveDataTypesAsString());
		list.addAll(TypeLiteral.getCustomDataTypesAsString());
		return list.toArray(new String[list.size()]);
	}
	
	public String openDataTypeSelectionWizard(EObject object){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		
		dialog.setElements(getDataTypeList(object)); //new String[] { "Linux", "Mac", "Windows" });
		dialog.setTitle("Select desired data type");
		
		// user pressed cancel
		if (dialog.open() != Window.OK) {
			// Return previous value
			if(object instanceof Attribute) return ((Attribute) object).getType();
		}
		Object[] result = dialog.getResult();
		
		// Value given and does not start with invalid character?
		if(result.length > 0 && !((String) result[0]).startsWith("__")){
			System.out.println(result[0]);
			return (String) result[0];
		}
		
		// Return previous value
		if(object instanceof Attribute) return ((Attribute) object).getType();
		return null; // Unknown error
	}
}
