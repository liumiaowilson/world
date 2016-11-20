package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.NovelDocument;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.model.NovelStage;

public class NovelDocumentManager {
	private static NovelDocumentManager instance;
	
	private NovelDocumentManager() {
		
	}
	
	public static NovelDocumentManager getInstance() {
		if(instance == null) {
			instance = new NovelDocumentManager();
		}
		
		return instance;
	}
	
	public NovelDocument getNovelDocument(String id) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		String [] items = id.split("-");
		if(items.length == 0) {
			return null;
		}
		
		NovelDocument doc = new NovelDocument();
		doc.id = id;
		
		int roleId = -1;
		try {
			roleId = Integer.parseInt(items[0]);
		}
		catch(Exception e) {
		}
		if(roleId < 0) {
			return null;
		}
		NovelRole role = NovelRoleManager.getInstance().getNovelRole(roleId);
		if(role == null) {
			return null;
		}
		doc.role = role;
		
		for(int i = 1; i < items.length; i++) {
			try {
				NovelFragment fragment = NovelFragmentManager.getInstance().getNovelFragment(Integer.parseInt(items[i]));
				if(fragment != null) {
					doc.fragments.add(fragment);
				}
			}
			catch(Exception e) {
			}
		}
		
		return doc;
	}
	
	public NovelDocument generateNovelDocument() {
		NovelRole role = NovelRoleManager.getInstance().randomNovelRole();
		if(role == null) {
			throw new DataException("No novel role could be found");
		}
		
		NovelStage start = NovelStageManager.getInstance().getStartStage();
		if(start == null) {
			throw new DataException("No novel stage could be found");
		}
		
		NovelDocument doc = new NovelDocument();
		doc.name = "Story of " + role.name;
		doc.role = role;
		
		NovelVariableManager.getInstance().resetRuntimeVars();
		NovelStage stage = start;
		while(stage != null) {
			boolean skip = false;
			if(!NovelStageManager.getInstance().isRequiredStage(stage)) {
				if(DiceManager.getInstance().dice(50)) {
					skip = true;
				}
			}
			
			if(!skip) {
				List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragmentsOfStage(stage.id);
				if(!fragments.isEmpty()) {
					List<NovelFragment> availableFragments = new ArrayList<NovelFragment>();
					for(NovelFragment fragment : fragments) {
						if(fragment.isAvailableFor(role)) {
							availableFragments.add(fragment);
						}
					}
					
					if(!availableFragments.isEmpty()) {
						List<NovelFragment> result = new ArrayList<NovelFragment>();
						if(NovelStageManager.getInstance().isMultipleStage(stage)) {
							int max = ConfigManager.getInstance().getConfigAsInt("novel_stage.multiple.max", 3);
							int n = DiceManager.getInstance().random(max);
							if(n < 1) {
								n = 1;
							}
							result.addAll(DiceManager.getInstance().random(availableFragments, n));
						}
						else {
							int n = DiceManager.getInstance().random(availableFragments.size());
							NovelFragment fragment = availableFragments.get(n);
							result.add(fragment);
						}
						
						for(NovelFragment fragment : result) {
							NovelFragmentManager.getInstance().runPreCode(fragment, role);
							doc.fragments.add(fragment);
							NovelFragmentManager.getInstance().runPostCode(fragment, role);
						}
					}
				}
			}
			
			stage = NovelStageManager.getInstance().getFollowingStage(stage);
		}
		
		StringBuilder id = new StringBuilder();
		id.append(role.id);
		for(NovelFragment fragment : doc.fragments) {
			id.append("-").append(fragment.id);
		}
		doc.id = id.toString();
		
		return doc;
	}
	
	public String toString(NovelDocument doc) {
		if(doc == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(NovelFragment fragment : doc.fragments) {
			sb.append(NovelFragmentManager.getInstance().toString(fragment, doc.role));
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public String toHtml(NovelDocument doc) {
		return this.toHtml(doc, false);
	}
	
	public String toHtml(NovelDocument doc, boolean debug) {
		if(doc == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("===================================================<br/>");
		if(debug) {
			sb.append("[").append(doc.role.id).append("] ");
		}
		sb.append(doc.role.display);
		sb.append("<br/>");
		sb.append("===================================================<br/>");
		sb.append("<hr/>");
		
		for(NovelFragment fragment : doc.fragments) {
			if(debug) {
				sb.append("[").append(fragment.id).append("] ");
			}
			sb.append(NovelFragmentManager.getInstance().toString(fragment, doc.role));
			sb.append("<br/>");
			sb.append("<hr/>");
		}
		
		return sb.toString();
	}
}
