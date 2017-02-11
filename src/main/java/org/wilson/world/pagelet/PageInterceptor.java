package org.wilson.world.pagelet;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.PageletManager;
import org.wilson.world.model.Pagelet;

public class PageInterceptor extends PageCreator {
	
	public PageInterceptor(String url) {
		List<Pagelet> pagelets = new ArrayList<Pagelet>();
		for(Pagelet pagelet : PageletManager.getInstance().getMatchingPagelets(url)) {
			pagelet = PageletManager.getInstance().load(pagelet);
			pagelets.add(pagelet);
		}
		
		this.addPagelets(pagelets);
	}
	
}
