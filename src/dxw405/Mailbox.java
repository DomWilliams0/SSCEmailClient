package dxw405;

import dxw405.util.Logging;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class Mailbox extends Observable implements Closeable
{
	private Store store;
	private Folder inbox;

	private List<Email> emails;

	public Mailbox()
	{
		emails = new ArrayList<>();
	}

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

	/**
	 * Gathers all messages from the mailbox
	 */
	public void gatherMail()
	{
		emails.clear();

		try
		{
			Message[] messages = inbox.getMessages();
			for (Message message : messages)
			{
				String subject = message.getSubject();
				String from = getSenders(message);
				String to = getRecipients(message);
				String content = getContent(message);
				Date date = message.getReceivedDate();

				Email email = new Email(subject, from, to, content, date);
				emails.add(email);
			}


		} catch (MessagingException e)
		{
			Logging.severe("Could not get messages", e);
		}

		setChanged();
		notifyObservers();
	}

	private String getSenders(Message message)
	{
		try
		{
			Address[] addresses = message.getFrom();
			if (addresses == null)
				return "";

			return concatEmails(addresses);

		} catch (MessagingException e)
		{
			Logging.warning("Could not parse senders", e);
			return "";
		}
	}

	private String getRecipients(Message message)
	{
		try
		{
			Address[] addresses = message.getRecipients(Message.RecipientType.TO);
			if (addresses == null)
				return "";

			return concatEmails(addresses);


		} catch (MessagingException e)
		{
			Logging.warning("Could not parse recipients", e);
			return "";
		}
	}

	private String getContent(Message message)
	{
		try
		{
			return message.getContentType().equals("TEXT/PLAIN") ? (String) message.getContent() : "";
		} catch (MessagingException | IOException e)
		{
			Logging.warning("Could not get message content", e);
			return "";
		}

	}

	private String concatEmails(Address[] addresses)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0, addressesLength = addresses.length; i < addressesLength; i++)
		{
			InternetAddress from = (InternetAddress) addresses[i];
			sb.append(from.getAddress());
			if (i != addressesLength - 1)
				sb.append(", ");
		}

		return sb.toString();
	}

	public List<Email> getEmails()
	{
		return emails;
	}
}
