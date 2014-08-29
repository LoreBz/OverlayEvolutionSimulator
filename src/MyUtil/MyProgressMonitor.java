package MyUtil;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

public class MyProgressMonitor extends ProgressMonitor implements
		PropertyChangeListener {
	SwingWorker<Void, Void> task;

	public MyProgressMonitor(Component parentComponent, Object message,
			String note, int min, int max, SwingWorker<Void, Void> task) {
		super(parentComponent, message, note, min, max);
		// TODO Auto-generated constructor stub
		this.task = task;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// if the operation is finished or has been canceled by
		// the user, take appropriate action
		if (this.isCanceled()) {
			task.cancel(true);
		} else if (event.getPropertyName().equals("progress")) {
			// get the % complete from the progress event
			// and set it on the progress monitor
			int progress = ((Integer) event.getNewValue()).intValue();
			this.setProgress(progress);
		}

	}
}
