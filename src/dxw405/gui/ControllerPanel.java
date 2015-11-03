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

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// centre: emails
		EmailListView emailListView = new EmailListView(mailbox);
		splitPane.setLeftComponent(emailListView);

		// right: email view
		EmailPreview emailPreview = new EmailPreview(mailbox);
		splitPane.setRightComponent(emailPreview);

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		final int minBorder = 200;
		splitPane.getLeftComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));
		splitPane.getRightComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));

		mailbox.addObserver(emailListView);
	}
}
