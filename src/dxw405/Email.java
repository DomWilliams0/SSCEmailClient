package dxw405;

import dxw405.util.Logging;
import dxw405.util.Utils;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * String representation of an email message
 */
public class Email
{
	private String subject;
	private String from;
	private String to;
	private EmailContent content;

	private Date date;
	private String dateString;

	private boolean read;
	private boolean recent;
	private Message mailboxReference;

	public Email(String subject, String from, String to, Date date, boolean read, boolean recent, Message mailboxReference)
	{
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.content = new EmailContent(mailboxReference);

		this.date = date;
		this.dateString = Utils.DATE_FORMATTER.format(date);

		this.recent = recent;
		this.read = read;
		this.mailboxReference = mailboxReference;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getFrom()
	{
		return from;
	}

	public String getTo()
	{
		return to;
	}

	public String getContent()
	{
		return content.getContent();
	}

	public List<String> getAttachmentNames() {return content.getAttachmentNames();}

	public Date getDateTime()
	{
		return date;
	}

	public String getDate()
	{
		return dateString;
	}

	public boolean isRead()
	{
		return read;
	}

	public boolean isRecent()
	{
		return recent;
	}

	public void setAsRead()
	{
		read = true;
	}

	public Message getMailboxReference()
	{
		return mailboxReference;
	}
}

class EmailContent
{
	private boolean loaded;
	private String content;
	private LinkedHashMap<String, Integer> attachmentNames;
	private Message message;

	public EmailContent(Message message)
	{
		this.message = message;
		this.loaded = false;
		this.content = null;
		this.attachmentNames = new LinkedHashMap<>();
	}

	public String getContent()
	{
		ensureLoaded();
		return content;
	}

	private void ensureLoaded()
	{
		if (!loaded)
			loaded = Mailbox.readContent(message, this);
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public List<String> getAttachmentNames()
	{
		ensureLoaded();

		List<String> names = new ArrayList<>();
		names.addAll(attachmentNames.keySet());
		return names;
	}

	public void addAttachment(String name, int bodyPartIndex)
	{
		attachmentNames.put(name, bodyPartIndex);
	}

	public InputStream getAttachmentInputStream(String name)
	{
		Integer bodyPartIndex = attachmentNames.get(name);
		if (bodyPartIndex == null)
			return null;

		try
		{
			Multipart multipart = (Multipart) message.getContent();
			BodyPart bodyPart = multipart.getBodyPart(bodyPartIndex);
			return bodyPart.getInputStream();


		} catch (IOException | MessagingException | IndexOutOfBoundsException e)
		{
			Logging.severe("Could not get attachment stream for '" + name + "'", e);
			return null;
		}
	}
}
