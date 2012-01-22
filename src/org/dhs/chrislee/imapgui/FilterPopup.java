package org.dhs.chrislee.imapgui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterPopup {

	private final String filterType;
	private final Display display;
	private final boolean standAlone;
	
	private Shell shell;
	private List filterList;
	private Text newFilter;
	private Button invert;
	private final Set<String> filters;
	private boolean invertSave = false;
	
	public FilterPopup(String filterType) {
		this.filterType = filterType;
		this.display = new Display();
		this.standAlone = true;
		this.filters = new HashSet<String>();
	}
	
	public FilterPopup(String filterType, Display display) {
		this.filterType = filterType;
		this.display = display;
		this.standAlone = false;
		this.filters = new HashSet<String>();
	}
	
	public void open() {
		
		/* calculate the size of the shell */
		Rectangle displaySize = display.getClientArea();
		int horizSize = displaySize.width / 3;
		int vertSize = displaySize.height / 3;
		
		shell = new Shell(display, SWT.CLOSE);
		shell.setText( filterType + " Filter Editor" );
		shell.setSize(horizSize, vertSize);
		createContents( display, shell );
		shell.open();
		while( !shell.isDisposed() ) {
			if( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		if( standAlone )
			display.dispose();
	}
	
	private void createContents(Display display, final Shell shell) {
		
		shell.setLayout(new GridLayout(3, false));
		
		/* create the list */
		filterList = new List(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData listGD = new GridData();
		listGD.horizontalSpan = 2;
		listGD.verticalSpan = 3;
		listGD.grabExcessHorizontalSpace = true;
		listGD.grabExcessVerticalSpace = true;
		listGD.horizontalAlignment = GridData.FILL;
		listGD.verticalAlignment = GridData.FILL;
		filterList.setLayoutData(listGD);
		filterList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				int index = filterList.getSelectionIndex();
				if( index > -1 )
					newFilter.setText(filterList.getItem(index));
			}

			@Override
			public void widgetDefaultSelected( SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}
		});
		/* add the filters */
		for( String s : filters ) {
			filterList.add(s);
		}
		filters.clear();
		
		/* right cells, make the items */
		GridData listButtonGD = new GridData();
		listButtonGD.horizontalAlignment = GridData.FILL;
		listButtonGD.verticalAlignment = GridData.FILL;
		
		Button remove = new Button(shell, SWT.PUSH);
		remove.setText("Remove");
		remove.setToolTipText("Remove the selected filter from the list." +
				"\nMultiple filters can be selected");
		remove.setLayoutData(listButtonGD);
		remove.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				int[] indexes = filterList.getSelectionIndices();
				if( indexes.length > 0 ) {
					filterList.remove(indexes);
				}
			}
		});
		Button selectAll = new Button(shell, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.setToolTipText("Select all filters in the list");
		selectAll.setLayoutData(listButtonGD);
		selectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				filterList.selectAll();
			}
		});
		Label blank1 = new Label(shell, SWT.NONE);

		/* middle row */
		newFilter = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData newFilterGD = new GridData();
		newFilterGD.horizontalSpan = 2;
		newFilterGD.horizontalAlignment = GridData.FILL;
		newFilterGD.verticalAlignment = GridData.FILL;
		newFilterGD.grabExcessHorizontalSpace = true;
		newFilter.setLayoutData(newFilterGD);
		Button add = new Button(shell, SWT.PUSH);
		add.setText(" Add ");
		add.setToolTipText("Add the preceding text as a filter");
		GridData addGD = new GridData();
		addGD.horizontalAlignment = GridData.FILL;
		addGD.verticalAlignment = GridData.FILL;
		add.setLayoutData(addGD);
		add.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				String addFilter = newFilter.getText();
				if( !addFilter.equals("") ) {
					filterList.add(addFilter);
					newFilter.setText("");
				}
			}
		});
		
		/* bottom row */
		invert = new Button(shell, SWT.CHECK);
		invert.setText("Encrypt all but these");
		GridData bGD = new GridData();
		bGD.grabExcessHorizontalSpace = true;
		bGD.horizontalAlignment = GridData.BEGINNING;
		invert.setLayoutData(bGD);
		
		Button ok = new Button(shell, SWT.NONE);
		ok.setText(" Save ");
		ok.setToolTipText("Save changes to the filters");
		ok.setLayoutData(listButtonGD);
		ok.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				
				/* save the filters to the set */
				String[] allFilters = filterList.getItems();
				for( String s : allFilters )
					filters.add(s);
				
				invertSave = invert.getSelection();
						
				shell.dispose();
			}
		});
		Button cancel = new Button(shell, SWT.NONE);
		cancel.setText("Cancel");
		cancel.setToolTipText("Close and do not save changes to the filters");
		cancel.setLayoutData(listButtonGD);
		cancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				shell.dispose();
			}		
		});
	}
	
	public void loadFilters(Set<String> filters) {
		this.filters.addAll(filters);
	}
	
	public Set<String> getFilters() {
		return filters;
	}
	
	public boolean getInvert() {
		return invertSave;
	}
	
	public static void main( String[] args ) {
		new FilterPopup("Standalone Test").open();
	}
}
