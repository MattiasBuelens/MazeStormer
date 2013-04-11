package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.robot.RobotUpdate;

public class UpdateReport extends Report<RobotUpdate> {

	private RobotUpdate update;

	public UpdateReport(ReportType type) {
		this(type, new RobotUpdate());
	}

	public UpdateReport(ReportType type, RobotUpdate update) {
		super(type);
		this.update = update;
	}

	public RobotUpdate getUpdate() {
		return update;
	}

	public void setUpdate(RobotUpdate update) {
		this.update = update;
	}

	@Override
	public RobotUpdate getValue() {
		return getUpdate();
	}

	@Override
	public void setValue(RobotUpdate value) {
		setUpdate(value);
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);

		RobotUpdate update = new RobotUpdate();
		update.loadObject(dis);
		setUpdate(update);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		getUpdate().dumpObject(dos);
	}

}
