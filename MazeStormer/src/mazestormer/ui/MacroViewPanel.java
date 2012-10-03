package mazestormer.ui;

import javax.swing.JPanel;

import mazestormer.board.Board;
import mazestormer.facade.IFacade;
import javax.swing.JLabel;

public class MacroViewPanel extends JPanel{
	
	private static final long serialVersionUID = 16L;
	
	private MainControl mc;
	private MacroViewGUI mvg;

	private JLabel modelStatus;
	private JPanel modelPane;

	/**
	 * Create the panel.
	 */
	public MacroViewPanel(final MainControl mc){
		this.mc = mc;
		
		setLayout(null);
		setBounds(10, 26, 548, 529);
		
		// MVG
		this.mvg = new MacroViewGUI(this);
		this.mvg.setBounds(10, 11, 528, 473);
		add(mvg);
		
		// MODEL BAR & PANE
		this.modelStatus = new JLabel();
		//TODO: this.modelStatus.setFont(font);
		this.modelPane = new JPanel();
		this.modelPane.setBounds(10, 454, 528, 24);
		this.modelPane.add(modelStatus);
		add(modelPane);
	}
	
	IFacade getFacade(){
		return this.mc.getFacade();
	}

	Board getBoard() {
		return this.mc.getBoard();
	}

	void setModelStatus(String msg){
		this.modelStatus.setText(msg);	
		
	}
}
