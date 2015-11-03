package dxw405.gui;

import dxw405.Email;

import javax.swing.*;
import java.awt.*;

public class EmailPreview extends JPanel
{
	private JLabel from;
	private JLabel to;
	private JLabel subject;
	private JTextPane content;

	public EmailPreview()
	{
		setLayout(new BorderLayout());

		// top: header info
		JPanel headerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel header = new JPanel(new GridBagLayout());

		from = new JLabel();
		to = new JLabel();
		subject = new JLabel();
		subject.setFont(subject.getFont().deriveFont(Font.BOLD, 16f));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.top = c.insets.bottom = 2;
		header.add(from, c);
		header.add(to, c);
		header.add(subject, c);
		headerContainer.add(header);

		// centre: content
		content = new JTextPane();
		content.setEditable(false);
		JScrollPane contentScrollPane = new JScrollPane(content);

		add(headerContainer, BorderLayout.NORTH);
		add(contentScrollPane, BorderLayout.CENTER);
	}

	public void view(Email selected)
	{
		if (selected == null)
			return;

		from.setText("<html><b>From: </b>" + selected.getFrom() + "</html>");
		to.setText("<html><b>To: </b>" + selected.getTo() + "</html>");
		subject.setText(selected.getSubject());
		content.setText(selected.getContent());
		content.setCaretPosition(0);
	}
}
