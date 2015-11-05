package dxw405.gui.workers;

import dxw405.util.Logging;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

public abstract class Worker
{
	private static final Map<Class<? extends Worker>, Worker> INSTANCES;

	static
	{
		INSTANCES = new HashMap<>();
		UIManager.put("ProgressMonitor.progressText", "Working...");
	}

	private HardWorker worker;

	public void run(Component parent)
	{
		// already running
		Class<? extends Worker> clazz = getClass();
		Worker currentWorker = INSTANCES.get(clazz);
		if (currentWorker != null)
		{
			Logging.fine(clazz.getSimpleName() + " is already running");

			SwingUtilities.invokeLater(() -> {
				currentWorker.worker.dialog.toFront();
				currentWorker.worker.dialog.repaint();
			});

			return;
		}
		INSTANCES.put(clazz, this);

		ProgressMonitor monitor = new ProgressMonitor(parent, null, null, 0, 100);
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		monitor.setNote("Working...");
		monitor.setProgress(0);

		worker = new HardWorker(monitor, () -> Worker.this.work(new OptionalProgressMonitor(monitor)));
		worker.probeForDialog();
		worker.execute();
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

		public void setNote(String note) {if (exists()) monitor.setNote(note);}

		public int getMaximum() {return monitor.getMaximum();}
	}

	private class HardWorker extends SwingWorker<Void, Void>
	{
		private ProgressMonitor monitor;
		private Runnable task;

		private JDialog dialog;
		private JProgressBar progressBar;
		private JOptionPane optionPane;

		public HardWorker(ProgressMonitor monitor, Runnable task)
		{
			this.monitor = monitor;
			this.task = task;
			this.dialog = null;
			this.progressBar = null;
			this.optionPane = null;
		}

		private void probeForDialog()
		{
			if (progressBar == null)
			{
				dialog = (JDialog) monitor.getAccessibleContext().getAccessibleParent();
				dialog.setResizable(false);
				recurse(dialog, false);
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
					((JButton) child).setText("Hide");
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

		@Override
		protected void done()
		{
			INSTANCES.put(Worker.this.getClass(), null);
		}
	}
}
