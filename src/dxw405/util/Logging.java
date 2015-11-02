package dxw405.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Logging
{
	private static Logging INSTANCE;

	static
	{
		// log formatting
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS [%4$7s] %5$s%6$s%n");
		System.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");

		INSTANCE = new Logging();
	}

	private static final Level STACK_TRACE_LEVEL = Level.FINER;

	private Logger logger;

	private Logging()
	{
		// default logger
		logger = Logger.getGlobal();
	}

	public static void initiate(String name, Level logLevel)
	{
		if (INSTANCE == null)
			INSTANCE = new Logging();

		INSTANCE.logger = Logger.getLogger(name);

		INSTANCE.logger.setLevel(logLevel);

		// log level publishing
		INSTANCE.logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		INSTANCE.logger.addHandler(handler);
	}

	public static void stackTrace(Exception e)
	{
		String wrapper = "------------------";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		INSTANCE.logger.log(STACK_TRACE_LEVEL, "\n" + wrapper + "\n" + sw.toString() + wrapper);
	}

	public static void severe(String msg) {INSTANCE.logger.severe(msg);}

	public static void warning(String msg) {INSTANCE.logger.warning(msg);}

	public static void info(String msg) {INSTANCE.logger.info(msg);}

	public static void fine(String msg) {INSTANCE.logger.fine(msg);}

	public static void finer(String msg) {INSTANCE.logger.finer(msg);}

	public static void finest(String msg) {INSTANCE.logger.finest(msg);}
}
