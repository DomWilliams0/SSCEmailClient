package dxw405.gui.panels;

import dxw405.Email;
import dxw405.gui.attachments.AttachmentSelection;

import javax.swing.*;
import java.awt.*;

public class EmailPreview extends JPanel
{
	private static final String PREVIEW_PANEL = "P";
	private static final String NULL_PANEL = "N";

	private JLabel from;
	private JLabel to;
	private JLabel sent;
	private JLabel flags;
	private JLabel subject;
	private JTextPane content;
	private AttachmentSelection attachments;
	private CardLayout switcher;

	public EmailPreview()
	{
		// switches between blank and populated
		switcher = new CardLayout();
		setLayout(switcher);

		JPanel emailPreview = createPreviewPanel();
		JPanel nullPanel = createNullPanel();

		add(emailPreview, PREVIEW_PANEL);
		add(nullPanel, NULL_PANEL);

		view(null);
	}

	private JPanel createNullPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Select an email to preview", SwingConstants.CENTER), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createPreviewPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = createHeaderPanel();
		JScrollPane contentPanel = createContentPanel();

		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(contentPanel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createHeaderPanel()
	{
		JPanel fullHeaderPanel = new JPanel(new BorderLayout());

		JPanel infoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel infoPanel = new JPanel(new GridBagLayout());

		from = new JLabel();
		to = new JLabel();
		sent = new JLabel();
		flags = new JLabel();

		subject = new JLabel();
		subject.setFont(subject.getFont().deriveFont(Font.BOLD, 16f));

		attachments = new AttachmentSelection(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.top = c.insets.bottom = 2;
		infoPanel.add(from, c);
		infoPanel.add(to, c);
		infoPanel.add(sent, c);
		infoPanel.add(flags, c);
		infoPanel.add(subject, c);
		infoContainer.add(infoPanel);

		fullHeaderPanel.add(infoContainer, BorderLayout.CENTER);
		fullHeaderPanel.add(attachments, BorderLayout.SOUTH);

		return fullHeaderPanel;
	}

	private JScrollPane createContentPanel()
	{
		content = new JTextPane();
		content.setEditable(false);

		return new JScrollPane(content);
	}

	public void view(Email selected)
	{
		if (selected == null)
		{
			switcher.show(this, NULL_PANEL);
			return;
		}

		switcher.show(this, PREVIEW_PANEL);

		from.setText(makeTitle("From", selected.getFrom()));
		to.setText(makeTitle("To", selected.getTo()));
		sent.setText(makeTitle("Sent", selected.getDate()));
		flags.setText(selected.getFlags());
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
