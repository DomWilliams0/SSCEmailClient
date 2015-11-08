package dxw405.gui.workers;

import dxw405.Email;

import java.awt.Desktop;
import java.io.IOException;

public class OpenAttachmentWorker extends SaveAttachmentWorker
{
	public OpenAttachmentWorker(Email email, String fileName)
	{
		super(email, fileName);
	}

	@Override
	protected void work(OptionalProgressMonitor monitor)
	{
		super.work(monitor);

		// open
		monitor.reset("Opening file...");
		try
		{
			Desktop.getDesktop().open(savePath.toFile());
		} catch (IOException e)
		{
			error("Could not open attachment '" + fileName + "'", e);
		}
	}
}
