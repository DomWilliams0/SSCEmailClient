package dxw405.gui.workers;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;

public abstract class Worker
{
	static
	{
		UIManager.put("ProgressMonitor.progressText", "Working...");
	}

	private HardWorker worker;

	public void run(Component parent)
	{
		ProgressMonitor monitor = new ProgressMonitor(parent, null, null, 0, 100);
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		monitor.setNote("Working...");
		monitor.setProgress(0);

		this.worker = new HardWorker(monitor, () -> Worker.this.work(new OptionalProgressMonitor(monitor)));
		this.worker.probeForDialog();
		this.worker.execute();
	}

	protected abstract void work(OptionalProgressMonitor monitor);

	protected void setIndeterminate(boolean indeterminate)
	{
		if (worker.progressBar != null)
			worker.progressBar.setIndeterminate(indeterminate);
	}

	public static class OptionalProgressMonitor
	{
		public static final OptionalProgressMonitor EMPTY = new OptionalProgressMonitor();
		private ProgressMonitor monitor;

		public OptionalProgressMonitor()
		{
			this(null);
		}

		public OptionalProgressMonitor(ProgressMonitor monitor)
		{
			this.monitor = monitor;
		}

		public void reset(String note, int maximum)
		{
			if (exists())
			{
				monitor.setNote(note);
				monitor.setMaximum(maximum);
			}

		}

		public boolean exists() {return monitor != null;}

		public void reset(String note)
		{
			update(note, 0);
		}

		public void update(String note, int nv)
		{
			if (exists())
			{
				monitor.setNote(note);
				monitor.setProgress(nv);
			}
		}

		public void setProgress(int nv) {if (exists()) monitor.setProgress(nv);}

		public boolean isCanceled() {return exists() && monitor.isCanceled();}

		public void setNote(String note) {if (exists()) monitor.setNote(note);}

		public int getMaximum() {return monitor.getMaximum();}

		public void setMaximum(int m) {if (exists()) monitor.setMaximum(m);}
	}

	private class HardWorker extends SwingWorker<Void, Void>
	{
		private ProgressMonitor monitor;
		private Runnable task;

		private JProgressBar progressBar;
		private JOptionPane optionPane;

		public HardWorker(ProgressMonitor monitor, Runnable task)
		{
			this.monitor = monitor;
			this.task = task;
			this.progressBar = null;
			this.optionPane = null;
		}

		private void probeForDialog()
		{
			if (progressBar == null)
			{
				// get progress bar
				recurse((JDialog) monitor.getAccessibleContext().getAccessibleParent(), false);
			}
		}

		private boolean recurse(Container c, boolean cancelButtonDone)
		{
			Component[] components = c.getComponents();
			for (Component child : components)
			{
				// complete
				if (cancelButtonDone && optionPane != null && progressBar != null)
					return true;

				// cancel button
				if (!cancelButtonDone && child instanceof JButton && ((JButton) child).getText().equals("Cancel"))
				{
					cancelButtonDone = true;
					child.setVisible(false);
				}

				// progress bar
				else if (progressBar == null && child instanceof JProgressBar)
				{
					progressBar = (JProgressBar) child;
				}

				// option pane
				else if (optionPane == null && child instanceof JOptionPane)
				{
					optionPane = (JOptionPane) child;
				}

				// recurse
				if (recurse((Container) child, cancelButtonDone))
					return true;
			}

			return false;
		}

		@Override
		protected Void doInBackground() throws Exception
		{
			task.run();
			return null;
		}
	}
}
