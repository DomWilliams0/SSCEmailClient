package dxw405.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.Random;
import java.util.logging.Level;

/**
 * Reused from https://github.com/DomWilliams0/SSCUniDatabase
 */
public class Utils
{
	public static final Random RANDOM = new Random();

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);


	private Utils()
	{
	}

	/**
	 * Validates that the given file is not null and exists
	 *
	 * @param file The file to validate
	 * @return If the file is valid
	 */
	public static boolean validateFile(File file)
	{
		// null
		if (file == null)
		{
			Logging.severe("Input file is null");
			return false;
		}

		// doesn't exist
		if (!file.exists())
		{
			Logging.severe("Input file doesn't exist (" + file.getPath() + ")");
			return false;
		}

		return true;
	}


	/**
	 * Opens the given file for reading
	 *
	 * @param file The file to read
	 * @return The file stream, or null if the operation failed
	 */
	public static FileInputStream readFile(File file)
	{
		if (!validateFile(file))
			return null;

		FileInputStream stream;
		try
		{
			stream = new FileInputStream(file);
			return stream;
		} catch (IOException e)
		{
			Logging.severe("Could not load file (" + file.getPath() + "): " + e);
			return null;
		}
	}

	/**
	 * Closes the given stream
	 *
	 * @param stream The stream to close
	 */
	public static void closeStream(InputStream stream)
	{
		try
		{
			stream.close();
		} catch (IOException e)
		{
			Logging.severe("Cannot close stream: " + e);
		}
	}

	/**
	 * Capitalises every word in the given string
	 *
	 * @param sentence The sentence to capitalise
	 * @return The first letter of every word capitalised, the rest lowercase
	 */
	public static String capitalise(String sentence)
	{
		if (sentence == null)
			return null;
		if (sentence.isEmpty())
			return sentence;

		String[] split = sentence.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String s : split)
		{
			int length = s.length();
			switch (length)
			{
				case 0:
					sb.append(" ");
					break;
				case 1:
					sb.append(s.toUpperCase()).append(" ");
					break;
				default:
					sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase()).append(" ");
					break;
			}
		}

		return sb.toString().trim();
	}

	/**
	 * Converts a string to a log level
	 *
	 * @param s String to parse
	 * @return The corresponding log level, or null if invalid
	 */
	public static Level stringToLevel(String s)
	{
		try
		{
			return Level.parse(s.toUpperCase());
		} catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	/**
	 * Converts a String into an Enum
	 *
	 * @param enumClass The enum
	 * @param s         The string to parse
	 * @return The corresponding enum value, or null if none was found
	 */
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String s)
	{
		return parseEnum(enumClass, s, true);
	}

	/**
	 * Converts a String into an Enum
	 *
	 * @param enumClass      The enum
	 * @param s              The string to parse
	 * @param convertToUpper If the given string should be converted to uppercase before comparison
	 * @return The corresponding enum value, or null if none was found
	 */
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String s, boolean convertToUpper)
	{
		EnumSet<E> values = EnumSet.allOf(enumClass);
		String sCompare = convertToUpper ? s.toUpperCase() : s;
		for (E value : values)
			if (value.toString().equals(sCompare))
				return value;
		return null;
	}


}
