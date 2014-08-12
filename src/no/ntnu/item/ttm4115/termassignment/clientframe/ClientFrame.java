package no.ntnu.item.ttm4115.termassignment.clientframe;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import no.ntnu.item.arctis.runtime.Block;

/**
 * A basic GUI intended to be instantiated many times.
 * Each time {@link #pack()} is called,
 * the windows are aligned in a grid.
 * 
 * Additionally, some convenience functions are added,
 * to make for cleaner GUI code in the actual block classes.
 */
public class ClientFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Signal which is sent upon frame dispose
	 * @see #dispose()
	 */
	public static final String SIGNAL_EXIT = "EXIT";
	
	/**
	 * Create a button which will send a block event
	 * @param signal	Name of the event
	 * @param caption	Text on the button
	 * @param additionalActions	(optional) additional actions to execute when the button is clicked
	 * @return	the newly generated button
	 */
	public JButton createEventButton(String signal, String caption, ActionListener... additionalActions) {
		return createEventButton(signal, caption, null, additionalActions);
	}
	/**
	 * Create a button which will send a block event with a String text attached
	 * @param signal	Name of the event
	 * @param caption	Text on the button
	 * @param data	Object whose {@link Object#toString()} will return the String that is sent with the event 
	 * @param additionalActions	(optional) additional actions to execute when the button is clicked
	 * @return	the newly generated button
	 */
	public JButton createEventButton(String signal, String caption, Object data, ActionListener... additionalActions) {
		JButton button = new JButton(caption);
		if (SIGNAL_EXIT.equals(signal)) {
			button.addActionListener(new DisposeAction());
		} else {
			button.addActionListener(new SignalSender(signal, data));
		}
		for (ActionListener l : additionalActions) {
			button.addActionListener(l);
		}
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		return button;
	}
	
	/**
	 * Enlarge the text on the button with 50%
	 * @param button	the button whose text must be enlarged
	 */
	public static void growButton(JButton button) {
		button.setFont(button.getFont().deriveFont(button.getFont().getSize2D()*1.5F));
	}

	/** The ReactiveBlocks block that this GUI represents */
	protected final Block block;

	/** The alias of Block, may be null if block is single-session */
	protected final String alias;

	/** Component for logging messages to */
	private LogTable logTable;

	/** List of all previously generated windows */
	private static List<ClientFrame> windows = new LinkedList<ClientFrame>();

	/**
	 * Construct a new basic GUI.
	 * After construction, components should be added.
	 * 
	 * If the block is a multi-session block,
	 * use {@link #BasicGUI(Block, String)} instead.
	 * 
	 * @param block	The block that this GUI represents
	 * @throws HeadlessException	If Java is running headless
	 */
	public ClientFrame(Block block) throws HeadlessException {
		this(block, null);
	}
	/**
	 * Construct a new basic GUI.
	 * After construction, components should be added.
	 * 
	 * If the block is a single-session block,
	 * use {@link #BasicGUI(Block)} instead.
	 * 
	 * @param block	The block that this GUI represents
	 * @throws HeadlessException	If Java is running headless
	 */
	public ClientFrame(Block block, String alias) throws HeadlessException {
		super((alias == null ? "" : alias + " - ") + block.getClass().getSimpleName());
		this.block = block;
		this.alias = alias;
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Add a table with log messages to the GUI
	 * @return	the table that was added
	 */
	public LogTable addLogTable() {
		JScrollPane scrollPane;
		add(scrollPane = new JScrollPane(logTable = new LogTable()), BorderLayout.CENTER);
		logTable.setScrollPane(scrollPane);
		return logTable;
	}
	/**
	 * Add a table with log messages to the GUI,
	 * at the given constraint. Constraint should be one of:
	 * {@link BorderLayout#NORTH},
	 * {@link BorderLayout#EAST},
	 * {@link BorderLayout#WEST},
	 * {@link BorderLayout#SOUTH},
	 * <code>null</code>.
	 * 
	 * @param constraints Location for the log table. Null to not add the table; add it yourself later.
	 * 
	 * @return	the table that was added
	 */
	public LogTable addLogTable(Object constraints) {
		logTable = new LogTable();
		if (constraints != null) {
			JScrollPane scrollPane;
			add(scrollPane = new JScrollPane(logTable), constraints);
			logTable.setScrollPane(scrollPane);
		}
		return logTable;
	}

	/** {@inheritDoc} */
	public void dispose() {
		super.dispose();
		block.sendToBlock(SIGNAL_EXIT);
	}
	
	/**
	 * Will, in addition to {@link Window#pack()}, also move the window.
	 * This will hopefully ensure that the windows don't overlap.
	 */
	public void pack() {
		super.pack();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				synchronized(windows) {
					if (!windows.contains(ClientFrame.this))
						windows.add(ClientFrame.this);
					alignAll();
				}
			}});
	}

	/**
	 * Align all previously created BasicGUI windows in a grid.
	 */
	protected static void alignAll() {
		int rows = (int)Math.sqrt(windows.size());
		int cols = (int)(Math.ceil( ((double)windows.size()) / rows));
		int lastRow = windows.size() - cols*(rows-1);
		int width, height;
		Insets insets = Toolkit.getDefaultToolkit()
		 .getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		Rectangle dBounds = new Rectangle(
				bounds.x+insets.left,
				bounds.y+insets.top,
				bounds.width-insets.left-insets.right,
				bounds.height-insets.top-insets.bottom
			);
	 
		if (lastRow == 0) {
			rows--;
			height = dBounds.height / rows;
		} else {
			height = dBounds.height / rows;
			if (lastRow < cols) {
				rows--;
				width = dBounds.width / cols;
				for (int i = 0; i < lastRow; i++) {
					windows.get(cols*rows+i).setBounds(
							dBounds.x+i*width, 
							dBounds.y+rows*height,
							width, 
							height 
						);
				}
			}
		}
				
		width = dBounds.width / cols;
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				windows.get(i+j*cols).setBounds( 
						dBounds.x+i*width, 
						dBounds.y+j*height,
						width, 
						height 
					);
			}
		}
	}

	/**
	 * Action which will close this GUI.
	 * Will be executed when a button will send a signal {@value ClientFrame#SIGNAL_EXIT}
	 */
	private class DisposeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			dispose();
		}
		
	}
	
	/**
	 * Action which will trigger an event in ReactiveBlocks.
	 */
 	private class SignalSender implements ActionListener {

 		private final String signal;
 		private final Object data;

		public SignalSender(String signal, Object data) {
 			this.signal = signal;
 			this.data = data;
 		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (data == null) {
				block.sendToBlock(signal);
			} else {
				block.sendToBlock(signal, data.toString());
			}
			if (logTable != null) {
				if (data == null) {
					logTable.addLogEntry(LogTable.Direction.OUT, signal+" button pressed");
				} else {
					String data = this.data.toString();
					logTable.addLogEntry(LogTable.Direction.OUT, signal+": "+data);
				}
			}
		}

	}
 	
}
