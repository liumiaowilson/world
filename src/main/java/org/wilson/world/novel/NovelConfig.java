package org.wilson.world.novel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ImageManager;

public class NovelConfig {
	private static NovelConfig instance;
	
	private List<String> imageRefPrefixWhitelist = new ArrayList<String>();
	
	private NovelConfig() {
		this.loadImageRefPrefixWhitelist();
	}
	
	private void loadImageRefPrefixWhitelist() {
		String whitelist = ConfigManager.getInstance().getConfig("novel.image_ref.prefix.whitelist", "set");
		if(StringUtils.isNotBlank(whitelist)) {
			for(String item : whitelist.split(",")) {
				this.imageRefPrefixWhitelist.add(item.trim());
			}
		}
	}
	
	public static NovelConfig getInstance() {
		if(instance == null) {
			instance = new NovelConfig();
		}
		
		return instance;
	}
	
	public List<String> getImageRefPrefixWhitelist() {
		return this.imageRefPrefixWhitelist;
	}
	
	public List<String> getImageRefNames() {
		List<String> names = new ArrayList<String>();
		for(String prefix : this.imageRefPrefixWhitelist) {
			names.addAll(ImageManager.getInstance().getImageRefNames(prefix));
		}
		
		return names;
	}
}
