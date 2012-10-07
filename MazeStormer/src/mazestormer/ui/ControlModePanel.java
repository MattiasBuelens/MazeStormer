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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.javarichclient.icon.tango.actions.*;

/**
 * The manual control panel of the NXT.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ControlModePanel extends JPanel{

	private static final long serialVersionUID = 15L;
	
	private JSlider sliderA;
	private JSlider sliderB;
	private JSlider sliderC;
	private JTextField inputA;
	private JTextField inputB;
	private JTextField inputC;
	private JTextField currentA;
	private JTextField currentB;
	private JTextField currentC;

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
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false),  
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
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false),  
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
                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false),  
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
                KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false),  
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
		
		// -- SLIDERS --
		sliderA = new JSlider();
		sliderA.setPaintTicks(true);
		sliderA.setPaintLabels(true);
		sliderA.setMajorTickSpacing(50);
		sliderA.setMinorTickSpacing(10);
		sliderA.setToolTipText("");
		sliderA.setBounds(445, 2, 200, 43);
		sliderA.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeedThroughSlider(Motor.A, sliderA.getValue()); }
		});
		add(sliderA);
		
		sliderB = new JSlider();
		sliderB.setMajorTickSpacing(50);
		sliderB.setPaintLabels(true);
		sliderB.setPaintTicks(true);
		sliderB.setMinorTickSpacing(10);
		sliderB.setToolTipText("");
		sliderB.setBounds(445, 47, 200, 43);
		sliderB.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeedThroughSlider(Motor.B, sliderB.getValue()); }
		});
		add(sliderB);
		
		sliderC = new JSlider();
		sliderC.setMajorTickSpacing(50);
		sliderC.setMinorTickSpacing(10);
		sliderC.setPaintLabels(true);
		sliderC.setPaintTicks(true);
		sliderC.setBounds(445, 90, 200, 43);
		sliderC.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) { changeSpeedThroughSlider(Motor.C, sliderC.getValue()); }
		});
		add(sliderC);
		
		// -- TEXTS --
		inputA = new JTextField();
		inputA.setHorizontalAlignment(SwingConstants.CENTER);
		inputA.setBounds(668, 18, 49, 20);
		inputA.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSpeedThroughInput(Motor.A, inputA.getText());
			}
		});
		add(inputA);
		inputA.setColumns(10);
		
		inputB = new JTextField();
		inputB.setHorizontalAlignment(SwingConstants.CENTER);
		inputB.setBounds(668, 61, 49, 20);
		inputB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSpeedThroughInput(Motor.B, inputB.getText());
			}
		});
		add(inputB);
		inputB.setColumns(10);
		
		inputC = new JTextField();
		inputC.setHorizontalAlignment(SwingConstants.CENTER);
		inputC.setBounds(668, 102, 49, 20);
		inputC.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSpeedThroughInput(Motor.C, inputC.getText());
			}
		});
		add(inputC);
		inputC.setColumns(10);
		
		currentA = new JTextField();
		currentA.setHorizontalAlignment(SwingConstants.CENTER);
		currentA.setText("50");
		currentA.setEditable(false);
		currentA.setColumns(10);
		currentA.setBounds(394, 18, 49, 20);
		add(currentA);
		
		currentB = new JTextField();
		currentB.setHorizontalAlignment(SwingConstants.CENTER);
		currentB.setText("50");
		currentB.setEditable(false);
		currentB.setColumns(10);
		currentB.setBounds(394, 61, 49, 20);
		add(currentB);
		
		currentC = new JTextField();
		currentC.setHorizontalAlignment(SwingConstants.CENTER);
		currentC.setText("50");
		currentC.setEditable(false);
		currentC.setColumns(10);
		currentC.setBounds(394, 102, 49, 20);
		add(currentC);
		
		JTextField textMotorA = new JTextField();
		textMotorA.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorA.setEditable(false);
		textMotorA.setText("A");
		textMotorA.setColumns(10);
		textMotorA.setBounds(350, 18, 32, 20);
		add(textMotorA);
		
		JTextField textMotorB = new JTextField();
		textMotorB.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorB.setEditable(false);
		textMotorB.setText("B");
		textMotorB.setColumns(10);
		textMotorB.setBounds(350, 61, 32, 20);
		add(textMotorB);
		
		JTextField textMotorC = new JTextField();
		textMotorC.setHorizontalAlignment(SwingConstants.CENTER);
		textMotorC.setEditable(false);
		textMotorC.setText("C");
		textMotorC.setColumns(10);
		textMotorC.setBounds(350, 102, 32, 20);
		add(textMotorC);
		
		setInitial();
	}
	
	private void setInitial(){
		this.inputA.setText("");
		this.inputB.setText("");
		this.inputC.setText("");
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
	
	private void changeSpeedThroughSlider(Motor m, int speed){
		if(m == Motor.A)
			this.currentA.setText(""+speed);
		else if(m == Motor.B)
			this.currentB.setText(""+speed);
		else if(m == Motor.C)
			this.currentC.setText(""+speed);
	}
	
	private void changeSpeedThroughInput(Motor m, String input){		
		Integer s = changeToDigit(input);
		int speed = 0;
		boolean change = false;
		if(s != null && s.intValue() >= sliderA.getMinimum() && s.intValue() <= sliderA.getMaximum()){
			change = true;
			speed = s.intValue();
		}
		
		setInitial();
		
		if(change){
			if(m == Motor.A)
				this.sliderA.setValue(speed);
			else if(m == Motor.B)
				this.sliderB.setValue(speed);
			else if(m == Motor.C)
				this.sliderC.setValue(speed);
		}
	}
	
	private static Integer changeToDigit(String request) throws NullPointerException{
        if(request == null)
        	throw new NullPointerException("The given request may not refer the null reference.");
		String cleanedString = "";
        for(int i=0; i<request.length(); i++){
            char k = request.charAt(i);
            if(Character.isDigit(k))
                cleanedString += k;
        }
        if(cleanedString.length() == 0)
          	return null;
        return Integer.parseInt(cleanedString);
    }
}
