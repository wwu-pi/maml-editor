package de.wwu.md2dot0.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ReorderItemsDialog extends Dialog {

	private Table list;
	private String title;
	private String labelText;
	private java.util.List<Object> fElements = new ArrayList<Object>();
	
	// Map Object to human readable String (not necessarily unique)
	private Function<Object, String> function = elem -> elem.toString();
	
	// Mapper from object hash to object (cannot move generic objects directly)
	private Map<String, Object> mapper = new HashMap<String, Object>();

	public Object[] getResult() {
		return fElements.toArray();
	}

	public ReorderItemsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
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
		label.setText(labelText);
		GridData data = new GridData();
		data.widthHint = convertWidthInCharsToPixels(60);
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		label.setLayoutData(data);

		list = createTable(parent);
		fillTable();

		return parent;
	}

	private Table createTable(Composite parent) {
		final Table table = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(false);
		table.setHeaderVisible(false);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4);
		gd.widthHint = convertWidthInCharsToPixels(60);
		gd.heightHint = convertHeightInCharsToPixels(10);
		table.setLayoutData(gd);

		// Initialize drag and drop
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		// Create the drag source
		DragSource source = new DragSource(table, DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(types);
		source.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				// Get the selected items in the drag source
				DragSource ds = (DragSource) event.widget;
				Table table = (Table) ds.getControl();
				TableItem[] selection = table.getSelection();

				// Put the data into the event (only single item can be moved)
				if(selection.length == 1){
					event.data = selection[0].getData();
				}
			}
		});

		// Create the drop target
		DropTarget target = new DropTarget(table, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;
				}

				// Allow dropping text only
				for (int i = 0, n = event.dataTypes.length; i < n; i++) {
					if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				// Provide visual feedback
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}

			public void drop(DropTargetEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					// Get the dropped data and drop position
					Object target = ((TableItem) event.item).getData();
					Object data = event.data;

					// Move opjects and redraw table
					moveItem(mapper.get(data), mapper.get(target));
				}
			}
		});

		return table;
	}

	public void setElements(java.util.List<Object> elements, Function<Object, String> function) {
		fElements = elements;
		
		// Initialize mapping
		mapper.clear();
		for(Object element : elements){
			mapper.put(element.toString(), element);
		}
		this.function = function;
	}
	
	protected void fillTable() {
		if (list == null)
			return;

		list.removeAll();

		// Image in front of item
		// Device device = Display.getCurrent ();
		// final Image image = new Image (device, 16, 16);
		// GC gc = new GC (image);
		// gc.setBackground (device.getSystemColor (SWT.COLOR_RED));
		// gc.fillRectangle (image.getBounds ());
		// gc.dispose ();

		if(fElements == null || fElements.size() == 0 || function == null) return;
		
		for (int i = 0; i < fElements.size(); i++) {
			TableItem item = new TableItem(list, SWT.NONE);
			item.setData(fElements.get(i).toString()); // Set hash as data (cannot pass object)
			item.setText((i+1) + ". " + function.apply(fElements.get(i)));
			// item.setImage(image);
		}
		
		// Add "to the end" item
		TableItem item = new TableItem(list, SWT.NONE);
		item.setData("EMPTY");
		item.setText("<to the end>");
	}
	
	protected void moveItem(Object itemToMove, Object draggedOnItem){
		// Drag on itself
		if(itemToMove == null || itemToMove.equals(draggedOnItem)) return;
		
		// remove old first
		fElements.remove(itemToMove);
		
		// find new spot and insert
		// End of list has no real element -> draggedOnItem is null
		int newIndex = draggedOnItem != null ? fElements.indexOf(draggedOnItem) : fElements.size();
		fElements.add(newIndex, itemToMove);
		
		fillTable();
	}
	
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
}
