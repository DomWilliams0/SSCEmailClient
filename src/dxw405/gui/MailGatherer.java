package dxw405.gui;

import dxw405.EmailClient;
import dxw405.Mailbox;
import dxw405.util.Logging;

import javax.swing.*;
import java.awt.*;

public class MailGatherer
{
	static
	{
		UIManager.put("ProgressMonitor.progressText", "Working...");
	}

	private Mailbox mailbox;
	private EmailClient emailClient;

	public MailGatherer(Mailbox mailbox, EmailClient emailClient)
	{
		this.mailbox = mailbox;
		this.emailClient = emailClient;
	}

	public void run(Component parent)
	{
		ProgressMonitor monitor = new ProgressMonitor(parent, null, null, 0, 100);
		monitor.setMillisToPopup(0);
		monitor.setMillisToDecideToPopup(0);

		Worker worker = new Worker(monitor);
		worker.execute();
	}

	private class Worker extends SwingWorker<Boolean, Void>
	{
		private ProgressMonitor monitor;

		public Worker(ProgressMonitor monitor)
		{
			this.monitor = monitor;
		}

		@Override
		protected Boolean doInBackground()
		{
			if (!mailbox.isConnected())
			{
				monitor.setNote("Connecting to the mailbox...");
				monitor.setProgress(0);

				// connect
				boolean success = emailClient.connectToMailbox(mailbox);
				if (!success)
				{
					monitor.setNote("Could not connect!");
					return Boolean.FALSE;
				}
			}

			monitor.setNote("Gathering mail...");
			try
			{
				mailbox.gatherMail(monitor);
			} catch (Exception e)
			{
				Logging.warning("Could not gather mail", e);
				return Boolean.FALSE;
			}

			return Boolean.TRUE;
		}


	}
}
