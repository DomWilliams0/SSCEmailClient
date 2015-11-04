package dxw405.gui;

import dxw405.Email;
import dxw405.Mailbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class EmailListView extends JPanel implements Observer
{
	private Mailbox mailbox;
	private JList<Email> emailList;
	private JScrollPane scrollPane;

	public EmailListView(MouseListener mouseListener, Mailbox mailbox)
	{
		this.mailbox = mailbox;

		emailList = new JList<>();
		emailList.setModel(new EmailListModel());
		emailList.setCellRenderer(new EmailListRenderer());
		emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		emailList.addMouseListener(mouseListener);

		scrollPane = new JScrollPane(emailList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(createSearchBox(), BorderLayout.NORTH);
	}

	private JPanel createSearchBox()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		// text field
		JTextField textField = new JTextField()
		{
			private static final String placeholder = "Search";

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				if (getText().length() == 0)
				{
					int h = getHeight();
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					Insets ins = getInsets();
					FontMetrics fm = g.getFontMetrics();
					int c0 = getBackground().getRGB();
					int c1 = getForeground().getRGB();
					int m = 0xfefefefe;
					int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
					g.setColor(new Color(c2, true));
					g.drawString(placeholder, ins.left, h / 2 + fm.getAscent() / 2 - 2);
				}
			}
		};

		// enter: search
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				mailbox.search(textField.getText());
			}
		});


		// escape: clear
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textField.setText("");
				mailbox.search("");
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.insets.set(5, 5, 5, 5);

		panel.add(textField, c);

		return panel;
	}

	@Override
	public void update(Observable o, Object arg)
	{
		JPanel scrollingPanel = new JPanel();
		scrollingPanel.setLayout(new BoxLayout(scrollingPanel, BoxLayout.Y_AXIS));
		((EmailListModel) emailList.getModel()).reset(mailbox.getEmails());
	}

	public void updateElement(int index)
	{
		((EmailListModel) emailList.getModel()).updateElement(index);

	}

	class EmailListModel extends DefaultListModel<Email>
	{

		private List<Email> emails;

		public EmailListModel()
		{
			emails = new ArrayList<>();
		}

		public void reset(List<Email> newEmails)
		{
			emails.clear();
			emails.addAll(newEmails);

			Collections.sort(emails, (e1, e2) -> {
				// unread first
				int unreadCompare = Boolean.compare(e1.isRead(), e2.isRead());

				if (unreadCompare != 0)
					return unreadCompare;

				// recent second
				int recentCompare = Boolean.compare(e1.isRecent(), e2.isRecent());
				if (recentCompare != 0)
					return recentCompare;

				// otherwise by date
				return -e1.getDateTime().compareTo(e2.getDateTime());
			});

			fireContentsChanged(this, 0, newEmails.size());
		}

		@Override
		public int getSize()
		{
			return emails.size();
		}

		@Override
		public Email getElementAt(int index)
		{
			return emails.get(index);
		}


		public void updateElement(int index)
		{
			fireContentsChanged(this, index, index);
		}
	}

	class EmailListRenderer extends JPanel implements ListCellRenderer<Email>
	{
		private static final int MAX_STRING_LENGTH = 40;
		private final Color defaultBG;
		private final Color selectedBG;

		private JLabel subject;
		private JLabel date;
		private JLabel from;
		private JLabel meta;


		public EmailListRenderer()
		{
			setLayout(new FlowLayout(FlowLayout.LEFT));

			subject = new JLabel();
			subject.setAlignmentX(LEFT_ALIGNMENT);
			subject.setFont(subject.getFont().deriveFont(Font.BOLD, 15f));

			from = new JLabel();
			meta = new JLabel();

			date = new JLabel();
			date.setAlignmentX(RIGHT_ALIGNMENT);

			JPanel container = new JPanel(new GridBagLayout());
			container.setOpaque(false);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = GridBagConstraints.RELATIVE;
			c.anchor = GridBagConstraints.WEST;

			container.add(subject, c);
			container.add(from, c);
			container.add(date, c);
			container.add(meta, c);
			add(container);

			defaultBG = getBackground();
			selectedBG = new Color(154, 198, 255);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Email> list, Email value, int index, boolean isSelected, boolean cellHasFocus)
		{
			// highlighted selection
			setBackground(isSelected ? selectedBG : defaultBG);

			// wrap and truncate
			subject.setText(display(value.getSubject(), !value.isRead()));
			date.setText(display(value.getDate()));
			from.setText(display(value.getFrom()));

			if (value.isRecent())
				meta.setText("[RECENT]");
			else
				meta.setText("");

			return this;
		}

		private String display(String s, boolean bold)
		{
			if (s == null)
				s = "";

			if (s.length() > MAX_STRING_LENGTH)
				s = s.substring(0, MAX_STRING_LENGTH - 3) + "...";

			double width = scrollPane.getWidth() * 0.6;
			String fontWeight = bold ? "bold" : "normal";

			return "<html><body style='width: " + width + "px; font-weight:" + fontWeight + "'>" + s + "</body></html>";
		}

		private String display(String s)
		{
			return display(s, false);
		}
	}
}
