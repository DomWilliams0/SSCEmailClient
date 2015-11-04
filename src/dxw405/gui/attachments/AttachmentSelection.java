package dxw405.gui.attachments;

import dxw405.util.JPanelMouseAdapter;
import dxw405.util.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

public class AttachmentSelection extends JPanelMouseAdapter implements ActionListener
{
	private Map<String, JPanel> itemCache;
	private Map<String, JPanel> itemCacheBuffer;

	private LinkedHashMap<String, File> attachments;
	private JPanel attachmentPanel;
	private JButton addButton;

	private AttachmentPopup rightClickPopup;
	private boolean editable;

	public AttachmentSelection(boolean editable)
	{
		this.editable = editable;

		itemCache = new TreeMap<>();
		itemCacheBuffer = new TreeMap<>();
		attachments = new LinkedHashMap<>();
		rightClickPopup = new AttachmentPopup(this);

		setLayout(new BorderLayout());

		attachmentPanel = new JPanel();
		attachmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		attachmentPanel.setBorder(new TitledBorder("Attachments"));
		if (editable)
		{
			addButton = new JButton("Add Attachment");
			addButton.addActionListener(this);
			attachmentPanel.add(addButton);
		}

		JScrollPane scrollPane = new JScrollPane(attachmentPanel);
		scrollPane.setBorder(new EmptyBorder(0,0,0,0));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		add(scrollPane, BorderLayout.CENTER);
	}

	public List<File> getAttachments()
	{
		List<File> files = new ArrayList<>();
		files.addAll(attachments.values());
		return files;
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

		refresh();
	}

	public void refresh()
	{
		attachmentPanel.removeAll();
		if (editable)
			attachmentPanel.add(addButton);

		itemCacheBuffer.clear();

		for (Map.Entry<String, File> entry : attachments.entrySet())
		{
			String key = entry.getKey();
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

		revalidate();
	}

	private JPanel createAttachment(Map.Entry<String, File> entry)
	{
		JPanel attachment = new JPanel();

		attachment.add(new JLabel(entry.getKey()));

		// hover for info
		StringBuilder toolTip = new StringBuilder();
		toolTip.append("<html>File name: ").append(entry);

		File file = entry.getValue();
		if (file != null)
			toolTip.append("<br/>File size: ").append(Utils.readableFileSize(file));

		toolTip.append("</html>");
		attachment.setToolTipText(toolTip.toString());

		// right click to remove
		if (editable)
			attachment.addMouseListener(this);

		return attachment;
	}

	public void setAttachments(List<String> names)
	{
		attachments.clear();

		for (String name : names)
			attachments.put(name, null);

		refresh();
	}

	public void removeAttachment(String attachmentName)
	{
		attachments.remove(attachmentName);
		refresh();
	}

	public void removeAllAttachments()
	{
		attachments.clear();
		refresh();
	}
}