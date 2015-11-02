package dxw405;

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

	public Email(String subject, String from, String to, String content, Date date)
	{
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.content = content;
		this.date = date;
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

	public Date getDate()
	{
		return date;
	}
}
