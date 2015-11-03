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

	public ControllerPanel(Mailbox mailbox)
	{

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// centre: emails
		EmailListView emailListView = new EmailListView(this, mailbox);
		splitPane.setLeftComponent(emailListView);

		// right: email view
		this.emailPreview = new EmailPreview();
		splitPane.setRightComponent(this.emailPreview);

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		// limit the split pane
		final int minBorder = 200;
		splitPane.getLeftComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));
		splitPane.getRightComponent().setMinimumSize(new Dimension(minBorder, (int) getMinimumSize().getHeight()));

		// add observer
		mailbox.addObserver(emailListView);
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		// email selection
		if (e.getSource() instanceof JList)
		{
			JList list = (JList) e.getSource();
			Email selected = (Email) list.getSelectedValue();

			emailPreview.view(selected);
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
