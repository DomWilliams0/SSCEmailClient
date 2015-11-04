package dxw405.gui;

import dxw405.Field;
import dxw405.PreparedEmail;
import dxw405.util.JPanelMouseAdapter;
import dxw405.util.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class EmailComposePanel extends JPanel
{
	public static final String SEND_COMMAND = "SEND";

	private static final int BORDER_THICKNESS = 5;
	private TreeMap<String, JTextComponent> inputs;

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
		panel.add(new AttachmentSelection(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createBody()
	{
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(0, BORDER_THICKNESS, 0, BORDER_THICKNESS));

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

class AttachmentSelection extends JPanelMouseAdapter implements ActionListener
{
	private Map<String, JPanel> itemCache;
	private Map<String, JPanel> itemCacheBuffer;

	private LinkedHashMap<String, File> attachments;
	private JPanel attachmentPanel;
	private JButton addButton;

	private AttachmentPopup rightClickPopup;

	public AttachmentSelection()
	{
		itemCache = new TreeMap<>();
		itemCacheBuffer = new TreeMap<>();
		attachments = new LinkedHashMap<>();
		rightClickPopup = new AttachmentPopup();

		setLayout(new BorderLayout());

		attachmentPanel = new JPanel();
		attachmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.addButton = new JButton("Add Attachment");
		this.addButton.addActionListener(this);
		attachmentPanel.add(this.addButton);

		JScrollPane scrollPane = new JScrollPane(attachmentPanel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		onClick(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		onClick(e);
	}

	private void onClick(MouseEvent e)
	{
		if (!e.isPopupTrigger())
			return;

		JPanel selected = (JPanel) e.getSource();
		Component component = selected.getComponent(0);
		if (!(component instanceof JLabel))
			return;

		rightClickPopup.display(component);


	}

	private class AttachmentPopup extends JPopupMenu
	{
		private JLabel component;

		public AttachmentPopup()
		{
			add(new JMenuItem(new AbstractAction("Remove")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachments.remove(component.getText());
					updateAttachments();
				}
			}));

			add(new JMenuItem(new AbstractAction("Remove All")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachments.clear();
					updateAttachments();
				}
			}));
		}

		public void display(Component component)
		{
			this.component = (JLabel) component;
			show(component, component.getX(), component.getY() + component.getHeight());
		}
	}

	private void updateAttachments()
	{
		attachmentPanel.removeAll();
		attachmentPanel.add(addButton);

		itemCacheBuffer.clear();

		for (Map.Entry<String, File> entry : attachments.entrySet())
		{
			String key = entry.getValue().getAbsolutePath();
			JPanel attachment = itemCache.get(key);
			if (attachment == null)
			{
				attachment = createAttachment(entry);
				itemCache.put(key, attachment);
			}

			attachmentPanel.add(attachment);
			itemCacheBuffer.put(key, attachment);
		}

		itemCache.clear();
		itemCache.putAll(itemCacheBuffer);

		repaint();
	}

	private JPanel createAttachment(Map.Entry<String, File> entry)
	{
		JPanel attachment = new JPanel();

		attachment.add(new JLabel(entry.getKey()));

		// hover for info
		attachment.setToolTipText("<html>File name: " + entry + "<br/>File size: " + Utils.readableFileSize(entry.getValue()) + "</html>");

		// right click to remove
		attachment.addMouseListener(this);

		return attachment;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);

		int result = chooser.showOpenDialog(this);
		if (result != JFileChooser.APPROVE_OPTION)
			return;

		File[] chosenFiles = chooser.getSelectedFiles();
		if (chosenFiles == null || chosenFiles.length == 0)
			return;

		for (File file : chosenFiles)
			attachments.put(file.getName(), file);

		updateAttachments();
	}


}
