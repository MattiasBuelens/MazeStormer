package mazestormer.ui;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.border.TitledBorder;

public class LogPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	public LogPanel() {
		setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		
		JList list = new JList();
		add(list);
	}

}
