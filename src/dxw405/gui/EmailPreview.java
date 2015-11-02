package dxw405.gui;

import dxw405.Mailbox;

import javax.swing.*;

public class EmailPreview extends JPanel
{
	private Mailbox mailbox;

	public EmailPreview(Mailbox mailbox)
	{
		this.mailbox = mailbox;
	}
}
