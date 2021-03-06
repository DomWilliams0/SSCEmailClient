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
			// try to connect
			ConnectWorker connectWorker = new ConnectWorker(mailbox, emailClient);
			connectWorker.run(null);
			connectWorker.waitUntilDone();

			if (!mailbox.isConnected())
			{
				Logging.severe("Can't connect to gather mail");
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
			error("Could not gather mail");
		}

	}


}
