package dxw405;

import javax.mail.Message;

public enum Field
{
	TO("To", "", true, true, true, Message.RecipientType.TO),
	CC("Cc", "", true, true, false, Message.RecipientType.CC),
	BCC("Bcc", "", true, true, false, Message.RecipientType.BCC),
	SUBJECT("Subject", "Re: ", false, true, true, null),
	BODY("Body", "", false, false, true, null);

	private String name;
	private String defaultValue;
	private boolean address, inHeader, mandatory;
	private Message.RecipientType recipientType;

	Field(String name, String defaultValue,
		  boolean address, boolean inHeader, boolean mandatory, Message.RecipientType recipientType)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.address = address;
		this.inHeader = inHeader;
		this.mandatory = mandatory;
		this.recipientType = recipientType;
	}


	public String getName()
	{
		return name;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public boolean isAddress()
	{
		return address;
	}

	public boolean isInHeader()
	{
		return inHeader;
	}

	public boolean isMandatory()
	{
		return mandatory;
	}

	public Message.RecipientType getRecipientType()
	{
		return recipientType;
	}
}