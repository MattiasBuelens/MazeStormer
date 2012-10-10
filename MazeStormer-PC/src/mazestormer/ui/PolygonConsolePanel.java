package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.*;

/**
 * The polygon console panel of the NXT.
 * 
 * @version
 * @author 	Team Bronze	
 *
 */
public class PolygonConsolePanel extends ConsolePanel{

	private static final long serialVersionUID = -4581780372201675533L;
	
	private static final int SIDE_MAX = 10;
	private static final int TURNS_MAX = 60;
	
	private JTextField polygonSide;
	private JTextField turnsField;

	private JButton playButton;
	private JButton stopButton;

	public PolygonConsolePanel(MainControl mainControl) throws NullPointerException{
		super(mainControl);
		setLayout(new MigLayout("", "", ""));
		initiateComponents();
	}
	
	private void initiateComponents(){
		initiateButtons();
		initiateTextFields();
		setInitial();
	}
	
	private void initiateButtons(){
	}
	
	private void initiateTextFields(){
		
		JTextField sideLabel = new JTextField("Sidelength [cm]:");
		sideLabel.setColumns(10);
		sideLabel.setHorizontalAlignment(SwingConstants.LEFT);
		sideLabel.setEditable(false);
		add(sideLabel, "cell 0 0");
		
		this.polygonSide = new JTextField();
		polygonSide.setColumns(5);
		this.polygonSide.setHorizontalAlignment(SwingConstants.RIGHT);
		this.polygonSide.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setPolygonSide(polygonSide.getText());
			}
		});
		add(this.polygonSide, "cell 1 0");
		
		JTextField turnsLabel = new JTextField("Turns:");
		turnsLabel.setColumns(10);
		turnsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		turnsLabel.setEditable(false);
		turnsLabel.setBounds(12, 55, 107, 20);
		add(turnsLabel, "cell 0 1");
		
		this.turnsField = new JTextField();
		turnsField.setColumns(5);
		this.turnsField.setHorizontalAlignment(SwingConstants.RIGHT);
		this.turnsField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setTurns(turnsField.getText());
			}
		});
		add(this.turnsField, "cell 1 1");
		this.playButton = new JButton("");
		this.playButton.setIcon(new MediaPlaybackStartIcon(32,32));
		this.playButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ play(); }
        });
		add(this.playButton, "cell 0 2");
		
		this.stopButton = new JButton("");
		this.stopButton.setIcon(new MediaPlaybackStopIcon(32,32));
		this.stopButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e){ stop(); }
        });
		add(this.stopButton, "cell 0 3");
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

