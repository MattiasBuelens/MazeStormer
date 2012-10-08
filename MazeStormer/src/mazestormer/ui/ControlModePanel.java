package mazestormer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.javarichclient.icon.tango.actions.*;

/**
 * The manual control mode panel of the NXT.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ControlModePanel extends JPanel{

	private static final long serialVersionUID = 15L;
	
	private static final int MOTOR_MIN = 0;
	private static final int MOTOR_MAX = 100;
	
	private JSpinner speedA;
	private JSpinner speedB;
	private JSpinner speedC;

	public ControlModePanel(){
		setLayout(null);
		setBounds(10, 465, 832, 134);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// -- BUTTONS --
		JButton up = new JButton("");
		up.setBounds(59,31,32,32);
		up.setIcon(new GoUpIcon(32,32));
		up.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ moveUp(); }
        });
		up.registerKeyboardAction(up.getActionListeners()[0],
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),  
                JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(up);
		
		JButton left = new JButton("");
		left.setBounds(28,61,32,32);
		left.setIcon(new GoPreviousIcon(32,32));
		left.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ moveLeft(); }
        });
		left.registerKeyboardAction(left.getActionListeners()[0],
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),  
                JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(left);
		
		JButton right = new JButton("");
		right.setBounds(90,61,32,32);
		right.setIcon(new GoNextIcon(32,32));
		right.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ moveRight(); }
        });
		right.registerKeyboardAction(right.getActionListeners()[0],
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),  
                JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(right);
		
		JButton down = new JButton("");
		down.setBounds(59,90,32,32);
		down.setIcon(new GoDownIcon(32,32));
		down.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ moveDown(); }
        });
		down.registerKeyboardAction(down.getActionListeners()[0],
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),  
                JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(down);
		
		JButton clockwise = new JButton("");
		clockwise.setBounds(215,31,32,32);
		clockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat_01.png")));
		clockwise.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ turnClockwise(); }
        });
		add(clockwise);
		
		JButton counterClockwise = new JButton("");
		counterClockwise.setBounds(171,31,32,32);
		counterClockwise.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/Repeat.png")));
		counterClockwise.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ turnCounterClockwise(); }
        });
		add(counterClockwise);
		
		JButton ultraSonicSensor = new JButton("");
		ultraSonicSensor.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/earth.png")));
		ultraSonicSensor.setBounds(171, 75, 32, 32);
		ultraSonicSensor.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ scanUltraSonicSensor(); }
        });
		add(ultraSonicSensor);
		
		JButton lightSensor = new JButton("");
		lightSensor.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/object_15.png")));
		lightSensor.setBounds(215, 75, 32, 32);
		lightSensor.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ scanLightSensor(); }
        });
		add(lightSensor);
		
		JButton soundSensor = new JButton("");
		soundSensor.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/music_01.png")));
		soundSensor.setBounds(259, 75, 32, 32);
		soundSensor.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ scanSoundSensor(); }
        });
		add(soundSensor);
		
		JButton action = new JButton("");
		action.setIcon(new ImageIcon(ControlModePanel.class.getResource("/res/images/ui/star.png")));
		action.setBounds(259, 31, 32, 32);
		action.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ showActions(); }
        });
		add(action);
		
		// -- TEXTS --
		JTextField textMotorA = new JTextField();
		textMotorA.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorA.setEditable(false);
		textMotorA.setText("Motor A");
		textMotorA.setColumns(10);
		textMotorA.setBounds(350, 18, 50, 20);
		add(textMotorA);
		
		JTextField textMotorB = new JTextField();
		textMotorB.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorB.setEditable(false);
		textMotorB.setText("Motor B");
		textMotorB.setColumns(10);
		textMotorB.setBounds(350, 61, 50, 20);
		add(textMotorB);
		
		JTextField textMotorC = new JTextField();
		textMotorC.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorC.setEditable(false);
		textMotorC.setText("Motor C");
		textMotorC.setColumns(10);
		textMotorC.setBounds(350, 102, 50, 20);
		add(textMotorC);
		
		// -- SPINNERS --
		this.speedA = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedA.setBounds(412, 18, 50, 20);
		this.speedA.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.A, ((Integer) speedA.getValue()).intValue()); }
		});
		add(this.speedA);
		
		this.speedB = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedB.setBounds(412, 61, 50, 20);
		this.speedB.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.B, ((Integer) speedB.getValue()).intValue()); }
		});
		add(this.speedB);
		
		this.speedC = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedC.setBounds(412, 102, 50, 20);
		this.speedC.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.C, ((Integer) speedC.getValue()).intValue()); }
		});
		add(this.speedC);
	}
	
	private void moveUp(){
		System.out.println("u");
	}
	
	private void moveLeft(){
		
	}
	
	private void moveRight(){
		System.out.println("r");
	}

	private void moveDown(){
	
	}
	
	private void turnClockwise(){
		
	}
	
	private void turnCounterClockwise(){
		
	}
	
	private void scanUltraSonicSensor(){
		
	}
	
	private void scanLightSensor(){
		
	}
	
	private void scanSoundSensor(){
		
	}
	
	private void showActions(){
		
	}
	
	private void changeSpeed(Motor m, int input){
	}
}
