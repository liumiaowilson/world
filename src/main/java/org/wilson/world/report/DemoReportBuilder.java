package org.wilson.world.report;

public class DemoReportBuilder extends AbstractReportBuilder {
	public DemoReportBuilder() {
		this.setName("demo");
	}

	@Override
	public ReportData build() {
		ReportData data = new ReportData("Demo");
		data.addRow("Name", "Value");
		data.addRow("name", "wilson");
		data.addRow("age", "18");
		
		return data;
	}

}
