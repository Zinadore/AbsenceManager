package us.absencemanager.ui.dragonstoneui;

import java.util.EventListener;
/**
 * 
 * @author Ioannis Boutsikas
 *
 */
public interface ControlListener extends EventListener {
	public void loadEvent(ControlEvent e);
	public void deleteUnitEvent(String unitId);
	public void deleteGroupEvent(int groupId);
}
