package dxw405.gui;

import dxw405.EmailClient;
import dxw405.Mailbox;
import dxw405.util.Logging;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;

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

		private JProgressBar progressBar;
		private JOptionPane optionPane;

		public Worker(ProgressMonitor monitor)
		{
			this.monitor = monitor;
			this.progressBar = null;
			this.optionPane = null;
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

		private void setIndeterminate(boolean indeterminate)
		{
			progressBar.setIndeterminate(indeterminate);
		}

		private void findDialogComponents()
		{
			if (progressBar == null)
			{
				// get progress bar
				recurse((JDialog) monitor.getAccessibleContext().getAccessibleParent(), false);
			}
		}

		private boolean recurse(Container c, boolean cancelButtonDone)
		{
			Component[] components = c.getComponents();
			for (Component child : components)
			{
				// complete
				if (cancelButtonDone && optionPane != null && progressBar != null)
					return true;

				// cancel button
				if (!cancelButtonDone && child instanceof JButton && ((JButton) child).getText().equals("Cancel"))
				{
					cancelButtonDone = true;
					child.setVisible(false);
				}

				// progress bar
				else if (progressBar == null && child instanceof JProgressBar)
				{
					progressBar = (JProgressBar) child;
				}

				// option pane
				else if (optionPane == null && child instanceof JOptionPane)
				{
					optionPane = (JOptionPane) child;
				}

				// recurse
				if (recurse((Container) child, cancelButtonDone))
					return true;
			}

			return false;
		}


	}
}
