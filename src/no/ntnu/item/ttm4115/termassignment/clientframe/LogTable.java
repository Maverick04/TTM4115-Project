package no.ntnu.item.ttm4115.termassignment.clientframe;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Three-column table (time in/out message) to show events sent and received.
 * The table will scroll down if it is aware of its enclosing {@link JScrollPane}.
 */
public class LogTable extends JTable {
	private static final long serialVersionUID = 1L;

	/** Direction of the event (incoming or outgoing) */
	public enum Direction {IN, OUT}

	/** Scrollpane that contains this table, used for scrolling down on add */
	private JScrollPane scrollPane = null;

	/**
	 * Construct new log table
	 */
	public LogTable() {
		super(new DefaultTableModel(new Object[0][0], new String[]{"Time", "in/out", "Message"}));
		getColumnModel().getColumn(0).setMaxWidth(SwingUtilities.computeStringWidth(getFontMetrics(getFont()), "MM:MM:MM"));
		getColumnModel().getColumn(1).setMaxWidth(SwingUtilities.computeStringWidth(getFontMetrics(getFont()), "IN/OUT"));
	}
	
	/** {@inheritDoc}} */
	public DefaultTableModel getModel() {
		return (DefaultTableModel) super.getModel();
	}
	/** {@inheritDoc}} */
	public void setModel(TableModel model) throws IllegalArgumentException {
		if (model instanceof DefaultTableModel) {
			super.setModel(model);
		} else {
			throw new IllegalArgumentException("LogTable requires DefaultTableModel");
		}
	}
	
	/**
	 * Add one event to the log.
	 * If the scroll pane is known, scroll down.
	 * 
	 * @param direction	direction of the event 
	 * @param message the message describing the event
	 */
	public void addLogEntry(Direction direction, String message) {
		if (direction == null)
			throw new NullPointerException("Direction");
		Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		getModel().addRow(new Object[]{
				sdf.format(cal.getTime()),
				direction,
				message
		});
		if (scrollPane != null) {
			final JScrollPane scrollPane = this.scrollPane;
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					JScrollBar vertical = scrollPane.getVerticalScrollBar();
					vertical.setValue(vertical.getMaximum());
				}});
		}
	}

	/**
	 * Make the table aware of the {@link JScrollPane} that contains it.
	 * This knowledge is used to scroll the {@link JScrollPane} down when new information is added,
	 * so that the new information is always visible
	 * 
	 * @param scrollPane the {@link JScrollPane} that contains this <code>LogTable</code>
	 */
	public void setScrollPane(JScrollPane scrollPane) {
		if (scrollPane != null) {
			this.scrollPane = scrollPane;
		}
	}

}
