package dxw405.gui.workers;

import dxw405.EmailClient;
import dxw405.Mailbox;
import dxw405.util.Logging;

public class MailGatherer extends Worker
{
	private Mailbox mailbox;
	private EmailClient emailClient;

	public MailGatherer(Mailbox mailbox, EmailClient emailClient)
	{
		this.mailbox = mailbox;
		this.emailClient = emailClient;
	}

	@Override
	protected void work(OptionalProgressMonitor monitor)
	{
		if (!mailbox.isConnected())
		{
			setIndeterminate(true);
			monitor.reset("Connecting to the mailbox...");

			// connect
			boolean success = emailClient.connectToMailbox(mailbox);
			if (!success)
			{
				monitor.setNote("Could not connect!");
				return;
			}
		}

		setIndeterminate(false);

		monitor.setNote("Gathering mail...");
		try
		{
			mailbox.gatherMail(monitor);
		} catch (Exception e)
		{
			Logging.warning("Could not gather mail", e);
			monitor.reset("Could not gather mail");
		}

	}


}
