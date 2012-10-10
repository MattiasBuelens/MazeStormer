package mazestormer.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

public class MainControl extends JFrame{

	public static final Font STANDARD_FONT = new Font("Verdana", Font.BOLD, 11);

	private static final long serialVersionUID = 14L;
	
	private JPanel configurationPane;
	private JPanel feedbackPane;
	private JTextArea feedback;
	
	private JTabbedPane plotTabPane;
	private JPanel consolePane;

	private IModelViewController mvc;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainControl frame = new MainControl(new ModelViewController());
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
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 660);
		setLocationRelativeTo(null);
		
		initiateComponents();
	}
	
	private Box rightPanel;
	
	private void initiateComponents() {
		setJMenuBar(getMainMenuBar());
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new MigLayout("insets 0", "", ""));
	    
	    initiateConfigurationPanel();
	    initiateTabPanel();
	    initiateConsolePanel();
	    initiateFeedbackPanel();
	    
	    this.rightPanel = new Box(BoxLayout.Y_AXIS);
	    this.rightPanel.add(this.plotTabPane);
        this.rightPanel.add(this.consolePane);
	    
	    mainPanel.add(this.configurationPane, "shrinky, top, w 450!");
	    mainPanel.add(this.rightPanel, "spany 5, wrap, grow, pushx, wmin 400");
	    mainPanel.add(this.feedbackPane, "pushy, growy, w 450!");

	    JScrollPane contentScrollPane = new JScrollPane(mainPanel);
	    contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
	    setContentPane(contentScrollPane);
	  }
	
	 private void initiateConfigurationPanel(){
	    if(this.configurationPane == null) {
	      this.configurationPane = new JPanel();
	      this.configurationPane.setBorder(getTitleBorder("Configuration"));
	      this.configurationPane.setLayout(new MigLayout());
	      //this.optionPane.setLayout(new MigLayout("insets 0", "", "top, align 50%"));
	
	      JLabel mode = new JLabel("Robot Type");
	      mode.setHorizontalAlignment(JLabel.RIGHT);
	      this.configurationPane.add(mode, "w 65!");
	      final JComboBox ob = new JComboBox(ROBOT_TYPES);
	      ob.addActionListener(new ActionListener(){
	    	  @Override
	    	  public void actionPerformed(ActionEvent e) {
	    		  changeMode((String) ob.getSelectedItem());
	    	  }
	      });
	      this.configurationPane.add(ob, "span, growx, wrap");
	  
	      JLabel action = new JLabel("Action");
	      action.setHorizontalAlignment(JLabel.RIGHT);
	      this.configurationPane.add(action, "w 65!");
	      final JComboBox ab = new JComboBox(OPTIONS);
	      ab.addActionListener(new ActionListener(){
	    	  	@Override
				public void actionPerformed(ActionEvent e) {
	    	  		changeOption((String) ob.getSelectedItem(), (String) ab.getSelectedItem());
	    	  	}
		  });
	      this.configurationPane.add(ab, "span, growx, wrap");
	      
	      this.configurationPane.add(new JSeparator(JSeparator.VERTICAL), "spany 5, growy, w 2!");
	    }
	 }
	
	private void initiateTabPanel(){
	    if(this.plotTabPane == null) {
	      this.plotTabPane = new JTabbedPane();
	      this.plotTabPane.add("Tab1", new JPanel());
	      this.plotTabPane.add("Tab2", new JPanel());
	    }
	 }
	
	private void initiateConsolePanel(){
	    if(this.consolePane == null) {
	      this.consolePane = new JPanel();
	      this.consolePane.setBorder(getTitleBorder("Console"));
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
	
	private Border getTitleBorder(String title){
		return BorderFactory.createTitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("null", Font.BOLD, 12), Color.BLUE);
	}

	private void setControlPane(ConsolePanel panel) throws NullPointerException {
		if(panel == null)
			throw new NullPointerException("The control pane may not refer the null reference.");
		this.rightPanel.remove(this.consolePane);
		this.consolePane = panel;
		this.consolePane.setBorder(getTitleBorder("Console"));
		this.rightPanel.add(this.consolePane);
		validate();
	}
	
	IModelViewController getModelViewController(){
		return this.mvc;
	}

	void setFeedback(String msg){
		this.feedback.setText(msg);
	}

	void addFeeback(String msg){
		this.feedback.append(msg);
	}

	private void updateFeedback(){
		if(getModelViewController().getFeedback() != null)
			setFeedback(getModelViewController().getFeedback());
	}
	
	private static final String[] ROBOT_TYPES = {"---", "Physical", "Virtual"};
	private static final String[] OPTIONS = {"---", "Connect", "Control", "Polygon"};
	
	private void changeMode(String mode){
		if(mode != null){
			if(mode.equals("Manual"));
				
			if(mode.equals("Simulator"));
		}
	}
	
	private void changeOption(String mode, String option){
		if(option != null && mode != null && !"---".equals(mode)){
			if(option.equals("---"))
				setControlPane(new ConsolePanel(this));
			if(option.equals("Connect"))
				setControlPane(new ConnectConsolePanel(this));
			if(option.equals("Control"))
				setControlPane(new ControlConsolePanel(this));
			if(option.equals("Polygon"))
				setControlPane(new PolygonConsolePanel(this));
		}
		
		if("---".equals(mode))
			setFeedback("Select a mode.");
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
		int choice = JOptionPane.showConfirmDialog(this,"Are you sure you want to quit MazeStormer?","Close",0,1,new ImageIcon(MainControl.class.getResource("/res/images/ui/close.png")));
		if(choice == 0){
			setVisible(false);
			dispose();
		}
	}

	// -- HELP --

	private void showAbout() { // TODO
		String about = "MazeStormer\n\n" + "Version: " + "Default Version"
				+ "Author: " + "Team Bronze\n\n"
				+ "(c) Copyright Team Bronze.\n" + "All rights reserved.";
		JOptionPane.showMessageDialog(this,about,"About MazeStormer",JOptionPane.INFORMATION_MESSAGE,new ImageIcon(MainControl.class.getResource("/res/images/ui/---.png")));
	}
}
