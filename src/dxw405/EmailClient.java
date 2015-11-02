package dxw405;

import dxw405.util.Config;
import dxw405.util.Logging;
import dxw405.util.Utils;

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
		config = new Config("res/config.properties");
		if (config.isInvalid())
			halt("Could not load config");

		// init logger
		Level level = Utils.stringToLevel(config.get("log-level"), Level.INFO);
		Logging.initiate("SSCEmailClient", level);

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
		Config creds = new Config(config.get("email-account"));
		if (creds.isInvalid())
			halt("Could not load account credentials");

		String user = creds.get("address");
		String password = creds.get("password");
		String host = creds.get("incoming-server");
		int port = creds.getInt("incoming-port");

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
