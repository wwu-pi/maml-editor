package de.wwu.maml.editor.dialog;

import java.util.function.Function;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ObjectListSelectionDialog extends ElementListSelectionDialog {

	// Map Object to human readable String (not necessarily unique)
	private Function<Object, String> function = elem -> elem.toString();
	
	Object[] elements;
	Object[] objectSelection;
	
	public ObjectListSelectionDialog(Shell parent, ILabelProvider renderer) {
		super(parent, renderer);
	}

	public void setElements(Object[] elements, Function<Object, String> function) {
		this.function = function;
		setElements(elements);
		this.elements = elements; // duplicated but property is private in ElementListSelectionDialog
	}
	
	@Override
    protected void setListElements(Object[] elements) {
        Assert.isNotNull(fFilteredList);
        
        Object[] readableElements = new Object[elements.length];
        for(int i=0; i< elements.length; i++){
        	// Transform to human readable representation
        	readableElements[i] = function.apply(elements[i]);
        }
        
        fFilteredList.setElements(readableElements);
		handleElementsChanged();
    }
	
	@Override
	public Object[] getResult(){
		return objectSelection;
	}
	
	@Override
	protected void computeResult() {
		int[] indices = getSelectionIndices();
		
		objectSelection = new Object[indices.length];
		for(int i=0; i < indices.length; i++){
			objectSelection[i] = elements[i];
		}
		
        super.computeResult();
    }
}
