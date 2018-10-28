package gui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import form.Element;

public final class TableElement extends JTable {

	private static final long serialVersionUID = 1L;
	
	public TableElement() {
		super(new ElementModel());
	}
	
	@Override
	public TableModel getModel() {
		return super.getModel();
	}
}


final class ElementModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	
	private static final String[] ENTETES = new String[]{"POSITION", "ELEMENT", "T1", "T2"};
	
	private List<Element> listElements;
	
	public ElementModel() {
		listElements = new ArrayList<Element>();
	}
	
	@Override
	public String getColumnName(int column) {
		return ENTETES[column];
	}

	@Override
	public int getColumnCount() {
		return ENTETES.length;
	}

	@Override
	public int getRowCount() {
		return listElements.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		
		final NumberFormat nf = NumberFormat.getInstance();
		
		switch (col) {
		case 0:
			return listElements.get(row).getPosition();
		case 1:
			return listElements.get(row);
		case 2:
			return nf.format(listElements.get(row).getT1());
		case 3:
			return nf.format(listElements.get(row).getT2());
		default:
			return null;
		}
		
		
	}
	
	public final void clearList()
	{
		this.listElements.clear();
		fireTableDataChanged();
	}
	
	public final void addElement(Element element){
		this.listElements.add(element);
		fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
	}
	
	public final void addElement(int position, Element element)
	{
		this.listElements.add(position, element);
		fireTableRowsInserted(position, position);
	}
	
	public final void removeElement(int row)
	{
		this.listElements.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
}
