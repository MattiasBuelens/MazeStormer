package mazestormer.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabUI extends JPanel{

	/**
	 * Create the panel.
	 */
	public TabUI(){
		setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 26, 548, 529);
		add(tabbedPane);
	}

}
