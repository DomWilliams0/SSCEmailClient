package dxw405;

import dxw405.util.Logging;

import javax.mail.*;
import java.io.Closeable;
import java.util.Properties;

public class Mailbox implements Closeable
{
	private Store store;
	private Folder inbox;

	/**
	 * Connects to the given mailbox
	 *
	 * @param host     The imap server
	 * @param port     The port to connect to
	 * @param user     The email address
	 * @param password The password
	 * @return If the operation was successful
	 */
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
			Logging.severe("Could not connect to mailbox", e);
			close();
			return false;
		}
	}

	/**
	 * @return The messages in the inbox. An empty array will be returned if the operation failed
	 */
	public Message[] getMessages()
	{
		try
		{
			return inbox.getMessages();
		} catch (MessagingException e)
		{
			Logging.severe("Could not get messages", e);
			return new Message[]{};
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
			Logging.severe("Could not close folder/store", me);
		}
	}
}
