package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class PolygonModePanel extends ModePanel{

	private static final long serialVersionUID = -4581780372201675533L;
	
	private static final int SIDE_MAX = 10;
	private static final int TURNS_MAX = 60;
	
	private JTextField polygonSide;
	private JTextField turnsField;

	private JButton playButton;
	private JButton stopButton;

	public PolygonModePanel(MainControl mainControl) throws NullPointerException{
		super(mainControl);
		
		// -- TEXT FIELDS --
		this.polygonSide = new JTextField();
		this.polygonSide.setHorizontalAlignment(SwingConstants.RIGHT);
		this.polygonSide.setBounds(131, 24, 50, 20);
		this.polygonSide.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setPolygonSide(polygonSide.getText());
			}
		});
		add(this.polygonSide);
		
		this.turnsField = new JTextField();
		this.turnsField.setHorizontalAlignment(SwingConstants.RIGHT);
		this.turnsField.setBounds(131, 55, 50, 20);
		this.turnsField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setTurns(turnsField.getText());
			}
		});
		add(this.turnsField);
		
		// -- NOT EDITABLE TEXT FIELDS --
		JTextField sideLabel = new JTextField("Sidelength [cm]:");
		sideLabel.setHorizontalAlignment(SwingConstants.LEFT);
		sideLabel.setEditable(false);
		sideLabel.setBounds(12, 24, 107, 20);
		add(sideLabel);
		
		JTextField turnsLabel = new JTextField("Turns:");
		turnsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		turnsLabel.setEditable(false);
		turnsLabel.setBounds(12, 55, 107, 20);
		add(turnsLabel);
		
		// -- BUTTONS --
		this.playButton = new JButton("");
		this.playButton.setBounds(65, 87, 32, 32);
		this.playButton.setIcon(new MediaPlaybackStartIcon(32,32));
		this.playButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ play(); }
        });
		add(this.playButton);
		
		this.stopButton = new JButton("");
		this.stopButton.setBounds(109, 87, 32, 32);
		this.stopButton.setIcon(new MediaPlaybackStopIcon(32,32));
		this.stopButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ stop(); }
        });
		add(this.stopButton);
		
		setInitial();
	}
	
	private void setInitial(){
		this.polygonSide.setText(""+0);
		this.turnsField.setText(""+0);
		this.stopButton.setVisible(false);
		setTextFieldsEnabled(true);
	}
	
	private void setTextFieldsEnabled(boolean request){
		this.polygonSide.setEnabled(request);
		this.turnsField.setEnabled(request);
	}
	
	private void setPolygonSide(String input){
		if(input != null){
			Integer s = changeToInteger(input);
			if(s != null && s.intValue() >= 0 && s.intValue() <= SIDE_MAX){
				this.polygonSide.setText(""+s.intValue());
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
		this.stopButton.setVisible(true);
	}
	
	private void stop(){
		setInitial();
	}
}
