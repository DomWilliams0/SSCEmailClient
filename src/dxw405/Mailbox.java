package dxw405;

import dxw405.util.Logging;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.io.Closeable;
import java.util.Properties;

public class Mailbox implements Closeable
{
	private Store store;
	private Folder inbox;

	public boolean connect(String host, int port, String user, String password)
	{
		// imap properties
		Properties imapProperties = System.getProperties();
		imapProperties.setProperty("mail.store.protocol", "imaps");
		imapProperties.setProperty("mail.user", user);
		imapProperties.setProperty("mail.password", password);

		Session session = Session.getDefaultInstance(imapProperties);
		try
		{
			// connect
			Logging.info("Attempting to connect to " + host + ":" + port + " with email'" + user + "'");
			store = session.getStore("imaps");
			store.connect(host, port, user, password);
			Logging.info("Successfully connected to mailbox");

			// test: list all emails in inbox
			inbox = store.getFolder("inbox");
			if (!inbox.isOpen())
				inbox.open(Folder.READ_WRITE);

			return true;

		} catch (MessagingException e)
		{
			Logging.severe(e, "Could not connect to mailbox");
			close();
			return false;
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (inbox != null && inbox.isOpen())
				inbox.close(true);

			if (store != null)
				store.close();

			Logging.fine("Closed the mailbox");

		} catch (MessagingException me)
		{
			Logging.severe(me, "Could not close folder/store");
		}
	}
}
