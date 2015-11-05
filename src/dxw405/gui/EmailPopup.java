package dxw405.gui;

import dxw405.Email;
import dxw405.Mailbox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class EmailPopup extends JPopupMenu
{
	private Email email;

	public EmailPopup(Mailbox mailbox)
	{
		add(new JMenuItem(new AbstractAction("Mark as Unread")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				mailbox.setAsRead(email, false);
			}
		}));
	}

	public void display(MouseEvent mouseEvent, Email email)
	{
		this.email = email;
		show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
	}
}
