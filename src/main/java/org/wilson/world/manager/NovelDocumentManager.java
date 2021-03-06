package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.image.ImageRef;
import org.wilson.world.model.NovelDocument;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.model.NovelStage;
import org.wilson.world.model.NovelStat;
import org.wilson.world.novel.NovelFragmentPerStage;
import org.wilson.world.novel.NovelRoleImageProvider;
import org.wilson.world.util.FormatUtils;

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
		NovelRole role = NovelRoleManager.getInstance().randomNovelRole(false);
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
			if(!NovelStageManager.getInstance().isAvailableFor(stage, role)) {
				continue;
			}
			
			boolean skip = false;
			if(!NovelStageManager.getInstance().isRequiredStage(stage)) {
				if(DiceManager.getInstance().dice(50)) {
					skip = true;
				}
			}
			
			if(!skip) {
				List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragmentsOfStage(stage.id, false);
				if(!fragments.isEmpty()) {
					List<NovelFragment> availableFragments = new ArrayList<NovelFragment>();
					for(NovelFragment fragment : fragments) {
						if(fragment.isAvailableFor(role)) {
							availableFragments.add(fragment);
						}
					}
					
					if(!availableFragments.isEmpty()) {
						List<NovelFragment> result = new ArrayList<NovelFragment>();
						int count  = 1;
						if(NovelStageManager.getInstance().isMultipleStage(stage)) {
							int max = ConfigManager.getInstance().getConfigAsInt("novel_stage.multiple.max", 3);
							int n = DiceManager.getInstance().random(max);
							if(n < 1) {
								n = 1;
							}
							count = n;
						}
						else {
							count = 1;
						}
						
						while(count-- > 0 && !availableFragments.isEmpty()) {
							NovelFragment fragment = this.getRandomNovelFragment(availableFragments);
							result.add(fragment);
							availableFragments.remove(fragment);
						}
						
						NovelStageManager.getInstance().runPreCode(stage, role);
						
						for(NovelFragment fragment : result) {
							NovelFragmentManager.getInstance().runPreCode(fragment, role);
							doc.fragments.add(fragment);
							NovelFragmentManager.getInstance().runPostCode(fragment, role);
						}
						
						NovelStageManager.getInstance().runPostCode(stage, role);
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
		
		NovelStat stat = new NovelStat();
		stat.docId = doc.id;
		stat.time = System.currentTimeMillis();
		NovelStatManager.getInstance().createNovelStat(stat);
		
		return doc;
	}
	
	/**
	 * Get uneven novel fragment at random
	 * 
	 * @param fragments
	 * @return
	 */
	private NovelFragment getRandomNovelFragment(List<NovelFragment> fragments) {
		if(fragments == null || fragments.isEmpty()) {
			return null;
		}
		
		List<NovelFragment> conditioned = new ArrayList<NovelFragment>();
		List<NovelFragment> unconditioned = new ArrayList<NovelFragment>();
		
		for(NovelFragment fragment : fragments) {
			if(StringUtils.isNotBlank(fragment.condition)) {
				conditioned.add(fragment);
			}
			else {
				unconditioned.add(fragment);
			}
		}
		
		List<NovelFragment> result = null;
		if(!conditioned.isEmpty() && unconditioned.isEmpty()) {
			result = conditioned;
		}
		else if(conditioned.isEmpty() && !unconditioned.isEmpty()) {
			result = unconditioned;
		}
		else {
			if(DiceManager.getInstance().dice(70)) {
				result = conditioned;
			}
			else {
				result = unconditioned;
			}
		}
		
		int n = DiceManager.getInstance().random(result.size());
		return result.get(n);
	}
	
	public int getImageDefaultWidth() {
		return ConfigManager.getInstance().getConfigAsInt("novel.image.width.default", 150);
	}
	
	public int getImageDefaultHeight() {
		return ConfigManager.getInstance().getConfigAsInt("novel.image.height.default", 150);
	}
	
	public boolean getImageDefaultAdjust() {
		return ConfigManager.getInstance().getConfigAsBoolean("novel.image.adjust.default", true);
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
	
	private String addImageToFragment(String text, ImageRef ref) {
		if(ref == null) {
			return text;
		}
		
		String originalUrl = ref.getUrl();
		
		String url = ref.getUrl(originalUrl, this.getImageDefaultWidth(), this.getImageDefaultHeight(), this.getImageDefaultAdjust());
		if(url != null) {
			return "<div><a href=\"" + originalUrl + "\"><img src=\"" + url + "\" align=\"left\"/></a></div>" + text;
		}
		else {
			return text;
		}
	}
	
	private String addImageToRole(String text, ImageRef ref) {
		if(ref == null) {
			return text;
		}
		
		String originalUrl = ref.getUrl();
		
		String url = ref.getUrl(originalUrl, this.getImageDefaultWidth(), this.getImageDefaultHeight(), this.getImageDefaultAdjust());
		if(url != null) {
			return text + "<br/><a href=\"" + originalUrl + "\"><img src=\"" + url + "\"/></a><br/>";
		}
		else {
			return text;
		}
	}
	
	public String toHtml(NovelDocument doc, boolean debug) {
		return this.toHtml(doc, debug, true);
	}
	
	public String toHtml(NovelDocument doc, boolean debug, boolean showImage) {
		if(doc == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("===================================================<br/>");
		if(debug) {
			sb.append("[<a href=\"javascript:jumpTo('novel_role_edit.jsp?id=").append(doc.role.id).append("')\">").append(doc.role.id).append("</a>] ");
		}
		String role_text = FormatUtils.toHtml(doc.role.display) + "<br/>";
		if(showImage) {
			ImageRef ref = ImageManager.getInstance().getImageRef(doc.role.image);
			if(ref == null) {
				NovelRoleImageProvider provider = ExtManager.getInstance().getExtension(NovelRoleImageProvider.class);
				if(provider != null) {
					String image = provider.getImage(doc.role);
					if(StringUtils.isNotBlank(image)) {
						ref = ImageManager.getInstance().getImageRef(image);
					}
				}
			}
			role_text = this.addImageToRole(role_text, ref);
		}
		sb.append(role_text);
		sb.append("===================================================<br/>");
		if(debug) {
			sb.append("<hr/>");
		}
		
		for(NovelFragment fragment : doc.fragments) {
			if(debug) {
				sb.append("[<a href=\"javascript:jumpTo('novel_fragment_edit.jsp?id=").append(fragment.id).append("')\">").append(fragment.id).append("</a>] ");
			}
			String content = NovelFragmentManager.getInstance().toString(fragment, doc.role);
			content = FormatUtils.toHtml(content) + "<br/>";
			if(showImage) {
				ImageRef ref = ImageManager.getInstance().getImageRef(fragment.image);
				if(ref == null) {
					//try to load default image from stage
					NovelStage stage = NovelStageManager.getInstance().getNovelStage(fragment.stageId);
					if(stage != null) {
						ref = ImageManager.getInstance().getImageRef(stage.image);
					}
				}
				content = this.addImageToFragment(content, ref);
			}
			sb.append(content);
			if(debug) {
				sb.append("<hr/>");
			}
		}
		
		return sb.toString();
	}
	
	public Map<String, Double> getNovelRoleInDocStats() {
		List<NovelStat> stats = NovelStatManager.getInstance().getNovelStats();
		if(stats.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map<String, Integer> data = new HashMap<String, Integer>();
		for(NovelStat stat : stats) {
			String docId = stat.docId;
			NovelDocument doc = this.getNovelDocument(docId);
			if(doc == null) {
				continue;
			}
			
			NovelRole role = doc.role;
			Integer count = data.get(role.name);
			if(count == null) {
				count = 0;
			}
			count += 1;
			data.put(role.name, count);
		}
		
		int total = 0;
		for(int count : data.values()) {
			total += count;
		}
		
		Map<String, Double> ret = new HashMap<String, Double>();
		
		if(total != 0) {
			for(Entry<String, Integer> entry : data.entrySet()) {
				String key = entry.getKey();
				int count = entry.getValue();
				ret.put(key, FormatUtils.getRoundedValue(count * 100.0 / total));
			}
		}
		
		return ret;
	}
	
	public List<NovelFragmentPerStage> getNovelFragmentPerStageStats() {
		List<NovelStat> stats = NovelStatManager.getInstance().getNovelStats();
		if(stats.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<NovelFragmentPerStage> ret = new ArrayList<NovelFragmentPerStage>();
		
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		int total = 0;
		for(NovelStat stat : stats) {
			NovelDocument doc = this.getNovelDocument(stat.docId);
			if(doc == null) {
				continue;
			}
			
			for(NovelFragment fragment : doc.fragments) {
				int stageId = fragment.stageId;
				Integer count = data.get(stageId);
				if(count == null) {
					count = 0;
				}
				count += 1;
				data.put(stageId, count);
			}
			
			total += 1;
		}
		
		if(total != 0) {
			NovelStage stage = NovelStageManager.getInstance().getStartStage();
			while(stage != null) {
				if(data.containsKey(stage.id)) {
					int count = data.get(stage.id);
					NovelFragmentPerStage nfps = new NovelFragmentPerStage();
					nfps.id = stage.id;
					nfps.name = stage.name;
					nfps.count = FormatUtils.getRoundedValue(count * 1.0 / total);
					ret.add(nfps);
				}
				
				stage = NovelStageManager.getInstance().getFollowingStage(stage);
			}
		}
		
		return ret;
	}
}
