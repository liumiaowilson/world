package org.wilson.world.pagelet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.PageletManager;
import org.wilson.world.model.Pagelet;

public class PageInterceptor {
	private List<Pagelet> pagelets = new ArrayList<Pagelet>();
	private List<Page> pages = new ArrayList<Page>();
	
	public PageInterceptor(String url) {
		for(Pagelet pagelet : PageletManager.getInstance().getMatchingPagelets(url)) {
			pagelet = PageletManager.getInstance().load(pagelet);
			pagelets.add(pagelet);
		}
	}
	
	public void executeServerCode(HttpServletRequest req, HttpServletResponse resp) {
		for(Pagelet pagelet : pagelets) {
			Page page = PageletManager.getInstance().executeServerCode(pagelet, req, resp);
			this.pages.add(page);
		}
	}
	
	public void renderCSS(Writer out) throws IOException {
		if(out != null) {
			for(Pagelet pagelet : pagelets) {
				if(StringUtils.isNotBlank(pagelet.css)) {
					out.write(pagelet.css);
				}
			}
		}
	}
	
	public void renderHTML(Writer out) throws IOException {
		if(out != null) {
			for(Pagelet pagelet : pagelets) {
				if(StringUtils.isNotBlank(pagelet.html)) {
					out.write(pagelet.html);
				}
			}
		}
	}
	
	public void renderClientScript(Writer out) throws IOException {
		if(out != null) {
			for(int i = 0; i < pagelets.size(); i++) {
				Pagelet pagelet = pagelets.get(i);
				Page page = pages.get(i);
				
				String script = page.getClientScript();
				if(StringUtils.isNotBlank(script)) {
					out.write(script);
					out.write("\n");
					out.write(pagelet.clientCode);
					out.write("\n");
				}
			}
		}
	}
}
