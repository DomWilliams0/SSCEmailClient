package dxw405.gui;

import dxw405.Mailbox;

import javax.swing.*;

public class EmailListView extends JPanel
{
	private Mailbox mailbox;

	public EmailListView(Mailbox mailbox)
	{
		this.mailbox = mailbox;
	}
}
