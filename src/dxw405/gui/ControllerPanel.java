package dxw405.gui;

import dxw405.Mailbox;

import javax.swing.*;
import java.awt.*;

public class ControllerPanel extends JPanel
{
	private Mailbox mailbox;

	public ControllerPanel(Mailbox mailbox)
	{
		this.mailbox = mailbox;

		// add components
		setLayout(new BorderLayout());

		// centre: emails
		add(new EmailListView(mailbox), BorderLayout.CENTER);

		// right: email view
		add(new EmailPreview(mailbox), BorderLayout.EAST);
	}
}
