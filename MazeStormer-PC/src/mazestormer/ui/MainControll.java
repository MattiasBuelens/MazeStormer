package mazestormer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;

public class MainControll extends JFrame{

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
					MainControll frame = new MainControll(new ModelViewController());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainControll(IModelViewController mvc) {
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
	
	private JPanel rightPanel;
	
	private void initiateComponents() {
		setJMenuBar(getMainMenuBar());
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new MigLayout("insets 0, debug 1000", "", ""));
	    
		      this.configurationPane = new JPanel();
		      this.configurationPane.setBorder(getTitleBorder("Configuration"));
		      this.configurationPane.setLayout(new MigLayout());
		
		      JLabel mode = new JLabel("Mode");
		      mode.setHorizontalAlignment(JLabel.RIGHT);
		      this.configurationPane.add(mode, "w 65!");
		      final JComboBox ob = new JComboBox(MODES);
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
		
        this.plotTabPane = new JTabbedPane();
        this.plotTabPane.add("Tab1", new JPanel());
        this.plotTabPane.add("Tab2", new JPanel());
        this.consolePane = new ControlConsolePanel(null);
        this.consolePane.setBorder(getTitleBorder("Console"));
	    
		      
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
	    this.feedbackPane = new JPanel();
	    this.feedbackPane.setBorder(getTitleBorder("Status"));
	    this.feedbackPane.setLayout(new MigLayout());
	    JScrollPane sbrText = new JScrollPane(this.feedback);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    this.feedbackPane.add(sbrText, "push, grow");
	    
	    this.rightPanel = new JPanel(new BorderLayout());
	    this.rightPanel.add(this.plotTabPane, "Center");
        this.rightPanel.add(this.consolePane, "South");
	    
	    mainPanel.add(this.configurationPane, "shrinky, top, w 450!");
	    mainPanel.add(this.rightPanel, "spany 5, wrap, grow, pushx, wmin 400");
	    mainPanel.add(this.feedbackPane, "pushy, growy, w 450!");

	    JScrollPane contentScrollPane = new JScrollPane(mainPanel);
	    contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
	    setContentPane(contentScrollPane);
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
		this.rightPanel.add(this.consolePane, "South");
		validate();
	}

	private static final String[] MODES = {"---", "Manual", "Simulator"};
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
				setControlPane(new ConsolePanel(null));
			if(option.equals("Connect"))
				setControlPane(new ConnectConsolePanel(null));
			if(option.equals("Control"))
				setControlPane(new ControlConsolePanel(null));
			if(option.equals("Polygon"))
				setControlPane(new PolygonConsolePanel(null));
		}
	}
	
	// -- MENU --
	
	private JMenuBar getMainMenuBar(){
		JMenuBar menuBar = new JMenuBar();

		JMenu mnGame = new JMenu("File");
		menuBar.add(mnGame);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		return menuBar;
	}
}


	