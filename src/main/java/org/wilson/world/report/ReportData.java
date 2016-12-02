package org.wilson.world.report;

import java.util.ArrayList;
import java.util.List;

public class ReportData {
	private String title;
	private List<List<String>> rows = new ArrayList<List<String>>();
	
	public ReportData(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void addRow(String ... row) {
		if(row == null) {
			return;
		}
		
		List<String> rowData = new ArrayList<String>();
		for(String item : row) {
			rowData.add(item);
		}
		
		this.rows.add(rowData);
	}
	
	public List<List<String>> getRows() {
		return this.rows;
	}
}
