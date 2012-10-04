package mazestormer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import mazestormer.board.Board;

/**
 * A control panel for 'micro view gui's.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class MicroViewPanel extends JPanel{

	private static final long serialVersionUID = 17L;

	private MainControl mc;
	private MicroViewGUI mvg;

	private JLabel lowerBar;
	private JPanel lowerPane;

	public MicroViewPanel(final MainControl mc){
		this.mc = mc;
		
		setLayout(null);
		setBounds(10, 26, 548, 529);
		
		// MVG
		this.mvg = new MicroViewGUI(this);
		this.mvg.setBounds(10, 43, 528, 441);
		add(mvg);
		
		// MODEL BAR & PANE
		this.lowerBar = new JLabel();
		this.lowerBar.setFont(MainControl.STANDARD_FONT);
		this.lowerPane = new JPanel();
		this.lowerPane.setBounds(10, 495, 528, 24);
		this.lowerPane.add(lowerBar);
		add(lowerPane);
		
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

	Board getBoard() {
		return this.mc.getBoard();
	}

	void setLowerBar(String msg){
		this.lowerBar.setText(msg);	
		
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
	
	private void showTextColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.lowerBar.setBackground(c);
		}
	}
	
	private void showTextBackgroundColor(){
		Color c = MainControl.showColor(this);
		if(c != null){
			this.lowerPane.setBackground(c);
		}
	}
	
	// --
}