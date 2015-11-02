package dxw405;

import dxw405.util.Config;
import dxw405.util.Logging;

import javax.mail.*;
import java.util.Properties;
import java.util.logging.Level;

public class EmailClient
{
	private Config config;

	public static void main(String[] args)
	{
		new EmailClient().run();
	}

	public EmailClient()
	{
		// init config
		Config config = new Config("res/config.properties");
		if (config.isInvalid())
			halt("Could not load config");

		// init logger from config level todo
		Logging.initiate("SSCEmailClient", Level.FINER);

		Logging.fine("Config and logger initiated successfully");
	}

	private void halt(String errorMessage)
	{
		Logging.severe(errorMessage);
		Logging.severe("Halting...");
		System.exit(2);
	}

	private void run()
	{

		// load credentials
		Config creds = new Config("res/account.properties");
		if (creds.isInvalid())
			halt("Could not load account credentials");

		String user = creds.get("email-address");
		String password = creds.get("email-password");
		String host = creds.get("email-incoming-server");
		int port = creds.getInt("email-incoming-port");

		// imap properties
		Properties imapProperties = System.getProperties();
		imapProperties.setProperty("mail.store.protocol", "imaps");
		imapProperties.setProperty("mail.user", user);
		imapProperties.setProperty("mail.password", password);

		Session session = Session.getDefaultInstance(imapProperties);
		Store store = null;
		Folder folder = null;
		try
		{
			// connect
			Logging.info("Attempting to connect to " + host + ":" + port + " with email'" + user + "'");
			store = session.getStore("imaps");
			store.connect(host, port, user, password);
			Logging.info("Successfully connected to mailbox");

			// test: list all emails in inbox
			folder = store.getFolder("inbox");
			if (!folder.isOpen())
				folder.open(Folder.READ_ONLY);

			Message[] messages = folder.getMessages();
			for (Message message : messages)
				System.out.println(message.getSubject());

		} catch (javax.mail.MessagingException e)
		{
			Logging.severe(e, "Could not connect to mailbox");
		} finally
		{
			try
			{
				if (folder != null && folder.isOpen())
					folder.close(true);

				if (store != null) store.close();

			} catch (MessagingException e)
			{
				Logging.severe(e, "Could not close folder/store");
			}
		}

	}

}
