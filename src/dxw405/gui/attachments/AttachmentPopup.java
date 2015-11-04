package dxw405.gui.attachments;

import dxw405.Email;
import dxw405.util.Logging;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AttachmentPopup extends JPopupMenu
{
	private JLabel component;
	private Email email;

	public AttachmentPopup(AttachmentSelection attachmentSelection, boolean editable)
	{
		if (editable)
		{
			add(new JMenuItem(new AbstractAction("Remove")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachmentSelection.removeAttachment(component.getText());
				}
			}));

			add(new JMenuItem(new AbstractAction("Remove All")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attachmentSelection.removeAllAttachments();
				}
			}));
		} else
		{
			add(new JMenuItem(new AbstractAction("Open")
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					String fileName = component.getText();
					String quotedName = "'" + fileName + "'";

					InputStream inputStream = email.getContent().getAttachmentInputStream(fileName);
					if (inputStream == null)
					{
						logError("Could not get a hold of file stream for attachment " + quotedName, null);
						return;
					}

					boolean delete = false;

					// save to temp
					Path saveDir = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "temporary_attachments");
					try
					{
						Files.createDirectories(saveDir);
					} catch (IOException e)
					{
						logError("Could not create temporary attachment directory", e);
						return;
					}

					Path savePath = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "temporary_attachments", fileName);
					try
					{
						long bytes = Files.copy(inputStream, savePath, StandardCopyOption.REPLACE_EXISTING);
						Logging.fine("Saved " + Utils.readableFileSize(bytes) + " to " + savePath.toAbsolutePath());
					} catch (IOException e)
					{
						logError("Could not save attachment " + quotedName, e);
						delete = true;
					}

					// open
					try
					{
						Desktop.getDesktop().open(savePath.toFile());
					} catch (IOException e)
					{
						logError("Could not open attachment " + quotedName, e);
						delete = true;
					}

					// delete
					if (delete)
					{
						try
						{
							Files.deleteIfExists(savePath);
							Logging.fine("Deleted temporary attachment " + quotedName);
						} catch (IOException e)
						{
							logError("Could not delete temporary attachment " + quotedName, e);
						}
					}
				}

				private void logError(String message, Exception e)
				{
					JOptionPane.showMessageDialog(component, message, "Uh oh", JOptionPane.ERROR_MESSAGE);
					if (e != null)
						Logging.severe(message, e);
					else
						Logging.severe(message);
				}
			}));
		}
	}

	public void display(Component component, Email email)
	{
		this.component = (JLabel) component;
		this.email = email;
		show(component, component.getX(), component.getY() + component.getHeight());
	}
}