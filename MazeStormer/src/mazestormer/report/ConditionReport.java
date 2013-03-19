package mazestormer.report;

public class ConditionReport extends RequestReport<Void> {

	public ConditionReport(ReportType type) {
		super(type);
	}

	@Override
	public Void getValue() {
		return null;
	}

	@Override
	public void setValue(Void value) {
	}

}
