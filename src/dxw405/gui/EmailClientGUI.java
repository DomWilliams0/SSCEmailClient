package dxw405.gui;

import dxw405.EmailClient;
import dxw405.Mailbox;
import dxw405.util.Logging;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EmailClientGUI
{
	private Mailbox mailbox;
	private EmailClient emailClient;

	private JFrame frame;

	public EmailClientGUI(EmailClient emailClient)
	{
		this.emailClient = emailClient;

		// show gui
		initGUI();

		// get mailbox
		mailbox = emailClient.createMailbox();
		if (mailbox == null)
			EmailClient.halt(null);
	}

	private void initGUI()
	{
		// create
		frame = createFrame("Email Client");

		// populate
		frame.add(new JLabel("Emails!"));

		// show
		show();
	}

	private JFrame createFrame(String title)
	{

		if (emailClient.getConfig().getBoolean("os-skin"))
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
			{
				Logging.warning("Could not prettify the UI :(", e);
			}
		}

		JFrame frame = new JFrame(title);
		frame.setSize(emailClient.getConfig().getInt("gui-width"), emailClient.getConfig().getInt("gui-height"));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (mailbox != null)
					mailbox.close();
			}
		});


		return frame;
	}

	private void show()
	{
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
