package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class MapPanelTest {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MapPanelTest window = new MapPanelTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MapPanelTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("MapPanelTest");
		frame.setBounds(100, 100, 559, 485);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		MapPanel map = new MapPanel();
		frame.getContentPane().add(map, BorderLayout.CENTER);
	}

}
