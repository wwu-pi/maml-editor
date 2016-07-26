package de.wwu.md2dot0.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ReorderItemsDialog extends Dialog {
	
	private ListViewer list;
	private Object[] fElements = {};

	public Object[] getResult() {
		return null;//list.getSelection();
	}

	public ReorderItemsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(5);
		layout.marginWidth = convertHorizontalDLUsToPixels(5);
		layout.verticalSpacing = convertVerticalDLUsToPixels(5);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(5);
		
		Label label = new Label(parent, SWT.WRAP);
		label.setText("Please bring the attached GUI elements into the desired order or appearance on the screen:\n\n");
		GridData data = new GridData();
		data.widthHint = convertWidthInCharsToPixels(60);
//		data.heightHint = convertHeightInCharsToPixels(3);
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
//		data.horizontalIndent = 5;
		label.setLayoutData(data);
		
		list = new ListViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		list.add(fElements);
		
		data = new GridData();
//        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(10);
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        list.getControl().setLayoutData(data);
        
        return parent;
	}
	
	public void setElements(Object[] elements) {
        fElements = elements;
    }
}
