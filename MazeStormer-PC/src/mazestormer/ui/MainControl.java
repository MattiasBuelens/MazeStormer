package mazestormer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
 * The main frame of the UI architecture.
 * 
 * @version
 * @author Team Bronze
 * 
 */
public class MainControl extends JFrame {

	public static final Font STANDARD_FONT = new Font("Verdana", Font.BOLD, 11);

	private static final long serialVersionUID = 14L;

	private IModelViewController mvc;

	// TODO private Maze maze;

	private JPanel contentPane;
	private JPanel workPane;
	private JPanel controlPane;
	private JTextArea feedback;
	private JLabel upperBar;
	private JPanel upperPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainControl frame = new MainControl(
							new ModelViewController());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainControl(IModelViewController mvc) {
		this.mvc = mvc;

		// FRAME
		// TODO
		// setIconImage(Toolkit.getDefaultToolkit().getImage(MainControl.class.getResource("/res/images/ui/---.png")));
		setTitle("MazeStormer");
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 660);
		setLocationRelativeTo(null);

		// MENU
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnGame = new JMenu("File");
		menuBar.add(mnGame);

		JSeparator separator_1 = new JSeparator();
		mnGame.add(separator_1);

		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK));
		mntmClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showClose();
			}
		});
		mnGame.add(mntmClose);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		JMenuItem mntmTextColor = new JMenuItem("Text Color");
		mntmTextColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showTextColor();
			}
		});
		mnOptions.add(mntmTextColor);

		JMenuItem mntmTextBackgroundColor = new JMenuItem(
				"Text Background Color");
		mntmTextBackgroundColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showTextBackgroundColor();
			}
		});
		mnOptions.add(mntmTextBackgroundColor);

		JMenuItem mntmBackgroundColor = new JMenuItem("Background Color");
		mntmBackgroundColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showBackgroundColor();
			}
		});
		mnOptions.add(mntmBackgroundColor);

		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		JMenuItem mntmModes = new JMenuItem("Modes");
		mntmModes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showModes();
			}
		});
		mnTools.add(mntmModes);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmManual = new JMenuItem("Manual");
		mntmManual.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showManual();
			}
		});
		mnHelp.add(mntmManual);

		JSeparator separator = new JSeparator();
		mnHelp.add(separator);

		JMenuItem mntmAboutRoborally = new JMenuItem("About MazeStormer");
		mntmAboutRoborally.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout();
			}
		});
		mnHelp.add(mntmAboutRoborally);

		JMenuItem mntmCredits = new JMenuItem("Credits");
		mntmCredits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCredits();
			}
		});
		mnHelp.add(mntmCredits);

		JSeparator separator_3 = new JSeparator();
		mnHelp.add(separator_3);

		JMenuItem mntmContact = new JMenuItem("Contact");
		mntmContact.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showContact();
			}
		});
		mnHelp.add(mntmContact);

		// CONTENT PANE
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		this.contentPane.setLayout(null);

		// UPPER BAR+PANE
		this.upperBar = new JLabel();
		this.upperBar.setFont(STANDARD_FONT);
		this.upperPane = new JPanel();
		this.upperPane.setBounds(10, 0, 832, 22);
		this.upperPane.add(upperBar);
		this.contentPane.add(upperPane);
		this.upperBar.setFont(STANDARD_FONT);

		// FEEDBACK TEXT AREA
		this.feedback = new JTextArea();
		this.feedback.setBackground(Color.WHITE);
		this.feedback.setEnabled(true);
		this.feedback.setLineWrap(true);
		this.feedback.setWrapStyleWord(true);
		this.feedback.setEditable(false);
		this.feedback.setCursor(null);
		this.feedback.setFocusable(false);
		this.feedback.setFont(STANDARD_FONT);
		JScrollPane sbrText = new JScrollPane(feedback);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sbrText.setBounds(563, 26, 279, 435);
		contentPane.add(sbrText);

		// INITIAL STATE
		setFeedback(null);
		setInitial();
	}

	IModelViewController getModelViewController() {
		return this.mvc;
	}

	// -- STATE --

	private void setInitial() {
		setUpperBar(null);
		setControlPane(new ConnectModePanel(this));
	}

	void setWorkPane(ModePanel panel) throws NullPointerException {
		if (panel == null)
			throw new NullPointerException(
					"The work pane may not refer the null reference.");
		if (this.controlPane != null)
			this.contentPane.remove(this.controlPane);
		this.workPane = panel;
		this.workPane.setBounds(10, 26, 548, 529);
		this.contentPane.add(workPane);
		repaint();
	}

	void setControlPane(ModePanel panel) throws NullPointerException {
		if (panel == null)
			throw new NullPointerException(
					"The control pane may not refer the null reference.");
		if (this.controlPane != null)
			this.contentPane.remove(this.controlPane);
		this.controlPane = panel;
		this.controlPane.setBounds(10, 465, 832, 134);
		this.contentPane.add(controlPane);
		repaint();
	}

	void setUpperBar(String msg) {
		this.upperBar.setText(msg);
	}

	void setFeedback(String msg) {
		this.feedback.setText(msg);
	}

	void addFeeback(String msg) {
		this.feedback.append(msg);
	}

	private void updateFeedback() {
		if (getModelViewController().getFeedback() != null)
			setFeedback(getModelViewController().getFeedback());
	}

	// -- FILE --

	private void showClose() {
		int choice = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to quit MazeStormer?",
				"Close",
				0,
				1,
				new ImageIcon(MainControl.class
						.getResource("/res/images/ui/close.png")));
		if (choice == 0) {
			setVisible(false);
			dispose();
		}
	}

	// -- TOOLS --

	private void showModes() {
		Object[] possibleValues = { "Connect", "Control", "Polygon" };
		Object selectedValue = JOptionPane.showInputDialog(
				this,
				"Select a mode:",
				"Modes",
				JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(MainControl.class
						.getResource("/res/images/ui/flag.png")),
				possibleValues, possibleValues[0]);
		if (selectedValue != null) {
			if (selectedValue.equals("Connect"))
				setControlPane(new ConnectModePanel(this));
			else if (selectedValue.equals("Control"))
				setControlPane(new ControlModePanel(this));
			else if (selectedValue.equals("Polygon"))
				setControlPane(new PolygonModePanel(this));
		}
	}

	// -- HELP --

	private void showCredits() {
		String credits = "Default"; // TODO
		setFeedback(credits);
	}

	private void showContact() {
		int choice = JOptionPane.showConfirmDialog(
				this,
				"Do you want to open your mail client?",
				"Contact",
				0,
				1,
				new ImageIcon(MainControl.class
						.getResource("/res/images/ui/email.png")));
		if (choice == 0) {
			Desktop desktop;
			if (Desktop.isDesktopSupported()
					&& (desktop = Desktop.getDesktop())
							.isSupported(Desktop.Action.MAIL)) {
				java.net.URI mailto;
				try {
					mailto = new java.net.URI(
							"mailto:matthias.moulin@gmail.com?subject=MazeStormer");
					desktop.mail(mailto);
				} catch (java.net.URISyntaxException e) {
					e.printStackTrace();
				} catch (java.io.IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void showAbout() { // TODO
		String about = "MazeStormer\n\n" + "Version: " + "Default Version"
				+ "Author: " + "Team Bronze\n\n"
				+ "(c) Copyright Team Bronze.\n" + "All rights reserved.";
		JOptionPane.showMessageDialog(
				this,
				about,
				"About MazeStormer",
				JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(MainControl.class
						.getResource("/res/images/ui/---.png")));
	}

	// -- OPTIONS --

	private void showManual() {
	}

	private void showTextColor() {
		Color c = showColor(this);
		if (c != null) {
			this.feedback.setForeground(c);
			this.upperBar.setForeground(c);
		}
	}

	private void showTextBackgroundColor() {
		Color c = showColor(this);
		if (c != null) {
			this.feedback.setBackground(c);
			this.upperPane.setBackground(c);
		}
	}

	private void showBackgroundColor() {
		Color c = showColor(this);
		if (c != null)
			this.contentPane.setBackground(c);
	}

	static Color showColor(Component parent) {
		Color[] colors = { Color.BLACK, Color.BLUE, Color.CYAN,
				Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY,
				Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
				Color.WHITE, Color.YELLOW };
		String[] possibleValues = { "Black", "Blue", "Cyan", "Dark Grey",
				"Gray", "Green", "Light Gray", "Magenta", "Orange", "Pink",
				"Red", "White", "Yellow" };
		assert (colors.length != possibleValues.length);
		Object selectedValue = JOptionPane.showInputDialog(
				parent,
				"Select a color:",
				"Color",
				JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(MainControl.class
						.getResource("/res/images/ui/question.png")),
				possibleValues, possibleValues[0]);

		if (selectedValue == null)
			return null;

		int i = 0;
		for (; i < possibleValues.length; i++)
			if (possibleValues[i].equals((String) selectedValue))
				break;
		return colors[i];
	}
}