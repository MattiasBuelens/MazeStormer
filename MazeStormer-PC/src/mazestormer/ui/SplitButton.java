package mazestormer.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

import org.gpl.JSplitButton.JSplitButton;

/**
 * Small wrapper around the JSplitButton which fixes some positioning issues.
 */
public class SplitButton extends JSplitButton {

	private static final long serialVersionUID = 1L;

	public SplitButton(String text, Icon icon) {
		super(text, icon);
	}

	public SplitButton(String text) {
		this(text, null);
	}

	public SplitButton(Icon icon) {
		this(null, icon);
	}

	public SplitButton() {
		this(null, null);
	}

	@Override
	public UI getUI() {
		return (UI) ui;
	}

	@Override
	public void updateUI() {
		setUI(new UI());
	}

	public static class UI extends BasicButtonUI {
		public static ComponentUI createUI(JComponent c) {
			return new UI();
		}

		@Override
		public Dimension getPreferredSize(JComponent c) {
			Dimension d = super.getPreferredSize(c);
			if (c instanceof SplitButton) {
				// Add the width of the split button to the preferred size
				SplitButton sb = (SplitButton) c;
				int spacing = sb.getSplitWidth();
				d.width += spacing;
			}
			return d;
		}

		@Override
		protected void paintText(Graphics g, AbstractButton b,
				Rectangle textRect, String text) {
			if (b instanceof SplitButton) {
				// Translate the text back by half the split button width
				SplitButton sb = (SplitButton) b;
				int spacing = sb.getSplitWidth();
				textRect.translate(-spacing / 2, 0);
			}
			super.paintText(g, b, textRect, text);
		}

	}

}
