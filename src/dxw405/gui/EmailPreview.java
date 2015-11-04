package dxw405.gui;

import dxw405.Email;
import dxw405.gui.attachments.AttachmentSelection;

import javax.swing.*;
import java.awt.*;

public class EmailPreview extends JPanel
{
	private JLabel from;
	private JLabel to;
	private JLabel sent;
	private JLabel subject;
	private JTextPane content;
	private AttachmentSelection attachments;

	public EmailPreview()
	{
		setLayout(new BorderLayout());

		// top: header info
		JPanel headerContainerContainer = new JPanel(new BorderLayout());

		JPanel headerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel header = new JPanel(new GridBagLayout());

		from = new JLabel();
		to = new JLabel();
		sent = new JLabel();
		subject = new JLabel();
		subject.setFont(subject.getFont().deriveFont(Font.BOLD, 16f));
		attachments = new AttachmentSelection(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.top = c.insets.bottom = 2;
		header.add(from, c);
		header.add(to, c);
		header.add(sent, c);
		header.add(subject, c);
		headerContainer.add(header);

		headerContainerContainer.add(headerContainer, BorderLayout.CENTER);
		headerContainerContainer.add(attachments, BorderLayout.SOUTH);

		// centre: content
		content = new JTextPane();
		content.setEditable(false);
		JScrollPane contentScrollPane = new JScrollPane(content);


		add(headerContainerContainer, BorderLayout.NORTH);
		add(contentScrollPane, BorderLayout.CENTER);
	}

	public void view(Email selected)
	{
		if (selected == null)
			return;

		from.setText(makeTitle("From", selected.getFrom()));
		to.setText(makeTitle("To", selected.getTo()));
		sent.setText(makeTitle("Sent", selected.getDate()));
		subject.setText(selected.getSubject());

		Email.EmailContent emailContent = selected.getContent();
		attachments.setEmail(selected);

		content.setText(emailContent.getContent());
		content.setCaretPosition(0);

	}

	private String makeTitle(String label, String field)
	{
		return "<html><b>" + label + ": </b>" + field + "</html>";
	}
}
