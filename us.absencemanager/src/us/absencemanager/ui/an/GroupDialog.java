package us.absencemanager.ui.an;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import us.absencemanager.controller.Controller;
import us.absencemanager.exceptions.AlreadyExistsException;
import us.absencemanager.exceptions.NoDataFoundException;

public class GroupDialog extends JDialog{
	
	private JPanel mainContainer, btnPanel;
	private JButton proceedBtn, addStudBtn;
	private Controller cont;
	private String groupName;
	private JTable groupTable;
	private GroupTableModel model;
	private int groupId;
	private JFrame thisFrame;
	
	public GroupDialog(JFrame fr, Controller cont, String groupName){
		super(fr);
		
		thisFrame = fr;
		this.cont = cont;
		this.groupName = groupName;
		mainContainer = (JPanel) this.getContentPane();
		mainContainer.setLayout(new BorderLayout());
		
		//BUTTON CONTAINER
		btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		
		addStudBtn = new JButton("Assign new students");
		proceedBtn = new JButton("RemoveStudents");
		btnPanel.add(proceedBtn);
		btnPanel.add(addStudBtn);
		
		this.addComponents();
		
		mainContainer.add(btnPanel, BorderLayout.SOUTH);
		this.addListeners();
		
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.pack();	
	}
	
	private void addComponents(){
		groupId = -1;
		for(int i = 0; i<cont.getStudentGroups().size(); i++){
			if(cont.getStudentGroups().get(i).getName().equalsIgnoreCase(groupName)){
				groupId = cont.getStudentGroups().get(i).getID();
			}
		}
		
		createTable();
		
		
	}
	
	private void createTable(){
		this.groupTable = new JTable();		

		this.model = new GroupTableModel();
		this.groupTable.setModel(model);
		try{
			try {
				this.model.setData(cont.getStudentsInGroup(groupId));
			} catch (NoDataFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		this.model.fireTableDataChanged();

		this.mainContainer.add(new JScrollPane(groupTable), BorderLayout.CENTER);
	}
	
	private void addListeners(){
		
		proceedBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				for(int i = 0; i < model.getRowCount(); i++){
					System.out.println("searching, "+model.getValueAt(i, 3));
					if((Boolean)model.getValueAt(i, 3) == true){
						try{
							System.out.println("delete");
							cont.removeStudentFromGroup((String)model.getValueAt(i,0), groupId); 
							model.setValueAt(false, i, 3);
							model.fireTableDataChanged();
						} catch (NoDataFoundException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, "Not number", "Error", JOptionPane.ERROR_MESSAGE);

						}

					}
				}
				
			}
			
		});
		
		addStudBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				StudentToGroupDialog studDlg = new StudentToGroupDialog(thisFrame, cont,groupId);
				
			}
			
		});
		
	}
}
