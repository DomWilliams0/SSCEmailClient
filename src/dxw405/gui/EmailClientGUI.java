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
		this.mailbox = emailClient.createBlankMailbox();

		// show gui
		initGUI();

		// get mailbox
		new MailGatherer(mailbox, emailClient).run(frame);
	}

	private void initGUI()
	{
		// create
		frame = createFrame("Email Client");

		// populate
		//		frame.setJMenuBar(createMenuBar());
		frame.add(new ControllerPanel(mailbox));

		// show
		show();
	}

	/**
	 * Creates the window of the size specified in the config and the given title
	 *
	 * @param title The window title
	 * @return The window
	 */
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
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	//	/**
	//	 * @return The menu bar, with various buttons such as exit, or compose mail
	//	 */
	//	private JMenuBar createMenuBar()
	//	{
	//		JMenuBar bar = new JMenuBar();
	//
	//
	//		// compose
	//		addMenuItem(bar, "Compose", 'c', new AbstractAction()
	//		{
	//			@Override
	//			public void actionPerformed(ActionEvent e)
	//			{
	//				JOptionPane.showMessageDialog(EmailClientGUI.this.getFrame(), "Compose mail!");
	//			}
	//		});
	//
	//		// search
	//		addMenuItem(bar, "Search", 's', new AbstractAction()
	//		{
	//			@Override
	//			public void actionPerformed(ActionEvent e)
	//			{
	//				JOptionPane.showMessageDialog(EmailClientGUI.this.getFrame(), "Search mail!");
	//			}
	//		});
	//
	//		// exit
	//		addMenuItem(bar, "Exit", 'x', new AbstractAction()
	//		{
	//			@Override
	//			public void actionPerformed(ActionEvent e)
	//			{
	//				EmailClientGUI.this.close();
	//			}
	//		});
	//
	//		return bar;
	//	}
	//
	//	private void addMenuItem(JMenuBar bar, String text, char mneumonic, Action action)
	//	{
	//		JButton item = new JButton();
	//		item.setMnemonic(mneumonic);
	//		action.putValue(Action.NAME, text);
	//		item.setAction(action);
	//		bar.add(item);
	//	}
	//
	//	public JFrame getFrame()
	//	{
	//		return frame;
	//	}

	public void close()
	{
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
