package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.javarichclient.icon.tango.actions.*;

import mazestormer.controller.ControlViewController;
import net.miginfocom.swing.MigLayout;

/**
 * A class of control console panels.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ControlConsolePanel extends ConsolePanel{

	private static final long serialVersionUID = 15L;
	
	private static final int MOTOR_MIN = 0;
	private static final int MOTOR_MAX = 100;
	
	private JSpinner speedA;
	private JSpinner speedB;
	private JSpinner speedC;

	public ControlConsolePanel(ControlViewController cvc){
		super(cvc);	
		setLayout(new MigLayout("", "", ""));
		initiateComponents();
	}
	
	private void initiateComponents(){
		initiateButtons();
		initiateSpeeds();
	}
	
	private void initiateButtons(){
		JButton up = new JButton("");
		up.setIcon(new GoUpIcon(32,32));
		up.addActionListener(new ActionListener(){
			@Override
	        public void actionPerformed(ActionEvent e){ moveUp(); }
	    });
		up.registerKeyboardAction(up.getActionListeners()[0],
	            KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false),  
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(up, "cell 1 0,alignx left,aligny top");
		
		JButton right = new JButton("");
		right.setIcon(new GoNextIcon(32,32));
		right.addActionListener(new ActionListener(){
			@Override
	        public void actionPerformed(ActionEvent e){ moveRight(); }
	    });
		right.registerKeyboardAction(right.getActionListeners()[0],
	            KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false),  
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(right, "cell 2 1,alignx left,aligny top");
		
		JButton down = new JButton("");
		down.setIcon(new GoDownIcon(32,32));
		down.addActionListener(new ActionListener(){
			@Override
	        public void actionPerformed(ActionEvent e){ moveDown(); }
	    });
		down.registerKeyboardAction(down.getActionListeners()[0],
	            KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false),  
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(down, "cell 1 1,alignx left,aligny top");
		
		JButton left = new JButton("");
		left.setIcon(new GoPreviousIcon(32,32));
		left.addActionListener(new ActionListener(){
			@Override
	        public void actionPerformed(ActionEvent e){ moveLeft(); }
	    });
		left.registerKeyboardAction(left.getActionListeners()[0],
	            KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false),  
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(left, "cell 0 1,alignx left,aligny top");
		
		JButton stop = new JButton("");
		stop.setIcon(new ProcessStopIcon(32,32));
		stop.addActionListener(new ActionListener(){
			@Override
	        public void actionPerformed(ActionEvent e){ stop(); }
	    });
		stop.registerKeyboardAction(stop.getActionListeners()[0],
	            KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false),  
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		add(stop, "cell 1 2,alignx left,aligny top");
	}
	
	private void initiateSpeeds(){
		JTextField textMotorA = new JTextField();
		textMotorA.setColumns(10);
		textMotorA.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorA.setEditable(false);
		textMotorA.setText("Motor A");
		add(textMotorA, "cell 3 0");
		
		this.speedA = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedA.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.A, ((Integer) speedA.getValue()).intValue()); }
		});
		add(this.speedA, "cell 4 0");
		
		JTextField textMotorB = new JTextField();
		textMotorB.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorB.setEditable(false);
		textMotorB.setText("Motor B");
		textMotorB.setColumns(10);
		add(textMotorB, "cell 3 1");
		
		this.speedB = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedB.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.B, ((Integer) speedB.getValue()).intValue()); }
		});
		add(this.speedB, "cell 4 1");
		
		JTextField textMotorC = new JTextField();
		textMotorC.setColumns(10);
		textMotorC.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorC.setEditable(false);
		textMotorC.setText("Motor C");
		add(textMotorC, "cell 3 2");
		
		this.speedC = new JSpinner(new SpinnerNumberModel(50,MOTOR_MIN,MOTOR_MAX,1));
		this.speedC.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeed(Motor.C, ((Integer) speedC.getValue()).intValue()); }
		});
		add(this.speedC, "cell 4 2");
	}

	private void moveUp(){
		System.out.println("u");
	}
	
	private void moveRight(){
		System.out.println("r");
	}
	
	private void moveLeft(){
		
	}

	private void moveDown(){
	
	}
	
	private void stop(){
		
	}
	
	private void changeSpeed(Motor m, int input){
	}
}

