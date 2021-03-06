package us.absencemanager.ui.dragonstoneui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
/**
 * 
 * @author Ioannis Boutsikas
 *
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	private JMenu fileMenu;
	private JMenu newMenu;
	private JMenuItem studentMenuItem;
	private JMenuItem groupMenuItem;
	private JMenuItem unitMenuItem;
	private JMenuItem exitMenuItem;
	private JMenuItem saveMenuItem;
	private AdditionListener listener;
	
	
	public MenuBar() {
		fileMenu = new JMenu("File");
		exitMenuItem = new JMenuItem("Exit");
		saveMenuItem = new JMenuItem("Save");
		fileMenu.add(saveMenuItem);
		fileMenu.add(exitMenuItem);
		
		newMenu = new JMenu("New");
		studentMenuItem = new JMenuItem("Student...");
		groupMenuItem = new JMenuItem("Student Group...");
		unitMenuItem = new JMenuItem("Unit...");
		newMenu.add(studentMenuItem);
		newMenu.add(groupMenuItem);
		newMenu.add(unitMenuItem);
		
		
		add(fileMenu);
		add(newMenu);
		
		studentMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewStudentDialog nsd = new NewStudentDialog(SwingUtilities.getWindowAncestor(MenuBar.this));
				nsd.setAdditionListener(listener);
				nsd.setVisible(true);
			}
		});
		groupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewGroupDialog ngd = new NewGroupDialog(SwingUtilities.getWindowAncestor(MenuBar.this));
				ngd.setAdditionListener(listener);
				ngd.setVisible(true);
			}
		});
		unitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewUnitDialog nud = new NewUnitDialog(SwingUtilities.getWindowAncestor(MenuBar.this));
				nud.setAdditionListener(listener);
				nud.setVisible(true);
			}
		});
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.saveEventRaised();
			}
		});
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(WindowListener l: SwingUtilities.getWindowAncestor(MenuBar.this).getWindowListeners()) {
					l.windowClosing(new WindowEvent(SwingUtilities.getWindowAncestor(MenuBar.this), WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}
	
	public void setAdditionListener(AdditionListener listener) {
		this.listener = listener;				
	}
}
