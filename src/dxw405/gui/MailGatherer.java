package dxw405.gui;

import dxw405.EmailClient;
import dxw405.Mailbox;
import dxw405.util.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
		monitor.setNote("Working...");
		monitor.setProgress(0);

		Worker worker = new Worker(monitor);
		worker.findDialogComponents();
		worker.execute();
	}

	private class Worker extends SwingWorker<Boolean, Void>
	{
		private ProgressMonitor monitor;

		private JButton cancelButton;
		private JProgressBar progressBar;

		public Worker(ProgressMonitor monitor)
		{
			this.monitor = monitor;
			this.cancelButton = null;
			this.progressBar = null;
		}

		@Override
		protected Boolean doInBackground()
		{
			if (!mailbox.isConnected())
			{
				setIndeterminate(true);
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

			setIndeterminate(false);

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

		private void findDialogComponents()
		{
			if (cancelButton == null || progressBar == null)
			{
				// prevent closing
				JDialog dialog = (JDialog) monitor.getAccessibleContext().getAccessibleParent();
				dialog.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent e)
					{
						EmailClient.halt("Mailbox connection interrupted");
					}
				});

				// get cancel button and progress bar
				recurse(dialog);
			}
		}

		private boolean recurse(Container c)
		{
			Component[] components = c.getComponents();
			for (Component child : components)
			{
				// complete
				if (cancelButton != null && progressBar != null)
					return true;

				// recurse
				if (child != null && recurse((Container) child))
					return true;

				// cancel button
				if (cancelButton == null && child instanceof JButton && ((JButton) child).getText().equals("Cancel"))
				{
					cancelButton = (JButton) child;
				}

				// progress bar
				else if (progressBar == null && child instanceof JProgressBar)
				{
					progressBar = (JProgressBar) child;
				}
			}

			return false;
		}


		private void setIndeterminate(boolean indeterminate)
		{
			progressBar.setIndeterminate(indeterminate);
			cancelButton.setVisible(!indeterminate);
		}


	}
}
