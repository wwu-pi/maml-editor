package de.wwu.md2dot0.dialog;

import javax.swing.JList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ReorderItemsDialog extends Dialog {
	
	private Table list;
	private Object[] fElements = {};
private Shell shell;

	public Object[] getResult() {
		return null;//list.getSelection();
	}

	public ReorderItemsDialog(Shell parentShell) {
		super(parentShell);
		shell = parentShell;
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
		
//		list = new JList(parent);//, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
//		list.add(fElements);
//		
//		list.setDragEnabled(true);
//        list.setTransferHandler(new ListTransferHandler());
		list = createTable(parent);
		
//		Device device = Display.getCurrent ();
//		 final Image image = new Image (device, 16, 16);
//		  GC gc = new GC (image);
//		  gc.setBackground (device.getSystemColor (SWT.COLOR_RED));
//		  gc.fillRectangle (image.getBounds ());
//		  gc.dispose ();

		for(Object o : fElements){
			TableItem item = new TableItem(list, SWT.NONE);
			item.setText(o.toString());
//			item.setImage(image);
		}
		
		
//		
//		data = new GridData();
////        data.widthHint = convertWidthInCharsToPixels(fWidth);
//        data.heightHint = convertHeightInCharsToPixels(10);
//        data.grabExcessVerticalSpace = true;
//        data.grabExcessHorizontalSpace = true;
//        data.horizontalAlignment = GridData.FILL;
//        data.verticalAlignment = GridData.FILL;
//        list.getControl().setLayoutData(data);
        
        return parent;
	}
	
	private Table createTable(Composite parent) {
		final Table table = new Table(parent, SWT.BORDER | SWT.MULTI);//SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(false);
		table.setHeaderVisible(false);
		
//		Device device = Display.getCurrent ();
//		
//		Color red = new Color (device, 255, 0, 0);
//		table.setBackground(red);
//		Color black = new Color (device, 0, 0, 0);
//		table.setForeground(black);
//		table.setFont(device.getSystemFont());
		
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4);
		gd.widthHint = convertWidthInCharsToPixels(60);
		gd.heightHint = convertHeightInCharsToPixels(10);
		table.setLayoutData(gd);
//		TableColumn one = new TableColumn(table, SWT.LEFT);
//		one.setText("One");
//		table.setHeaderVisible(true);
//		table.setData(-1);
		table.setDragDetect(true);
		
		 
		
		return table;
	}
	
	public void setElements(Object[] elements) {
        fElements = elements;
    }
}
