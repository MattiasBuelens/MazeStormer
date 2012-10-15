package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import mazestormer.controller.ConnectViewController;
import net.miginfocom.swing.MigLayout;

/**
 * A class of connect console panels.
 * 
 * @version
 * @author Team Bronze
 *
 */
public class ConnectConsolePanel extends ConsolePanel{
	
	private static final long serialVersionUID = 6293072438435084809L;
	
	private JButton connectButton;
	
	private JCheckBox bluetoothCheck;
	private JCheckBox usbCheck;

	public ConnectConsolePanel(ConnectViewController cvc) throws NullPointerException{
		super(cvc);
		setLayout(new MigLayout("", "", ""));
		initiateComponents();
	}
	
	private void initiateComponents(){
		initiateButtons();
		initiateCheckBoxes();
	}
	
	private void initiateButtons(){
		this.connectButton = new JButton("Connect");
		this.connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ connect(); }
		});
		add(connectButton, "cell 0 0,alignx left,aligny top");
	}
	
	private void initiateCheckBoxes(){
		this.bluetoothCheck = new JCheckBox("Bluetooth");
		this.bluetoothCheck.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bluetoothPreformed();
			}
		});
		add(this.bluetoothCheck, "cell 0 1,alignx left,aligny center");
		
		this.usbCheck = new JCheckBox("USB");
		this.usbCheck.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				usbPreformed();
			}
		});
		add(this.usbCheck, "cell 0 2,alignx left,aligny center");
	}
	
	private void connect(){
		
	}
	
	private void bluetoothPreformed(){
		if(this.bluetoothCheck.isSelected())
				this.usbCheck.setEnabled(false);
			else
				this.usbCheck.setEnabled(true);
	}
	
	private void usbPreformed(){
		if(this.usbCheck.isSelected())
			this.bluetoothCheck.setEnabled(false);
		else
			this.bluetoothCheck.setEnabled(true);
	}
}
