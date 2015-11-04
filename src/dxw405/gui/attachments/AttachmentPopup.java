package dxw405.gui.attachments;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;

public class AttachmentPopup extends JPopupMenu
{
	private JLabel component;

	public AttachmentPopup(AttachmentSelection attachmentSelection)
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
	}

	public void display(Component component)
	{
		this.component = (JLabel) component;
		show(component, component.getX(), component.getY() + component.getHeight());
	}
}