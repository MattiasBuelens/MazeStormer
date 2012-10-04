package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.ImageIcon;

/**
 * The control panel of the NXT.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ControlModePanel extends ControlPanel{

	private static final long serialVersionUID = 15L;

	public ControlModePanel(){
		setLayout(null);
		setBounds(10, 26, 548, 529);
		
		JButton move = new JButton("");
		move.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/arrow_up.png")));
		move.setBounds(126, 247, 89, 53);
		move.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showMove(); }
        });
		add(move);
		
		JButton turnCounterClockwise = new JButton("");
		turnCounterClockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat.png")));
		turnCounterClockwise.setBounds(27, 247, 89, 53);
		turnCounterClockwise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showTurnCounterClockwise(); }
        });
		add(turnCounterClockwise);
		
		JButton turnClockwise = new JButton("");
		turnClockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat_01.png")));
		turnClockwise.setBounds(225, 247, 89, 53);
		turnClockwise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showTurnClockwise(); }
        });
		add(turnClockwise);
	}
	
	private void showMove(){
		
	}
	
	private void showTurnCounterClockwise(){
		
	}
	
	private void showTurnClockwise(){
		
	}
}
