package dxw405.gui.workers;

import dxw405.EmailClient;
import dxw405.Mailbox;

public class ConnectWorker extends Worker
{
	private Mailbox mailbox;
	private EmailClient emailClient;

	public ConnectWorker(Mailbox mailbox, EmailClient emailClient)
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
				error("Could not connect");
			else
				monitor.setProgress(Integer.MAX_VALUE);
		}
	}
}
