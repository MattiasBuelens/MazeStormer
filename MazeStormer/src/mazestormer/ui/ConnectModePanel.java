package mazestormer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;

public class ConnectModePanel extends JPanel{
	
	private static final long serialVersionUID = 6293072438435084809L;
	
	private JButton searchButton;
	private JButton connectButton;
	
	private JCheckBox bluetoothCheck;
	private JCheckBox usbCheck;
	
	private JList connectList;

	/**
	 * Create the panel.
	 */
	public ConnectModePanel() {
		setLayout(null);
		setBounds(10, 465, 832, 134);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// -- BUTTONS --
		this.searchButton = new JButton("Search");
		this.searchButton.setBounds(10, 11, 89, 23);
		this.searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ search(); }
		});
		add(this.searchButton);
		
		this.connectButton = new JButton("Connect");
		this.connectButton.setBounds(10, 45, 89, 23);
		this.connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){ connect(); }
		});
		add(connectButton);
		
		// -- CHECK BOXES --
		this.bluetoothCheck = new JCheckBox("Bluetooth");
		this.bluetoothCheck.setBounds(10, 75, 89, 24);
		add(this.bluetoothCheck);
		
		this.usbCheck = new JCheckBox("USB");
		this.usbCheck.setBounds(10, 102, 89, 24);
		add(this.usbCheck);
		
		showConnectList();
	}
	
	private void search(){
		if(this.usbCheck.isSelected()){
			
		}
		if(this.bluetoothCheck.isSelected()){
			
		}
	}
	
	private void connect(){
	}
	
	private void showConnectList(){
		String[] connections = {"1","2","3"};
		
		// -- CHOICE LIST --
		this.connectList = new JList(connections);
		this.connectList.setBounds(109, 13, 150, 110);
		add(this.connectList);
	}
	
	
}
