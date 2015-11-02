package dxw405.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reused from https://github.com/DomWilliams0/SSCUniDatabase
 */
public class Config
{
	private Properties properties;

	public Config()
	{
		this.properties = new Properties();
	}

	public boolean load(String filePath)
	{
		FileInputStream inStream = Utils.readFile(new File(filePath));
		if (inStream == null)
			return false;

		try
		{
			properties.load(inStream);
			Utils.closeStream(inStream);
			return true;
		} catch (IOException e)
		{
			Logging.severe("Could not load config file (" + filePath + "): " + e);
			return false;
		}
	}

	public String get(String key)
	{
		String value = properties.getProperty(key);
		if (value == null)
			throw new IllegalArgumentException("Invalid config key: " + key);

		return value;
	}

	public int getInt(String key)
	{
		String value = get(key);

		try
		{
			return Integer.parseInt(value);
		} catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Cannot get integer from config (" + key + "=" + value + ")");
		}
	}

	public boolean getBoolean(String key)
	{
		String value = get(key);

		switch (value)
		{
			case "true":
				return true;
			case "false":
				return false;
			default:
				throw new IllegalArgumentException("Cannot get boolean from config (" + key + "=" + value + ")");
		}
	}
}
