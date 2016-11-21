package org.wilson.world.novel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.NovelRoleManager;
import org.wilson.world.model.NovelRole;
import org.wilson.world.util.FormatUtils;

public class NovelRoleReportBuilderImpl implements NovelRoleReportBuilder {

	@Override
	public List<NovelRoleReport> build() {
		List<NovelRoleReport> reports = new ArrayList<NovelRoleReport>();
		
		List<NovelRole> roles = NovelRoleManager.getInstance().getNovelRoles();
		
		reports.add(this.buildJobReport(roles));
		reports.add(this.buildStatusReport(roles));
		reports.add(this.buildAgeReport(roles));
		
		return reports;
	}

	private NovelRoleReport buildJobReport(List<NovelRole> roles) {
		String name = "job";
		String title = "Novel Role Jobs";
		Map<String, Integer> data = new HashMap<String, Integer>();
		for(NovelRole role : roles) {
			String job = role.get("job");
			if(StringUtils.isBlank(job)) {
				continue;
			}
			Integer i = data.get(job);
			if(i == null) {
				i = 0;
			}
			i += 1;
			data.put(job, i);
		}
		
		return this.buildReport(name, title, data);
	}
	
	private NovelRoleReport buildStatusReport(List<NovelRole> roles) {
		String name = "status";
		String title = "Novel Role Status";
		Map<String, Integer> data = new HashMap<String, Integer>();
		for(NovelRole role : roles) {
			String key = null;
			if(role.is("married")) {
				key = "Married";
			}
			else if(role.is("virgin")) {
				key = "Virgin";
			}
			else {
				key = "Other";
			}
			Integer i = data.get(key);
			if(i == null) {
				i = 0;
			}
			i += 1;
			data.put(key, i);
		}
		
		return this.buildReport(name, title, data);
	}
	
	private NovelRoleReport buildAgeReport(List<NovelRole> roles) {
		String name = "age";
		String title = "Novel Role Ages";
		Map<String, Integer> data = new HashMap<String, Integer>();
		for(NovelRole role : roles) {
			String key = null;
			try {
				int age = Integer.parseInt(role.get("age"));
				if(age <= 20) {
					key = "Young";
				}
				else if(age > 30) {
					key = "Middle-aged";
				}
				else {
					key = "Old";
				}
			}
			catch(Exception e) {
				continue;
			}
			Integer i = data.get(key);
			if(i == null) {
				i = 0;
			}
			i += 1;
			data.put(key, i);
		}
		
		return this.buildReport(name, title, data);
	}
	
	private NovelRoleReport buildReport(String name, String title, Map<String, Integer> data) {
		NovelRoleReport report = new NovelRoleReport();
		report.name = name;
		report.title = title;
		int total = 0;
		for(Integer i : data.values()) {
			total += i;
		}
		if(total != 0) {
			for(Entry<String, Integer> entry : data.entrySet()) {
				String key = entry.getKey();
				int value = entry.getValue();
				report.data.put(key, FormatUtils.getRoundedValue(value * 100.0 / total));
			}
		}
		
		return report;
	}
}
