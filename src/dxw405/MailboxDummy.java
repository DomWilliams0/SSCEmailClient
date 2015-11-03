package dxw405;

import dxw405.util.Utils;

import javax.swing.*;
import java.util.Date;

public class MailboxDummy extends Mailbox
{
	@Override
	public boolean connect(String host, int port, String user, String password)
	{
		return true;
	}

	@Override
	public void close()
	{
	}

	@Override
	public void gatherMail()
	{
		for (int i = 0; i < 100; i++)
		{
			addEmail(new Email("A long email subject! No no no much much longer please!",
					"sender@gmail.com", "me@gmail.com", "wow this is a long email, telling you all about something",
					new Date(), Utils.RANDOM.nextBoolean()));
		}

		setChanged();
		notifyObservers();
	}

	@Override
	public void gatherMail(ProgressMonitor monitor)
	{
		gatherMail();
		monitor.setProgress(100);
	}
}
