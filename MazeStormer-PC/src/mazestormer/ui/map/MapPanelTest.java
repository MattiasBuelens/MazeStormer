package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class MapPanelTest {

	private JFrame frmMappaneltest;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MapPanelTest window = new MapPanelTest();
					window.frmMappaneltest.setVisible(true);
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
		frmMappaneltest = new JFrame();
		frmMappaneltest.setTitle("MapPanelTest");
		frmMappaneltest.setBounds(100, 100, 450, 300);
		frmMappaneltest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMappaneltest.getContentPane().setLayout(new BorderLayout(0, 0));

		MapPanel map = new MapPanel();
		frmMappaneltest.getContentPane().add(map, BorderLayout.CENTER);
	}

}
