package mazestormer.ui;

import java.awt.BorderLayout;

import javax.swing.JTable;
import javax.swing.border.TitledBorder;

public class StatePanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;

	public StatePanel() {
		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		table = new JTable();
		add(table);
	}

}
