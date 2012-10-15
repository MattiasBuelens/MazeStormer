package mazestormer.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import mazestormer.controller.IMainController;
import mazestormer.controller.MainController;
import net.miginfocom.swing.MigLayout;

public class MainView extends JFrame{

	public static final Font STANDARD_FONT = new Font("Verdana", Font.BOLD, 11);

	private static final long serialVersionUID = 14L;
	
	private JPanel mainPanel;
	private JPanel configurationPane;
	private JPanel feedbackPane;
	private JTextArea feedback;
	private Box rightPanel;

	private IMainController mc;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView frame = new MainView(MainController.getInstance());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainView(IMainController mc) {
		this.mc = mc;
		
		// FRAME
		// TODO
		// setIconImage(Toolkit.getDefaultToolkit().getImage(MainControl.class.getResource("/res/images/ui/---.png")));
		setTitle("MazeStormer");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 660);
		setLocationRelativeTo(null);
		
		initiateComponents();
	}
	
	private void initiateComponents() {
		setJMenuBar(getMainMenuBar());
	    this.mainPanel = new JPanel();
	    this.mainPanel.setLayout(new MigLayout("insets 0", "", ""));
	    
	    initiateConfigurationPanel();
	    initiateRightPanel();
	    initiateFeedbackPanel();
	    
	    this.mainPanel.add(this.configurationPane, "shrinky, top, w 450!");
	    this.mainPanel.add(this.rightPanel, "spany 5, wrap, grow, pushx, wmin 400");
	    this.mainPanel.add(this.feedbackPane, "pushy, growy, w 450!");

	    JScrollPane contentScrollPane = new JScrollPane(this.mainPanel);
	    contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
	    setContentPane(contentScrollPane);
	  }
	
	 private void initiateConfigurationPanel(){
	    if(this.configurationPane == null) {
	      this.configurationPane = new JPanel();
	      this.configurationPane.setBorder(getTitleBorder("Configuration"));
	      this.configurationPane.setLayout(new MigLayout());
	      //this.optionPane.setLayout(new MigLayout("insets 0", "", "top, align 50%"));
	
	      JLabel robotType = new JLabel("Robot Type");
	      robotType.setHorizontalAlignment(JLabel.RIGHT);
	      this.configurationPane.add(robotType, "w 65!");
	      this.configurationPane.add(new JSeparator(JSeparator.VERTICAL), "spany 5, growy, w 2!");
	      final JComboBox rb = new JComboBox(ROBOT_TYPES);
	      this.configurationPane.add(rb, "span, growx, wrap");
	  
	      JLabel mode = new JLabel("Mode");
	      mode.setHorizontalAlignment(JLabel.RIGHT);
	      this.configurationPane.add(mode, "w 65!");
	      final JComboBox mb = new JComboBox(MODES);
	      this.configurationPane.add(mb, "span, growx, wrap");
	      
	      
	      JLabel space = new JLabel();
	      this.configurationPane.add(space, "w 65!");
	      JButton confirmButton = new JButton("Confirm");
	      confirmButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeConfiguration((String) rb.getSelectedItem(), (String) mb.getSelectedItem());
			} 
	      });
	      this.configurationPane.add(confirmButton);
	    }
	 }
	
	private void initiateRightPanel(){
	    if(this.rightPanel == null) {
	      this.rightPanel = getMainController().getDefault();
	    }
	 }
	
	private void initiateFeedbackPanel(){
	    if(this.feedbackPane == null){
	    	initiateFeedback();
	    	this.feedbackPane = new JPanel();
	    	this.feedbackPane.setBorder(getTitleBorder("Status"));
	    	this.feedbackPane.setLayout(new MigLayout());
	    	//this.feedbackPane.setLayout(new MigLayout("insets 0", "", "top, align 50%"));
	    	JScrollPane sbrText = new JScrollPane(this.feedback);
			sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    	this.feedbackPane.add(sbrText, "push, grow");
	    }
	 }
	
	private void initiateFeedback(){
	    if(this.feedback == null){
	     	this.feedback = new JTextArea();
	     	this.feedback.setBackground(Color.WHITE);
			this.feedback.setEnabled(true);
			this.feedback.setLineWrap(true);
			this.feedback.setWrapStyleWord(true);
			this.feedback.setEditable(false);
			this.feedback.setCursor(null);
			this.feedback.setFocusable(false);
			this.feedback.setFont(STANDARD_FONT);
			this.feedback.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			
			//DefaultCaret caret = (DefaultCaret) this.feedback.getCaret();
		    //caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    }
	 }
	
	public static Border getTitleBorder(String title){
		return BorderFactory.createTitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("null", Font.BOLD, 12), Color.BLUE);
	}
	
	private void setRightPanel(Box rightPanel){
		if(rightPanel == null)
			throw new NullPointerException("The right panel may not refer the null reference.");
		this.mainPanel.remove(this.feedbackPane);
		this.mainPanel.remove(this.rightPanel);
		this.rightPanel = rightPanel;
		this.mainPanel.add(rightPanel, "spany 5, wrap, grow, pushx, wmin 400");
		this.mainPanel.add(this.feedbackPane, "pushy, growy, w 450!");
		validate();
	}
	
	IMainController getMainController(){
		return this.mc;
	}

	void setFeedback(String msg){
		this.feedback.setText(msg);
	}

	void addFeeback(String msg){
		this.feedback.append(msg);
	}

	private void updateFeedback(){
		if(getMainController().getFeedback() != null)
			setFeedback(getMainController().getFeedback());
	}
	
	private static final String[] ROBOT_TYPES = RobotType.getAllRobotTypeNames();
	private static final String[] MODES = {"---", "Connect", "Control", "Polygon"};
	
	private void changeConfiguration(String robotType, String mode){
		if(mode != null && robotType != null){
			if(!RobotType.DEFAULT.getRobotTypeName().equals(robotType)){
				String s = "Changing robot type will disconnect every NXT.\nChanging mode will delete all progress.\nAre you sure you want to continue?";
				int a = JOptionPane.showConfirmDialog(this,s,"Change Configuration",JOptionPane.YES_OPTION,JOptionPane.WARNING_MESSAGE);
				
				if(a == 0){
					if(mode.equals("---"))
						setRightPanel(getMainController().getDefault());
					if(mode.equals("Connect"))
						setRightPanel(getMainController().getConnectView(RobotType.getCorrespondingRobotType(robotType)));
					if(mode.equals("Control"))
						setRightPanel(getMainController().getControlView(RobotType.getCorrespondingRobotType(robotType)));
					if(mode.equals("Polygon"))
						setRightPanel(getMainController().getPolygonView(RobotType.getCorrespondingRobotType(robotType)));
				}
			}
			else
				setFeedback("Select a mode...");
		}
	}
	
	// -- MENU --
	
	private JMenuBar getMainMenuBar(){
		JMenuBar menuBar = new JMenuBar();

		JMenu mnGame = new JMenu("File");
		menuBar.add(mnGame);

		JSeparator separator_1 = new JSeparator();
		mnGame.add(separator_1);

		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		mntmClose.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showClose();
			}
		});
		mnGame.add(mntmClose);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAboutRoborally = new JMenuItem("About MazeStormer");
		mntmAboutRoborally.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showAbout();
			}
		});
		mnHelp.add(mntmAboutRoborally);
		
		return menuBar;
	}

	// -- FILE --

	private void showClose() {
		int choice = JOptionPane.showConfirmDialog(this,"Are you sure you want to quit MazeStormer?","Close",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
		if(choice == 0){
			setVisible(false);
			dispose();
		}
	}

	// -- HELP --

	private void showAbout() {
		String about = "MazeStormer\n\n" + "Version: " + "Default Version\n"
				+ "Author: " + "Team Bronze\n\n"
				+ "(c) Copyright Team Bronze.\n" + "All rights reserved.";
		JOptionPane.showMessageDialog(this,about,"About MazeStormer",JOptionPane.INFORMATION_MESSAGE);
	}
}
