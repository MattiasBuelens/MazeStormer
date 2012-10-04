package mazestormer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A control panel for 'macro view gui's.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class MacroViewPanel extends JPanel{
	
	private static final long serialVersionUID = 16L;
	
	private MainControl mc;
	private MacroViewGUI mvg;

	private JLabel modelStatus;
	private JPanel modelPane;

	public MacroViewPanel(final MainControl mc){
		this.mc = mc;
		
		setLayout(null);
		setBounds(10, 26, 548, 529);
		
		// MVG
		this.mvg = new MacroViewGUI(this);
		this.mvg.setBounds(10, 43, 528, 441);
		add(mvg);
		
		// MODEL BAR & PANE
		this.modelStatus = new JLabel();
		this.modelStatus.setFont(MainControl.STANDARD_FONT);
		this.modelPane = new JPanel();
		this.modelPane.setBounds(10, 495, 528, 24);
		this.modelPane.add(modelStatus);
		add(modelPane);
		
		// MENU
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(10, 22, 528, 21);
		add(menuBar);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmState = new JMenuItem("State");
		mntmState.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showState(); }
	        });
		mnTools.add(mntmState);
		
		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);
		
		JMenuItem mntmBackgroundColor = new JMenuItem("Background Color");
		mntmBackgroundColor.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showBackgroundColor(); }
	        });
		mnWindow.add(mntmBackgroundColor);
		
		JMenuItem mntmBorderColor = new JMenuItem("Border Color");
		mntmBorderColor.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showBorderColor(); }
	        });
		mnWindow.add(mntmBorderColor);
		
		JMenuItem mntmGridColor = new JMenuItem("Grid Color");
		mntmGridColor.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showGridColor(); }
	        });
		mnWindow.add(mntmGridColor);
		
		JMenuItem mntmTextColor = new JMenuItem("Text Color");
		mntmTextColor.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showTextColor(); }
	        });
		mnWindow.add(mntmTextColor);
		
		JMenuItem mntmTextBackgroundColor = new JMenuItem("Text Background Color");
		mntmTextBackgroundColor.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) { showTextBackgroundColor(); }
	        });
		mnWindow.add(mntmTextBackgroundColor);
	}
	
	IModelViewController getModelViewController(){
		return this.mc.getModelViewController();
	}

	//TODO
	//Board getBoard() {
	//	return this.mc.getBoard();
	//}

	void setModelStatus(String msg){
		this.modelStatus.setText(msg);	
		
	}
	
	// -- TOOLS --
	
	private void showState(){
		// TODO
		String state = "";
		this.mc.setFeedback(state);
	}
	
	// -- WINDOW --
	
	private void showBackgroundColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.setBackground(c);
		}
	}
	
	private void showBorderColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.mvg.setBorderColor(c);
		}
	}
	
	private void showGridColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.mvg.setBackgroundColor(c);
		}
	}
	
	private void showTextColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.modelStatus.setBackground(c);
		}
	}
	
	private void showTextBackgroundColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.modelPane.setBackground(c);
		}
	}
	
	// --
}
