package dxw405.gui;

import dxw405.Email;
import dxw405.Mailbox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EmailListView extends JPanel implements Observer
{
	private Mailbox mailbox;
	private JList<Email> emailList;

	public EmailListView(Mailbox mailbox)
	{
		this.mailbox = mailbox;

		emailList = new JList<>();
		emailList.setModel(new EmailListModel());
		emailList.setCellRenderer(new EmailListRenderer());
		emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(emailList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg)
	{
		JPanel scrollingPanel = new JPanel();
		scrollingPanel.setLayout(new BoxLayout(scrollingPanel, BoxLayout.Y_AXIS));
		((EmailListModel) emailList.getModel()).reset(mailbox.getEmails());
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


	}

	class EmailListRenderer extends JPanel implements ListCellRenderer<Email>
	{
		private final Color defaultBG;
		private final Color selectedBG;

		private JLabel subject;
		private JLabel date;
		private JLabel from;


		public EmailListRenderer()
		{
			setLayout(new FlowLayout(FlowLayout.LEFT));

			subject = new JLabel();
			subject.setAlignmentX(LEFT_ALIGNMENT);
			subject.setFont(subject.getFont().deriveFont(Font.BOLD, 18f));

			from = new JLabel();

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
			add(container);

			defaultBG = getBackground();
			selectedBG = new Color(154, 198, 255);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Email> list, Email value, int index, boolean isSelected, boolean cellHasFocus)
		{
			setBackground(isSelected ? selectedBG : defaultBG);

			subject.setText(value.getSubject());
			date.setText(value.getDate());
			from.setText(value.getFrom());
			return this;
		}
	}
}
