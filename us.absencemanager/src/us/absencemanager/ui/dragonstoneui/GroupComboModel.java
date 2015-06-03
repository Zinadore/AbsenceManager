package us.absencemanager.ui.dragonstoneui;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import us.absencemanager.model.StudentGroup;

public class GroupComboModel extends AbstractListModel implements ComboBoxModel {
	List<StudentGroup> list;
	StudentGroup selection;
	
	public StudentGroup getElementAt(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	@Override
	public Object getSelectedItem() {
		return selection;
	}

	@Override
	public void setSelectedItem(Object item) {
		selection = (StudentGroup)item;
	}

	public void setData(List list) {
		this.list = list;
	}
	
	public int getSelectedId() {
		return selection.getID();
	}
}