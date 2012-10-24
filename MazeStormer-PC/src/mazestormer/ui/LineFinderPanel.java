package mazestormer.ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

public class LineFinderPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;
	private final ILineFinderController controller;
	private JPanel container;
	private SpinnerNumberModel calibrateModel;

	public LineFinderPanel(ILineFinderController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Linefinder control",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow][grow][right]", "[][][]"));
		add(this.container);
		
		createCalibrate();
	}
	
	private void createCalibrate() {
		this.calibrateModel = new SpinnerNumberModel(new Integer(0), new Integer(0),
				new Integer(1023), new Integer(1));
		
		JLabel lblCalibrate = new JLabel("Calibrate: ");
		container.add(lblCalibrate, "cell 0 0,grow");

		JSpinner spinNbSides = new JSpinner();
		spinNbSides.setModel(nbSidesModel);
		container.add(spinNbSides, "cell 1 0,grow");
		
		JLabel lblUnit = new JLabel("sides");
		container.add(lblUnit, "cell 2 0,grow");
	}


}
