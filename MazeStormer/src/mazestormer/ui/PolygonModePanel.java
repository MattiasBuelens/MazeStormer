package mazestormer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import com.javarichclient.icon.tango.actions.*;

/**
 * The polygon mode panel of the NXT.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class PolygonModePanel extends JPanel{

	private static final long serialVersionUID = -4581780372201675533L;
	
	private static final int SIDE_MAX = 10;
	private static final int ANGLE_MAX = 360;
	private static final int TURNS_MAX = 10;
	
	private JTextField polygonSide;
	private JTextField polygonAngle;
	private JTextField turnsField;

	private JButton playButton;
	private JButton stopButton;

	public PolygonModePanel(){
		setLayout(null);
		setBounds(10, 465, 832, 134);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// -- TEXT FIELDS --
		this.polygonSide = new JTextField();
		polygonSide.setHorizontalAlignment(SwingConstants.RIGHT);
		this.polygonSide.setBounds(84, 24, 50, 20);
		this.polygonSide.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setPolygonSide(polygonSide.getText());
			}
		});
		add(this.polygonSide);
		
		this.polygonAngle = new JTextField();
		polygonAngle.setHorizontalAlignment(SwingConstants.RIGHT);
		this.polygonAngle.setBounds(84, 55, 50, 20);
		this.polygonAngle.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setPolygonAngle(polygonAngle.getText());
			}
		});
		add(this.polygonAngle);
		
		this.turnsField = new JTextField();
		turnsField.setHorizontalAlignment(SwingConstants.RIGHT);
		this.turnsField.setBounds(84, 86, 50, 20);
		this.turnsField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setTurns(turnsField.getText());
			}
		});
		add(this.turnsField);
		
		// -- NOT EDITABLE TEXT FIELDS --
		JTextField sideLabel = new JTextField("Side");
		sideLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sideLabel.setEditable(false);
		sideLabel.setBounds(24, 24, 50, 20);
		add(sideLabel);
		
		JTextField angleLabel = new JTextField("Angle");
		angleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		angleLabel.setEditable(false);
		angleLabel.setBounds(24, 55, 50, 20);
		add(angleLabel);
		
		JTextField turnsLabel = new JTextField("Turns");
		turnsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		turnsLabel.setEditable(false);
		turnsLabel.setBounds(24, 86, 50, 20);
		add(turnsLabel);
		
		// -- BUTTONS --
		playButton = new JButton("");
		playButton.setBounds(161, 24, 32, 32);
		playButton.setIcon(new MediaPlaybackStartIcon(32,32));
		playButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ play(); }
        });
		add(playButton);
		
		stopButton = new JButton("");
		stopButton.setBounds(200, 24, 32, 32);
		stopButton.setIcon(new MediaPlaybackStopIcon(32,32));
		stopButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ stop(); }
        });
		add(stopButton);
		
		setInitial();
	}
	
	private void setInitial(){
		this.polygonSide.setText(""+0);
		this.polygonAngle.setText(""+0);
		this.turnsField.setText(""+0);
	}
	
	private void setPolygonSide(String input){
		if(input != null){
			Integer s = changeToInteger(input);
			if(s != null && s.intValue() >= 0 && s.intValue() <= SIDE_MAX){
				this.polygonSide.setText(""+s.intValue());
			}
		}
	}
	
	private void setPolygonAngle(String input){
		if(input != null){
			Integer a = changeToInteger(input);
			if(a != null && a.intValue() >= 0 && a.intValue() <= ANGLE_MAX){
				this.polygonAngle.setText(""+a.intValue());
			}
		}
	}
	
	private void setTurns(String input){
		if(input != null){
			Integer t = changeToInteger(input);
			if(t != null && t.intValue() >= 0 && t.intValue() <= TURNS_MAX){
				this.turnsField.setText(""+t.intValue());
			}
		}
	}
	
	private static Integer changeToInteger(String request)
			throws NullPointerException{
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
	
	private void play(){
		setTextFieldsEnabled(false);
	}
	
	private void stop(){
		setTextFieldsEnabled(true);
		setInitial();
	}
	
	private void setTextFieldsEnabled(boolean request){
		this.polygonSide.setEnabled(request);
		this.polygonAngle.setEnabled(request);
		this.turnsField.setEnabled(request);
	}
}
