package us.absencemanager.ui.dragonstoneui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import us.absencemanager.controller.Controller;
import us.absencemanager.exceptions.AlreadyExistsException;
import us.absencemanager.exceptions.NoDataFoundException;

/**
 * 
 * @author Ioannis Boutsikas
 *
 */
public class MainFrame extends JFrame implements AdditionListener, PopupListener {
	private Controller c = Controller.getInstance();
	private ControlPanel controlPanel;
	private JPanel leftPane;
	private TablePanel tablePanel;
	private MenuBar menuBar;
	private ExistingStudentPanel eStPanel;
	private int currentGroupId;
	private DisplayAbsencesDialog absenceDialog;
	
	public MainFrame() {
		try { 
			c.loadStudents();
		} catch (ClassNotFoundException e1) {

		} catch (IOException e1) { 
	
		}
		
		try {
			c.loadGroups();
		} catch (ClassNotFoundException e1) {

		} catch (IOException e1) {
			String groupName = JOptionPane.showInputDialog(MainFrame.this, "We detected that you have no files saved.\n Please provide a name for your first group!", "Fresh install", JOptionPane.INFORMATION_MESSAGE);
			try {
				c.addStudentGroup(groupName);
				c.saveGroups("");
			} catch (AlreadyExistsException e) {
				JOptionPane.showMessageDialog(MainFrame.this, "The student already exists (ID should be unique)!", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				
			}
		}
		
		try {
			c.loadUnits();
		} catch (ClassNotFoundException e1) {

		} catch (IOException e1) { 
	
		}
		
		menuBar = new MenuBar();
		controlPanel = new ControlPanel(c.getStudentGroups(), c.getUnits());
		leftPane = new JPanel(new GridLayout(2, 1));
		tablePanel = new TablePanel();
		tablePanel.addPopupListener(this);
		eStPanel = new ExistingStudentPanel();
		
		/**
		 * Load students from the selected group to the JTable
		 */
		controlPanel.setControlListener(new ControlListener() {
			@Override
			public void loadEvent(ControlEvent ce) {
				try {
					tablePanel.initModel();
					tablePanel.setModelListener(MainFrame.this);
					tablePanel.setData(c.getStudentsInGroup(ce.getGroupId()));
					currentGroupId = ce.getGroupId();
					eStPanel.populateList(c.getStudents(), c.getStudentsInGroup(currentGroupId));
				} catch (NoDataFoundException e) {
					JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			@Override
			public void deleteUnitEvent(String unitId) {
				try {
					c.deleteUnit(unitId);
					controlPanel.refreshUnits(c.getUnits());
				} catch (NoDataFoundException e) {
					JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			@Override
			public void deleteGroupEvent(int groupId) {
				try {
					c.deleteStudentGroup(groupId);
					controlPanel.refreshGroups(c.getStudentGroups());
				} catch (NoDataFoundException e) {
					JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		eStPanel.populateList(c.getStudents());
		eStPanel.setAdditionListener(this);
		eStPanel.setPopupListener(this);
		menuBar.setAdditionListener(this);
		
		
		setJMenuBar(menuBar);
		leftPane.add(controlPanel);
		leftPane.add(eStPanel);
		add(leftPane, BorderLayout.WEST);
		add(tablePanel, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int answer = JOptionPane.showConfirmDialog(MainFrame.this, "Do you want to save your changes before closing", "Closing", JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION){
                	try {
						c.saveAll("");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "An error occured while saving", "Saving error", JOptionPane.ERROR_MESSAGE);
					}
                } else {
                	MainFrame.this.dispose();
                }
			}
		});
		setTitle("Absence Manager");
		Dimension dim = new Dimension(1000, 600);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setSize(dim);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	/**
	 * This method handles all additions from the new menu.
	 */
	public void additionEventRaised(CustomEvent ev) {
		if(ev instanceof StudentEvent) { // Student Events
			StudentEvent s = (StudentEvent)ev;
			try {
				c.addStudent(s.getId(), s.getfName(), s.getlName(), s.getEmail());
				eStPanel.populateList(c.getStudents());
				if(s.getAdd()) {
					try {
						c.addStudentToGroup(s.getId(), currentGroupId);
						tablePanel.refresh();
						eStPanel.populateList(c.getStudents(), c.getStudentsInGroup(currentGroupId));
					} catch (Exception e) {
						JOptionPane.showMessageDialog(MainFrame.this, "There is no group to add the student", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (AlreadyExistsException e) {
				JOptionPane.showMessageDialog(MainFrame.this, "The student already exists (ID should be unique)!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ev instanceof GroupEvent) { // Group Events
			GroupEvent ge = (GroupEvent) ev;
			try {
				c.addStudentGroup(ge.getName());
				controlPanel.refreshGroups(c.getStudentGroups());
			} catch (AlreadyExistsException e) {
				JOptionPane.showMessageDialog(MainFrame.this, "Could not add the group!\nIf you are getting this there is something really wrong with the IDs", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ev instanceof UnitEvent) { // Unit Events 
			UnitEvent ue = (UnitEvent) ev;
			try {
				c.addUnit(ue.getId(), ue.getName(), ue.getAbs());
				controlPanel.refreshUnits(c.getUnits());
			} catch (AlreadyExistsException e) {
				JOptionPane.showMessageDialog(MainFrame.this, "The unit already exists (ID should be unique)!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public void saveEventRaised() {
		try {
			c.saveAll("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void moveToGroupEvent(String id) {
		try {
			if (currentGroupId != 0) {
				c.addStudentToGroup(id, currentGroupId);
				tablePanel.setData(c.getStudentsInGroup(currentGroupId));
				tablePanel.refresh();
				eStPanel.populateList(c.getStudents(), c.getStudentsInGroup(currentGroupId));
			} else {
				JOptionPane.showMessageDialog(this, "You should first load a group", "Warning", JOptionPane.WARNING_MESSAGE);
			}			
		} catch (NoDataFoundException e) {
			e.printStackTrace();
		} catch (AlreadyExistsException e) {
			JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void removeFromGroupEvent() {
		try {
			String sId = tablePanel.getSelectedStudent();
			int row = tablePanel.getSelectedRow();
			c.removeStudentFromGroup(sId, currentGroupId);
			tablePanel.refresh(row);
			eStPanel.populateList(c.getStudents(), c.getStudentsInGroup(currentGroupId));
		} catch (NoDataFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(MainFrame.this, "You should load a group first", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void addAbsenceEvent(String id) {
		ControlPanelInfo cpi = controlPanel.getPanelInfo();
		try {
			c.addAbsenceToStudent(id, cpi.getUnitId(), cpi.getClassroom(), cpi.getDateTime());
		} catch (NoDataFoundException e) {
			JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void removeAbsenceEvent(String id) {
		ControlPanelInfo cpi = controlPanel.getPanelInfo();
		try {
			c.removeAbsenceFromStudent(id, cpi.getUnitId(), cpi.getClassroom(), cpi.getDateTime());
		} catch (NoDataFoundException e) {
			JOptionPane.showMessageDialog(MainFrame.this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void deleteStudentEvent(String id) throws NoDataFoundException {
		c.deleteStudent(id);
		if(currentGroupId >= 1){
			c.removeStudentFromGroup(id, currentGroupId);
			int row = tablePanel.getSelectedRow();
			tablePanel.refresh(row);
			eStPanel.populateList(c.getStudents(), c.getStudentsInGroup(currentGroupId));
		} else {
			eStPanel.populateList(c.getStudents());
		}
	}

	@Override
	public void displayAbsencesEvent(String studentId) throws NoDataFoundException {
		absenceDialog = new DisplayAbsencesDialog(MainFrame.this, studentId);
		
	}
}
