package org.wilson.world.report;

public abstract class AbstractReportBuilder implements ReportBuilder {
	private int id;
	private String name;
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}
}
