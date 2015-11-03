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
		EmailListView emailListView = new EmailListView(mailbox);
		add(emailListView, BorderLayout.CENTER);

		// right: email view
		EmailPreview emailPreview = new EmailPreview(mailbox);
		add(emailPreview, BorderLayout.EAST);

		mailbox.addObserver(emailListView);
	}
}
