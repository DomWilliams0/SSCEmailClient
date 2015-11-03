package dxw405.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmailComposePanel extends JPanel
{
	private static final Field[] FIELDS = {
			new Field("To", true, ""),
			new Field("Cc", true, ""),
			new Field("Bcc", true, ""),
			new Field("Subject", false, "Re: ")
	};

	private static final int BORDER_THICKNESS = 5;

	public EmailComposePanel()
	{
		setLayout(new BorderLayout());

		add(createHeader(), BorderLayout.NORTH);
		add(createBody(), BorderLayout.CENTER);
		add(createControlBar(), BorderLayout.SOUTH);
	}

	private JPanel createHeader()
	{
		JPanel container = new JPanel(new BorderLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.insets.top = c.insets.left = c.insets.bottom = c.insets.right = BORDER_THICKNESS;
		c.weighty = 1;
		c.weightx = 2;

		// labels
		JPanel labelPanel = new JPanel(new GridBagLayout());
		for (Field field : FIELDS)
		{
			JLabel label = new JLabel(field.name, SwingConstants.RIGHT);

			if (field.isAddress)
				label.setToolTipText("Separate addresses with a semi-colon (;)");
			labelPanel.add(label, c);
		}

		// field
		JPanel fieldPanel = new JPanel(new GridBagLayout());

		for (Field FIELD : FIELDS)
			fieldPanel.add(new JTextField(FIELD.defaultValue), c);

		container.add(labelPanel, BorderLayout.WEST);
		container.add(fieldPanel, BorderLayout.CENTER);
		return container;
	}

	private JPanel createBody()
	{
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(0, BORDER_THICKNESS, 0, BORDER_THICKNESS));

		bodyPanel.add(new JScrollPane(new JTextArea("")), BorderLayout.CENTER);

		return bodyPanel;
	}

	private JPanel createControlBar()
	{
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton send = new JButton("Send");
		send.addActionListener(new SendButtonListener());
		controlPanel.add(send);

		return controlPanel;
	}


	private static class Field
	{
		public String name;
		public boolean isAddress;
		public String defaultValue;

		public Field(String name, boolean isAddress, String defaultValue)
		{
			this.name = name;
			this.isAddress = isAddress;
			this.defaultValue = defaultValue;
		}
	}


	private class SendButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showMessageDialog(EmailComposePanel.this, "Send!");
		}
	}
}
