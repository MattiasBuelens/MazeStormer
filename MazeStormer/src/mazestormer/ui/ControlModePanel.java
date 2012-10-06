package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;

/**
 * The control panel of the NXT.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ControlModePanel extends JPanel{

	private static final long serialVersionUID = 15L;

	public ControlModePanel(){
		setLayout(null);
		setBounds(10, 465, 832, 134);
		
		
		// -- BUTTONS --
		JButton up = new JButton("");
		up.setLocation(59, 31);
		up.setSize(32, 32);
		up.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/arrow_up.png")));
		up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moveUp(); }
        });
		add(up);
		
		JButton left = new JButton("");
		left.setLocation(28, 61);
		left.setSize(32, 32);
		left.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/arrow_left.png")));
		left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moveLeft(); }
        });
		add(left);
		
		JButton right = new JButton("");
		right.setLocation(90, 61);
		right.setSize(32, 32);
		right.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/arrow_right.png")));
		right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moveRight(); }
        });
		add(right);
		
		JButton down = new JButton("");
		down.setLocation(59, 90);
		down.setSize(32, 32);
		down.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/arrow_down.png")));
		down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moveDown(); }
        });
		add(down);
		
		JButton clockwise = new JButton("");
		clockwise.setLocation(215, 31);
		clockwise.setSize(32, 32);
		clockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat_01.png")));
		clockwise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { turnClockwise(); }
        });
		add(clockwise);
		
		JButton counterClockwise = new JButton("");
		counterClockwise.setLocation(171, 31);
		counterClockwise.setSize(32, 32);
		counterClockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat.png")));
		counterClockwise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { turnCounterClockwise(); }
        });
		add(counterClockwise);
		
		// -- SLIDERS --
		JSlider sliderA = new JSlider();
		sliderA.setToolTipText("");
		sliderA.setBounds(445, 22, 200, 16);
		add(sliderA);
		
		JSlider sliderB = new JSlider();
		sliderB.setToolTipText("");
		sliderB.setBounds(445, 64, 200, 16);
		add(sliderB);
		
		JSlider sliderC = new JSlider();
		sliderC.setBounds(445, 106, 200, 16);
		add(sliderC);
	}
	
	private void moveUp(){
		
	}
	
	private void moveLeft(){
		
	}
	
	private void moveRight(){
		
	}

	private void moveDown(){
	
	}
	
	private void turnClockwise(){
		
	}
	
	private void turnCounterClockwise(){
		
	}
}
