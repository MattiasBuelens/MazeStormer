package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.Beans;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.gpl.JSplitButton.JSplitButton;

public class MapPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JToolBar actionBar;
	private MapCanvas canvas;
	private MapDocument document;

	public MapPanel() {
		setLayout(new BorderLayout(0, 0));

		createActionBar();
		add(actionBar, BorderLayout.NORTH);

		if (!Beans.isDesignTime()) {
			createCanvas();
			add(canvas, BorderLayout.CENTER);
		}
	}

	private void createActionBar() {
		JToolBar actionBar = new JToolBar();

		JSplitButton btnGo = new JSplitButton("Go");

		JPopupMenu menuGo = new JPopupMenu();
		btnGo.setPopupMenu(menuGo);
		if (Beans.isDesignTime()) {
			// TODO Remove
			addPopup(this, menuGo);
		}

		JMenuItem mntmGoToRobot = new JMenuItem("Go to robot");
		menuGo.add(mntmGoToRobot);

		JMenuItem mntmGoToStart = new JMenuItem("Go to start");
		menuGo.add(mntmGoToStart);

		actionBar.add(btnGo);
		this.actionBar = actionBar;
	}

	private void createCanvas() {
		this.document = new MapDocument();
		this.canvas = new MapCanvas(this.document);
	}

	// TODO Remove
	private static void addPopup(Component component, final JPopupMenu popup) {

	}
}
