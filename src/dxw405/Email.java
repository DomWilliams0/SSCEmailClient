package dxw405;

import dxw405.util.Utils;

import javax.mail.Message;
import java.util.Date;

/**
 * String representation of an email message
 */
public class Email
{
	private String subject;
	private String from;
	private String to;
	private String content;

	private Date date;
	private String dateString;

	private boolean read;
	private boolean recent;
	private Message mailboxReference;

	public Email(String subject, String from, String to, String content, Date date, boolean read, boolean recent, Message mailboxReference)
	{
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.content = content;
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
		return content;
	}

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
