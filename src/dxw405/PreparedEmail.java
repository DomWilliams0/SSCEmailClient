package dxw405;

import dxw405.util.Logging;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An email that is prepared to be sent
 */
public class PreparedEmail
{
	private Map<Message.RecipientType, List<Address>> recipients;
	private String subject, body;
	private List<String> errors;

	public PreparedEmail()
	{
		this(new HashMap<>(), "", "");
	}

	public PreparedEmail(Map<Message.RecipientType, List<Address>> recipients, String subject, String body)
	{
		this.recipients = recipients;
		this.subject = subject;
		this.body = body;
		this.errors = new ArrayList<>();
	}

	public List<Address> getRecipients(Message.RecipientType type)
	{
		List<Address> addresses = recipients.get(type);
		if (addresses == null)
		{
			addresses = new ArrayList<>();
			recipients.put(type, addresses);
		}
		return addresses;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public List<String> getErrors()
	{
		return errors;
	}

	public void setRecipients(Field field, String addresses)
	{
		recipients.put(field.getRecipientType(), parseEmails(field, addresses, field.isMandatory()));
	}

	private List<Address> parseEmails(Field field, String input, boolean mandatory)
	{
		List<Address> addresses = new ArrayList<>();

		// remove blanks
		String[] splitUnprocessed = input.split(";");
		List<String> split = new ArrayList<>();
		for (String s : splitUnprocessed)
			if (!s.isEmpty())
				split.add(s);

		if (split.isEmpty())
		{
			if (mandatory)
				errors.add("At least 1 '" + field + "' recipient is needed");
			return addresses;
		}

		for (String s : split)
		{
			String email = s.trim();
			if (email.isEmpty())
				continue;

			try
			{
				addresses.add(new InternetAddress(email));
			} catch (AddressException e)
			{
				Logging.warning("Invalid address", e);
				errors.add("Invalid address: " + email);
			}
		}

		return addresses;
	}

	public boolean hasErrors()
	{
		return !errors.isEmpty();
	}

	@Override
	public String toString()
	{
		return "PreparedEmail{" +
				"recipients=" + recipients +
				", subject='" + subject + '\'' +
				", body='" + body + '\'' +
				'}';
	}
}
