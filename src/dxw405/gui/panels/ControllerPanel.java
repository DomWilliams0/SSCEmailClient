package dxw405.gui.panels;

import dxw405.Email;
import dxw405.Mailbox;
import dxw405.PreparedEmail;
import dxw405.gui.workers.MailSendWorker;
import dxw405.util.JPanelMouseAdapter;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ControllerPanel extends JPanelMouseAdapter implements ActionListener
{
	private EmailPreview emailPreview;
	private EmailListView emailListView;
	private EmailComposePanel emailComposePanel;
	private Mailbox mailbox;

	public ControllerPanel(Mailbox mailbox)
	{
		this.mailbox = mailbox;
		emailComposePanel = new EmailComposePanel(this);

		// fill panel with tabbedpane
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Mailbox", createMailboxTab());
		tabbedPane.addTab("Compose", emailComposePanel);
		tabbedPane.addTab("Rules", new RulesPanel(mailbox));

		// add observer
		mailbox.addObserver(emailListView);
	}

	private JSplitPane createMailboxTab()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// centre: emails
		emailListView = new EmailListView(this, mailbox);
		splitPane.setLeftComponent(emailListView);

		// right: email view
		emailPreview = new EmailPreview();
		splitPane.setRightComponent(emailPreview);

		// limit divider movement
		final int minBorder = 200;
		splitPane.getLeftComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));
		splitPane.getRightComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));

		return splitPane;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// email selection
		if (e.getSource() instanceof JList)
		{
			JList list = (JList) e.getSource();
			Email selected = (Email) list.getSelectedValue();
			if (selected == null)
				return;

			emailPreview.view(selected);

			// set as read
			mailbox.setAsRead(selected);
			emailListView.updateElement(list.getSelectedIndex());

		}

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		// send email
		if (event.getActionCommand().equals(EmailComposePanel.SEND_COMMAND))
		{
			JButton sendButton = (JButton) event.getSource();

			PreparedEmail email = emailComposePanel.prepareEmail();
			if (email != null)
			{
				MailSendWorker worker = new MailSendWorker(mailbox, email);
				worker.setToggleComponent(sendButton);
				worker.run(this);
			}

		}
	}
}
