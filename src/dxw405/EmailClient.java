package dxw405;

import dxw405.util.Config;
import dxw405.util.Logging;
import dxw405.util.Utils;

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

		String host = creds.get("incoming-server");
		int port = creds.getInt("incoming-port");
		String user = creds.get("address");
		String password = creds.get("password");

		// connect to the mailbox
		Mailbox mailbox = new Mailbox();
		if (!mailbox.connect(host, port, user, password))
			halt("Could not connect to the mailbox");

		// close mailbox
		mailbox.close();
	}

}
