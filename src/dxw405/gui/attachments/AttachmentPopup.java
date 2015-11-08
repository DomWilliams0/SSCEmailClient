package dxw405.gui.attachments;

import dxw405.Email;
import dxw405.gui.workers.OpenAttachmentWorker;
import dxw405.gui.workers.SaveAttachmentWorker;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;

public class AttachmentPopup extends JPopupMenu
{
	private JLabel component;
	private Email email;

	public AttachmentPopup(AttachmentSelection attachmentSelection, boolean editable)
	{
		if (editable)
		{
			add(new JMenuItem(new AbstractAction("Remove")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachmentSelection.removeAttachment(component.getText());
				}
			}));

			add(new JMenuItem(new AbstractAction("Remove All")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachmentSelection.removeAllAttachments();
				}
			}));
		} else
		{
			add(new JMenuItem(new AbstractAction("Save")
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					new SaveAttachmentWorker(email, component.getText()).run(null);
				}
			}));
			add(new JMenuItem(new AbstractAction("Open")
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					new OpenAttachmentWorker(email, component.getText()).run(null);
				}
			}));
		}
	}

	public void display(Component component, Email email)
	{
		this.component = (JLabel) component;
		this.email = email;
		show(component, component.getX(), component.getY() + component.getHeight());
	}
}