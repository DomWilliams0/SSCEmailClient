package dxw405.gui;

import dxw405.Email;
import dxw405.Mailbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ControllerPanel extends JPanel implements MouseListener
{
	private EmailPreview emailPreview;
	private EmailListView emailListView;
	private Mailbox mailbox;

	public ControllerPanel(Mailbox mailbox)
	{
		this.mailbox = mailbox;

		// fill panel with tabbedpane
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Mailbox", createMailboxTab());
		tabbedPane.addTab("Compose", new EmailComposePanel());

		// add observer
		mailbox.addObserver(this.emailListView);
	}

	private JSplitPane createMailboxTab()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// centre: emails
		this.emailListView = new EmailListView(this, mailbox);
		splitPane.setLeftComponent(this.emailListView);

		// right: email view
		this.emailPreview = new EmailPreview();
		splitPane.setRightComponent(this.emailPreview);

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
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}
}
