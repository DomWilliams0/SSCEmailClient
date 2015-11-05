package dxw405.gui.workers;

import dxw405.Mailbox;
import dxw405.PreparedEmail;
import dxw405.util.Logging;

import javax.mail.MessagingException;

public class MailSendWorker extends Worker
{
	private Mailbox mailbox;
	private PreparedEmail email;

	public MailSendWorker(Mailbox mailbox, PreparedEmail email)
	{
		this.mailbox = mailbox;
		this.email = email;
	}

	@Override
	protected void work(OptionalProgressMonitor monitor)
	{
		monitor.reset("Sending email...");
		setIndeterminate(true);

		try
		{
			mailbox.sendEmail(email);
		} catch (MessagingException e)
		{
			Logging.severe("Could not send email", e);
			error("Could not send email: \n" + e.getMessage());
		}
	}
}
