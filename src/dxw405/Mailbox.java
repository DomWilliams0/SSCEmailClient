package dxw405;

import com.sun.mail.imap.IMAPMessage;
import dxw405.gui.panels.RulesPanel;
import dxw405.util.Logging;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.BodyTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.swing.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Mailbox extends Observable implements Closeable
{
	private Store store;
	private Folder inbox;
	private Session session;
	private boolean connected;

	private List<Email> emails;
	private String emailAddress;
	private int maxEmails;
	private Message[] filteredMessages, allMessages;

	public Mailbox(int maxEmails)
	{
		this.maxEmails = maxEmails;
		connected = false;
		emails = new ArrayList<>();
	}


	public static boolean readContent(Message message, Email.EmailContent emailContent)
	{
		try
		{
			IMAPMessage imapMessage = (IMAPMessage) message;
			imapMessage.setPeek(true);
			Logging.fine("Fetching message content (" + message.getSubject() + ")");

			// simple text
			if (message.isMimeType("text/*"))
			{
				emailContent.setContent((String) message.getContent());
				return true;
			}

			StringBuilder sb = new StringBuilder();
			Multipart multipart = (Multipart) message.getContent();
			for (int i = 0; i < multipart.getCount(); i++)
			{
				BodyPart bodyPart = multipart.getBodyPart(i);

				try
				{
					String name = bodyPart.getFileName();

					if (name == null)
					{
						// text content
						if (bodyPart.isMimeType("text/*"))
							sb.append(bodyPart.getContent());

						continue;
					}

					// attachment
					emailContent.addAttachment(name, i);
					Logging.fine("Found attachment '" + name + "'");


				} catch (MessagingException e)
				{
					Logging.warning("Ignored body part: " + bodyPart.getDescription(), e);
				}

			}

			emailContent.setContent(sb.toString());
			return true;

		} catch (MessagingException | IOException e)
		{
			Logging.warning("Could not get message content", e);
			return false;
		}

	}

	/**
	 * Connects to the given mailbox
	 *
	 * @param host     The incoming server host
	 * @param port     The port to connect to for incoming mail
	 * @param outHost  The outgoing server host
	 * @param outPort  The port to connect to for sending mail
	 * @param user     The email address
	 * @param password The password   @return If the operation was successful
	 */
	public boolean connect(String host, int port, String outHost, String outPort, String user, String password)
	{
		// connection properties
		Properties connProps = System.getProperties();
		connProps.put("mail.store.protocol", "imaps");
		connProps.put("mail.smtp.auth", "true");
		connProps.put("mail.smtp.starttls.enable", "true");
		connProps.put("mail.smtp.host", outHost);
		connProps.put("mail.smtp.port", outPort);

		emailAddress = user;

		session = Session.getDefaultInstance(connProps, new Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(user, password);
			}
		});
		try
		{
			// connect
			Logging.info("Attempting to connect to " + host + ":" + port + " with email'" + user + "'");
			store = session.getStore("imaps");
			store.connect(host, port, user, password);
			connected = true;
			Logging.info("Successfully connected to mailbox");

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

	public boolean isConnected() {return connected;}

	public List<Email> getEmails()
	{
		return emails;
	}

	public void setAsRead(Email email)
	{
		setAsRead(email, true);
	}

	public void setAsRead(Email email, boolean read)
	{
		email.setAsRead(read);
		setFlag(email, Flags.Flag.SEEN, read, true);
	}

	private void setFlag(Email email, Flags.Flag flag, boolean value, boolean notifyObservers)
	{
		try
		{
			Message mailboxReference = email.getMailboxReference();
			if (mailboxReference == null)
				return;

			mailboxReference.setFlag(flag, value);

			if (notifyObservers)
			{
				setChanged();
				notifyObservers(email);
			}
		} catch (MessagingException e)
		{
			Logging.severe("Could not set as read", e);
		}

	}

	public void sendEmail(PreparedEmail email) throws MessagingException
	{
		if (emailAddress == null)
			throw new MessagingException("Invalid from address");

		MimeMessage message = new MimeMessage(session);
		message.setFrom(emailAddress);
		message.setSubject(email.getSubject());

		Multipart multipart = new MimeMultipart();

		// body
		BodyPart content = new MimeBodyPart();
		content.setContent(email.getBody(), "text/plain");
		multipart.addBodyPart(content);

		// recipients
		for (Field field : Field.values())
		{
			if (!field.isAddress())
				continue;

			List<Address> addressList = email.getRecipients(field.getRecipientType());
			Address[] addresses = new Address[addressList.size()];
			for (int i = 0, addressListSize = addressList.size(); i < addressListSize; i++)
				addresses[i] = addressList.get(i);

			message.setRecipients(field.getRecipientType(), addresses);
		}

		// attachments
		List<File> attachments = email.getAttachments();
		if (!attachments.isEmpty())
		{
			for (File attachment : attachments)
			{
				try
				{
					MimeBodyPart attachmentBodyPart = new MimeBodyPart();
					attachmentBodyPart.attachFile(attachment);
					multipart.addBodyPart(attachmentBodyPart);
				} catch (IOException e)
				{
					throw new MessagingException("Could not attach file '" + attachment.getName() + "': " + e.getMessage());
				}
			}
		}

		message.setContent(multipart);

		Transport.send(message);
	}

	public void search(String term)
	{
		SearchTerm st;
		if (term == null || term.isEmpty())
		{
			st = new SearchTerm()
			{
				@Override
				public boolean match(Message message)
				{
					return true;
				}
			};
		} else
		{
			st = new OrTerm(
					new SubjectTerm(term),
					new BodyTerm(term)
			);
		}

		try
		{
			Logging.fine("Searching for '" + term + "'");
			filteredMessages = inbox.search(st, allMessages);
			parseEmails(null);

			setChanged();
			notifyObservers();
		} catch (MessagingException e)
		{
			Logging.severe("Could not search messages for '" + term + "'", e);
		}
	}

	/**
	 * Fills the emails list with the Messages that should be displayed
	 *
	 * @param monitor An optional progress monitor
	 */
	private void parseEmails(ProgressMonitor monitor) throws MessagingException
	{
		emails.clear();

		if (monitor != null)
		{
			monitor.setMaximum(filteredMessages.length);
			monitor.setProgress(0);
		}


		for (int i = 0, messagesLength = filteredMessages.length; i < messagesLength; i++)
		{
			// cancelled early
			if (monitor != null && monitor.isCanceled())
			{
				monitor.setNote("Cancelled");
				Logging.info("Cancelled email collection");
				break;
			}

			Message message = filteredMessages[i];
			Flags flags = message.getFlags();

			String subject = message.getSubject();
			String from = getSenders(message);
			String to = getRecipients(message);
			Date date = message.getReceivedDate();
			boolean read = flags.contains(Flags.Flag.SEEN);
			boolean recent = flags.contains(Flags.Flag.RECENT);
			String[] userFlags = flags.getUserFlags();

			Email email = new Email(subject, from, to, date, read, recent, userFlags, message);
			addEmail(email);

			if (monitor != null)
			{
				monitor.setProgress(i + 1);
				monitor.setNote("Gathered " + (i + 1) + "/" + filteredMessages.length);
			}
		}
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

	protected void addEmail(Email email) {emails.add(email);}

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

	public void applyRules(Set<RulesPanel.Rule> rules)
	{
		applyRules(rules, null);
	}

	public void applyRules(Set<RulesPanel.Rule> rules, ProgressMonitor monitor)
	{
		// remove all user flags
		try
		{
			if (monitor != null)
			{
				monitor.setNote("Removing previous flags");
				monitor.setProgress(0);
				monitor.setMaximum(emails.size());
			}

			for (int i = 0, emailsSize = emails.size(); i < emailsSize; i++)
			{
				Message m = emails.get(i).getMailboxReference();
				String[] userFlags = m.getFlags().getUserFlags();
				for (String userFlag : userFlags)
					m.setFlags(new Flags(userFlag), false);

				if (monitor != null)
					monitor.setProgress(i + 1);
			}
		} catch (MessagingException e)
		{
			Logging.severe("Could not remove all user flags", e);
			if (monitor != null)
			{
				monitor.setNote("Failed to remove user flags");
				monitor.setProgress(0);
			}
		}

		if (monitor != null)
		{
			monitor.setNote("Applying rules");
			monitor.setProgress(0);
		}

		// apply rules
		int ruleIndex = 1;
		rules.removeIf(r -> !r.isValid());

		for (RulesPanel.Rule rule : rules)
		{
			if (monitor != null)
			{
				monitor.setNote("Applying rule " + ruleIndex++ + "/" + rules.size());
				monitor.setProgress(0);
			}

			Flags flag = new Flags(rule.getFlag());

			try
			{
				for (int i = 0, emailsSize = emails.size(); i < emailsSize; i++)
				{
					Email email = emails.get(i);
					Message m = email.getMailboxReference();
					if (monitor != null && i != monitor.getMaximum() - 1)
						monitor.setProgress(i + 1);

					// add new flag
					if (rule.satisfiedBy(email))
						m.setFlags(flag, true);
				}
			} catch (MessagingException e)
			{
				Logging.severe("Could not apply rule to emails: " + rule, e);
			}
		}

		Logging.fine("Applied " + rules.size() + " rules to " + emails.size() + " emails");
		gatherMail(monitor);
	}

	/**
	 * Gathers all messages from the mailbox, updating the given (optional) progress monitor
	 *
	 * @param monitor The optional progress monitor to update
	 */
	public void gatherMail(ProgressMonitor monitor)
	{
		try
		{
			final int lastEmail = inbox.getMessageCount();
			int firstEmail = maxEmails <= 0 ? 1 : lastEmail - maxEmails + 1;

			allMessages = inbox.getMessages(firstEmail, lastEmail);
			filteredMessages = new Message[allMessages.length];
			System.arraycopy(allMessages, 0, filteredMessages, 0, filteredMessages.length);

			parseEmails(monitor);

		} catch (MessagingException e)
		{
			Logging.severe("Could not get messages", e);
		}

		setChanged();
		notifyObservers();
	}

	public void gatherMail()
	{
		gatherMail(null);
	}
}
