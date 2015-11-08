package dxw405.gui.workers;

import dxw405.Email;
import dxw405.util.Logging;
import dxw405.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveAttachmentWorker extends Worker
{
	protected String fileName;
	protected Path savePath;
	private Email email;

	public SaveAttachmentWorker(Email email, String fileName)
	{
		this.email = email;
		this.fileName = fileName;
		this.savePath = null;
	}

	@Override
	protected void work(OptionalProgressMonitor monitor)
	{
		Thread workerThread = new Thread(() -> {
			String quotedName = "'" + fileName + "'";

			setIndeterminate(true);
			monitor.reset("Loading...");

			// get stream
			InputStream is = email.getContent().getAttachmentInputStream(fileName);
			if (is == null)
			{
				error("Could not find attachment " + quotedName);
				return;
			}

			// save to temp
			String tmp = System.getProperty("java.io.tmpdir");
			Path saveDir = FileSystems.getDefault().getPath(tmp, "temporary_attachments");
			try
			{
				Files.createDirectories(saveDir);
			} catch (IOException e)
			{
				error("Could not create temporary attachment directory", e);
				return;
			}

			savePath = FileSystems.getDefault().getPath(tmp, "temporary_attachments", fileName);
			if (savePath.toFile().exists())
				return;

			// save
			monitor.reset("Saving file...");
			try
			{
				long bytes = Files.copy(is, savePath);
				Logging.fine("Saved " + Utils.readableFileSize(bytes) + " to " + savePath.toAbsolutePath());
			} catch (IOException e)
			{
				error("Could not save attachment " + quotedName, e);
			}

		});

		workerThread.start();
		try
		{
			workerThread.join();
		} catch (InterruptedException e)
		{
			error("Could not save file", e);
		}
	}

}
