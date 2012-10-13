package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import mazestormer.ui.SplitButton;

import org.apache.batik.swing.JSVGCanvas;

public class MapPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JToolBar actionBar;
	private JSVGCanvas canvas;

	private Map map;

	public MapPanel() {
		setLayout(new BorderLayout(0, 0));

		createActionBar();
		actionBar.setFloatable(false);
		add(actionBar, BorderLayout.NORTH);

		createCanvas();
		add(canvas, BorderLayout.CENTER);
	}

	private void createActionBar() {
		actionBar = new JToolBar();

		JToggleButton btnFollow = new JToggleButton("Follow robot");
		actionBar.add(btnFollow);
		actionBar.add(createGoButton());

		JButton btnReset = new JButton("Reset zoom");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvas.resetRenderingTransform();
			}
		});
		actionBar.add(btnReset);
	}

	private SplitButton createGoButton() {
		JPopupMenu menuGo = new JPopupMenu();

		JMenuItem menuGoStart = new JMenuItem("Go to start");
		menuGo.add(menuGoStart);

		SplitButton btnGo = new SplitButton();
		btnGo.setText("Go to robot");

		btnGo.setPopupMenu(menuGo);
		if (Beans.isDesignTime()) {
			// Show popup menu in designer
			addPopup(this, menuGo);
		}

		return btnGo;
	}

	private void createCanvas() {
		map = new Map();
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));

		RobotLayer layer = new RobotLayer();
		layer.setScale(0.5f);
		layer.setRotationAngle(0);
		map.addLayer(layer);

		canvas = new MapCanvas();
		canvas.setDocument(map.getDocument());
		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
	}

	// TODO Remove
	private static void addPopup(Component component, final JPopupMenu popup) {

	}
}
