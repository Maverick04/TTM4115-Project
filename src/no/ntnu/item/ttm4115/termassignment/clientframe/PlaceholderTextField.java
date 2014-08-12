package no.ntnu.item.ttm4115.termassignment.clientframe;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.FocusManager;
import javax.swing.JTextField;

/**
 * A JTextField which displays a gray placeholder text when
 * not focussed and no text has been entered yet.
 */
public class PlaceholderTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	/** The placeholder text */
	private String placeholder;

	/**
	 * Construct a new PlaceholderTextField
	 * 
	 * @param placeholder the placeholder text
	 */
	public PlaceholderTextField(String placeholder) {
		this.placeholder = placeholder;
	}
	
	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);

		if (getText().isEmpty()
				&& !(FocusManager.getCurrentKeyboardFocusManager()
						.getFocusOwner() == this)) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.GRAY);
			// TODO: these values should be calculated from the current theme
			g2.drawString(placeholder, 5, 15);
			g2.dispose();
		}
	}

}
