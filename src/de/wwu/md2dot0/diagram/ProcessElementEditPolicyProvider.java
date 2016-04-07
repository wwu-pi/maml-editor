package de.wwu.md2dot0.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.core.service.IProviderChangeListener;
import org.eclipse.gmf.runtime.common.core.service.ProviderChangeEvent;
import org.eclipse.gmf.runtime.diagram.ui.internal.editparts.NoteAttachmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.viewpoint.ViewpointPackage;

@SuppressWarnings("restriction")
public class ProcessElementEditPolicyProvider implements IEditPolicyProvider {

	    /** the property change support. */
	    @SuppressWarnings("rawtypes")
		private List listeners;

	    /**
	     * Create a new {@link AirNoteAttachmentEditPolicyProvider}.
	     */
	    @SuppressWarnings("rawtypes")
		public ProcessElementEditPolicyProvider() {
	        this.listeners = new ArrayList(2);
	    }

		public void createEditPolicies(EditPart editPart) {
	        if (editPart instanceof NoteAttachmentEditPart) {
	            editPart.installEditPolicy(EditPolicy.CONNECTION_ROLE, new ProcessElementEditPolicy());
	        }
	    }

	    @SuppressWarnings("unchecked")
		public void addProviderChangeListener(IProviderChangeListener listener) {
	        this.listeners.add(listener);
	    }

	    public boolean provides(IOperation operation) {
	        if (operation instanceof CreateEditPoliciesOperation) {
	            CreateEditPoliciesOperation castedOperation = (CreateEditPoliciesOperation) operation;
	            EditPart editPart = castedOperation.getEditPart();
	            Object model = editPart.getModel();
	            if (model instanceof View) {
	                View view = (View) model;
	                if (view.getDiagram() != null && view.getDiagram().getElement() != null
	                        && view.getDiagram().getElement().eClass().getEPackage().getNsURI().equals(ViewpointPackage.eINSTANCE.getNsURI())) {
	                    if ("NoteAttachment".equals(view.getType())) {
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    }

	    public void removeProviderChangeListener(IProviderChangeListener listener) {
	        this.listeners.remove(listener);
	    }

	    /**
	     * Fire a {@link ProviderChangeEvent}.
	     */
	    @SuppressWarnings("rawtypes")
	    protected void fireProviderChanged() {
	        ProviderChangeEvent event = new ProviderChangeEvent(this);
	        Iterator iterListener = this.listeners.iterator();
	        while (iterListener.hasNext()) {
	            IProviderChangeListener listener = (IProviderChangeListener) iterListener.next();
	            listener.providerChanged(event);
	        }
	    }

	}