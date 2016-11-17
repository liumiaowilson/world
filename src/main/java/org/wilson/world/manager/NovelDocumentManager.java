package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.exception.DataException;
import org.wilson.world.model.NovelDocument;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.model.NovelStage;

public class NovelDocumentManager {
	public static final String SPLITTER = "======";
	
	private static NovelDocumentManager instance;
	
	private static int GLOBAL_ID = 1;
	
	private NovelDocumentManager() {
		
	}
	
	public static NovelDocumentManager getInstance() {
		if(instance == null) {
			instance = new NovelDocumentManager();
		}
		
		return instance;
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
		doc.id = GLOBAL_ID++;
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
						int n = DiceManager.getInstance().random(availableFragments.size());
						NovelFragment fragment = availableFragments.get(n);
						
						NovelFragmentManager.getInstance().runPreCode(fragment, role);
						doc.fragments.add(fragment);
						NovelFragmentManager.getInstance().runPostCode(fragment, role);
					}
				}
			}
			
			stage = NovelStageManager.getInstance().getFollowingStage(stage);
		}
		
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
			sb.append(SPLITTER);
		}
		
		return sb.toString();
	}
	
	public String toHtml(NovelDocument doc) {
		String str = this.toString(doc);
		if(str == null) {
			return null;
		}
		
		str = str.replaceAll("\n", "<br/>");
		str = str.replaceAll(SPLITTER, "<hr/>");
		return str;
	}
}
