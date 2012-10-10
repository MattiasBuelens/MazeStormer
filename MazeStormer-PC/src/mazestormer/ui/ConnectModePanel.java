package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ConnectModePanel extends ModePanel{
	
	private static final long serialVersionUID = 6293072438435084809L;
	
	private JButton connectButton;
	
	private JCheckBox bluetoothCheck;
	private JCheckBox usbCheck;

	public ConnectModePanel(MainControl mainControl) throws NullPointerException{
		super(mainControl);
		
		// -- BUTTONS --
		this.connectButton = new JButton("Connect");
		this.connectButton.setBounds(10, 31, 89, 23);
		this.connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ connect(); }
		});
		add(connectButton);
		
		// -- CHECK BOXES --
		this.bluetoothCheck = new JCheckBox("Bluetooth");
		this.bluetoothCheck.setBounds(10, 61, 89, 24);
		this.bluetoothCheck.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bluetoothPreformed();
			}
		});
		add(this.bluetoothCheck);
		
		this.usbCheck = new JCheckBox("USB");
		this.usbCheck.setBounds(10, 88, 89, 24);
		this.usbCheck.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				usbPreformed();
			}
		});
		add(this.usbCheck);
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
