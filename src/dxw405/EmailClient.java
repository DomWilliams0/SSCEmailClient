package dxw405;

import dxw405.util.Config;
import dxw405.util.Logging;

import java.util.logging.Level;

public class EmailClient
{
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
		Logging.initiate("SSCEmailClient", Level.INFO);

		Logging.fine("Config and logger initiated successfully");
	}

	private void halt(String errorMessage)
	{
		Logging.severe(errorMessage);
		System.exit(2);
	}

	private void run()
	{
		// todo run
	}

}
