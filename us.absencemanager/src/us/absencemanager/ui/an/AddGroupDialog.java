package us.absencemanager.ui.an;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import us.absencemanager.controller.Controller;
import us.absencemanager.exceptions.AlreadyExistsException;
import us.absencemanager.exceptions.NoDataFoundException;

/**
 * @authors Athanasios Doulgeris , Nikolaos Doumpalas
 *
 */
public class AddGroupDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5641772545368260891L;
	private JPanel mainContainer, btnPanel, centerPanel, gridPanel;
	private JButton createButton;
	private JLabel titleLb;
	private JTable studentsTable;
	private JTextField titleTxt;
	private GridBagConstraints cons;
	private GridBagLayout gridBag;
	private Controller cont;
	private AbsenceTableModel model;
	private AddGroupDialog thisFrame;

	/**
	 * @param fr
	 * @param cont
	 */
	public AddGroupDialog(JFrame fr, Controller cont){
		super(fr, ModalityType.APPLICATION_MODAL);
		thisFrame = this;
		this.cont = cont;
		this.mainContainer = (JPanel) this.getContentPane();
		this.mainContainer.setLayout(new BorderLayout());

		this.addComponents();
		this.addListeners();


		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(fr);


	}

	/**
	 * 
	 */
	private void addComponents(){
		//BUTTON CONTAINER
		this.btnPanel = new JPanel();
		this.btnPanel.setLayout(new FlowLayout());

		this.createButton = new JButton("Create");
		this.btnPanel.add(createButton);

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new BorderLayout());

		this.cons = new GridBagConstraints();
		this.gridBag = new GridBagLayout(); 

		//TEXTFIELDS AND LABELS
		this.gridPanel = new JPanel();
		this.gridPanel.setLayout(gridBag);

		this.titleLb = new JLabel("Group title:");
		this.cons.gridx = 0;
		this.cons.gridy = 0;
		this.gridBag.setConstraints(titleLb, cons);
		this.gridPanel.add(titleLb);

		this.titleTxt = new JTextField("", 10);
		this.cons.gridx = 1;
		this.cons.gridy = 0;
		this.gridBag.setConstraints(titleTxt, cons);
		this.gridPanel.add(titleTxt);	

		this.createTable();

		this.centerPanel.add(gridPanel, BorderLayout.NORTH);
		this.mainContainer.add(centerPanel, BorderLayout.CENTER);
		this.mainContainer.add(btnPanel, BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	private void createTable(){
		this.studentsTable = new JTable();		

		this.model = new AbsenceTableModel();
		this.studentsTable.setModel(model);
		try{
			this.model.setData(cont.getStudents());
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		this.model.fireTableDataChanged();

		this.centerPanel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
	}

	/**
	 * 
	 */
	private void addListeners(){
		createButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int groupId = -1, counter = 0;
				try {
					cont.addStudentGroup(titleTxt.getText());
				} catch (AlreadyExistsException e) {
					
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}	

				for(int i = 0; i < cont.getStudentGroups().size(); i++){
					System.out.println("searching: "+titleTxt.getText()+", "+ cont.getStudentGroups().get(i).getName());
					if(cont.getStudentGroups().get(i).getName().equalsIgnoreCase(titleTxt.getText())){
						
						groupId = cont.getStudentGroups().get(i).getID();
						System.out.println("found match "+titleTxt.getText()+" "+ cont.getStudentGroups().get(i).getName());
					}
				}
				
				for(int i = 0; i < model.getRowCount(); i++){
					if((Boolean)model.getValueAt(i, 3) == true){
						try{
							cont.addStudentToGroup((String)model.getValueAt(i,0), groupId); 
							counter++;
						} catch (NoDataFoundException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, "Not number", "Error", JOptionPane.ERROR_MESSAGE);

						} catch (AlreadyExistsException e){
							JOptionPane.showMessageDialog(null, "Student already in group", "Error", JOptionPane.ERROR_MESSAGE);
						}

					}
				}
				System.out.println(groupId);
				JOptionPane.showMessageDialog(null, "Group Created Succesfully, "+ counter+" students added", "Success", JOptionPane.INFORMATION_MESSAGE);
				
				int answer = JOptionPane.showConfirmDialog(thisFrame, "The Window will close", "Closing", JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION){
                	
					thisFrame.dispose();
					
                }

			}
		});

	}

}
