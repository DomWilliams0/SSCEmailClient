package dxw405.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MenuBar extends JMenuBar
{
	public MenuBar(EmailClientGUI gui)
	{
		// exit
		addItem("Exit", 'x', new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gui.close();
			}
		});

		// compose
		addItem("Compose", 'c', new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(gui.getFrame(), "Compose mail!");
			}
		});

		// search
		addItem("Search", 's', new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(gui.getFrame(), "Search mail!");
			}
		});
	}

	private void addItem(String title, char mneumonic, Action action)
	{
		JMenuItem item = new JMenuItem(title, mneumonic);
		item.setAction(action);
		add(item);
	}
}
