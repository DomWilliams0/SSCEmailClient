package dxw405.gui.panels;

import dxw405.Email;
import dxw405.Mailbox;
import dxw405.gui.EmailPopup;
import dxw405.gui.TextFieldPlaceholder;
import dxw405.util.JPanelMouseAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class EmailListView extends JPanelMouseAdapter implements Observer
{
	private Mailbox mailbox;
	private JList<Email> emailList;
	private JScrollPane scrollPane;
	private String lastSearch;

	private EmailPopup emailPopup;

	public EmailListView(MouseListener emailSelectListener, Mailbox mailbox)
	{
		this.mailbox = mailbox;
		this.lastSearch = null;
		this.emailPopup = new EmailPopup(mailbox);

		emailList = new JList<>();
		emailList.setModel(new EmailListModel());
		emailList.setCellRenderer(new EmailListRenderer());
		emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		emailList.addMouseListener(emailSelectListener);
		emailList.addMouseListener(this);

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
		JTextField textField = new TextFieldPlaceholder("Search");

		// enter: search
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String searchTerm = textField.getText();
				if (!searchTerm.equalsIgnoreCase(lastSearch))
				{
					mailbox.search(searchTerm);
					lastSearch = searchTerm;
				}
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
		EmailListModel model = (EmailListModel) emailList.getModel();

		// single email
		if (arg != null && arg instanceof Email)
		{
			model.reset((Email) arg);
		}

		// reset all
		else
		{
			model.reset(mailbox.getEmails());
		}
	}

	public void updateElement(int index)
	{
		((EmailListModel) emailList.getModel()).updateElement(index);

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		onRightClick(e);
	}

	private void onRightClick(MouseEvent e)
	{
		if (!e.isPopupTrigger())
			return;

		int selectedIndex = emailList.locationToIndex(e.getPoint());
		if (selectedIndex < 0)
			return;

		emailList.setSelectedIndex(selectedIndex);
		Email email = emailList.getSelectedValue();
		if (email == null)
			return;

		emailPopup.display(e, email);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		onRightClick(e);
	}

	class EmailListModel extends DefaultListModel<Email>
	{

		private List<Email> emails;

		public EmailListModel()
		{
			emails = new ArrayList<>();
		}

		public void reset(Email email)
		{
			int index = emails.indexOf(email);
			if (index >= 0)
				fireContentsChanged(this, index, index);
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
