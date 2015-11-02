package dxw405;

import dxw405.gui.EmailClientGUI;
import dxw405.util.Config;
import dxw405.util.Logging;
import dxw405.util.Utils;

import java.util.logging.Level;

/**
 * Core class that sits at the centre of the application
 */
public class EmailClient
{
	private Config config;

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

	/**
	 * Logs the given message as severe, before killing the program
	 *
	 * @param errorMessage The error message. Can be null
	 */
	public static void halt(String errorMessage)
	{
		if (errorMessage != null && !errorMessage.isEmpty())
			Logging.severe(errorMessage);

		Logging.severe("Halting...");
		System.exit(2);
	}

	public static void main(String[] args)
	{
		EmailClient emailClient = new EmailClient();
		new EmailClientGUI(emailClient);
	}

	/**
	 * Creates the mailbox from the config
	 *
	 * @return The mailbox, or null if the operation failed
	 */
	public Mailbox createMailbox()
	{
		// load credentials
		Config creds = new Config(config.get("email-account"));
		if (creds.isInvalid())
		{
			Logging.severe("Could not load account credentials");
			return null;
		}

		String host = creds.get("incoming-server");
		int port = creds.getInt("incoming-port");
		String user = creds.get("address");
		String password = creds.get("password");

		// connect to the mailbox
		Mailbox mailbox = new Mailbox();
		if (!mailbox.connect(host, port, user, password))
		{
			Logging.severe("Could not connect to the mailbox");
			return null;
		}

		return mailbox;
	}

	public Config getConfig()
	{
		return config;
	}
}
