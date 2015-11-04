package dxw405.gui;

import dxw405.Field;
import dxw405.PreparedEmail;
import dxw405.gui.attachments.AttachmentSelection;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.TreeMap;

public class EmailComposePanel extends JPanel
{
	public static final String SEND_COMMAND = "SEND";

	private static final int BORDER_THICKNESS = 5;
	private TreeMap<String, JTextComponent> inputs;
	private AttachmentSelection attachments;

	public EmailComposePanel(ActionListener sendButtonListener)
	{
		inputs = new TreeMap<>();

		setLayout(new BorderLayout());

		add(createHeader(), BorderLayout.NORTH);
		add(createBody(), BorderLayout.CENTER);
		add(createControlBar(sendButtonListener), BorderLayout.SOUTH);
	}

	private JPanel createHeader()
	{
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createEmailFieldHeaders(), BorderLayout.CENTER);

		attachments = new AttachmentSelection(true);
		panel.add(attachments, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createBody()
	{
		JPanel bodyPanel = new JPanel(new BorderLayout());

		JTextArea textArea = new JTextArea("");
		inputs.put(Field.BODY.getName(), textArea);

		bodyPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		return bodyPanel;
	}

	private JPanel createControlBar(ActionListener sendButtonListener)
	{
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton send = new JButton("Send");
		send.setActionCommand(SEND_COMMAND);
		send.addActionListener(sendButtonListener);
		controlPanel.add(send);

		return controlPanel;
	}

	private JPanel createEmailFieldHeaders()
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
		for (Field field : Field.values())
		{
			if (!field.isInHeader())
				continue;

			JLabel label = new JLabel(field.getName(), SwingConstants.RIGHT);

			if (field.isAddress())
				label.setToolTipText("Separate addresses with a semi-colon (;)");
			labelPanel.add(label, c);
		}

		// fields
		JPanel fieldPanel = new JPanel(new GridBagLayout());

		for (Field field : Field.values())
		{
			if (!field.isInHeader())
				continue;

			JTextField textField = new JTextField(field.getDefaultValue());
			textField.moveCaretPosition(field.getDefaultValue().length());
			inputs.put(field.getName(), textField);

			fieldPanel.add(textField, c);
		}

		container.add(labelPanel, BorderLayout.WEST);
		container.add(fieldPanel, BorderLayout.CENTER);
		return container;
	}

	public PreparedEmail prepareEmail()
	{
		PreparedEmail preparedEmail = new PreparedEmail();

		// populate fields
		for (Field field : Field.values())
			if (field.isAddress())
				preparedEmail.setRecipients(field, getField(field));

		preparedEmail.setSubject(getField(Field.SUBJECT));
		preparedEmail.setBody(getField(Field.BODY));
		preparedEmail.setAttachments(attachments.getAttachments());

		// show error dialog if necessary
		if (preparedEmail.hasErrors())
		{
			String delimiter = "\n -";
			String errorMessage = "Please fix the following issue(s):" + delimiter + String.join(delimiter, preparedEmail.getErrors());
			JOptionPane.showMessageDialog(EmailComposePanel.this, errorMessage, "Uh oh", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return preparedEmail;
	}

	private String getField(Field field)
	{
		JTextComponent textComponent = inputs.get(field.getName());
		if (textComponent == null)
			return null;

		return textComponent.getText();
	}
}

