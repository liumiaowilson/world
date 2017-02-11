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
	
	public PageInterceptor(List<Pagelet> pagelets) {
		this.pagelets.addAll(pagelets);
	}
	
	public PageInterceptor(Pagelet pagelet) {
		this.pagelets.add(pagelet);
	}
	
	public String getNext() {
		for(Page page : this.pages) {
			String next = page.getNext();
			if(StringUtils.isNotBlank(next)) {
				return next;
			}
		}
		
		return null;
	}
	
	public String executeServerCode(HttpServletRequest req, HttpServletResponse resp) {
		for(Pagelet pagelet : pagelets) {
			Page page = PageletManager.getInstance().executeServerCode(pagelet, req, resp);
			this.pages.add(page);
		}
		
		return this.getNext();
	}
	
	public void renderCSS(Writer out) throws IOException {
		if(out != null) {
			for(Page page : this.pages) {
				String css = page.getCss();
				if(StringUtils.isNotBlank(css)) {
					out.write(css);
				}
			}
		}
	}
	
	public void renderHTML(Writer out) throws IOException {
		if(out != null) {
			for(Page page : this.pages) {
				String html = page.getHtml();
				if(StringUtils.isNotBlank(html)) {
					out.write(html);
				}
			}
		}
	}
	
	public void renderClientScript(Writer out) throws IOException {
		if(out != null) {
			for(Page page : this.pages) {
				String script = page.getClientScript();
				if(StringUtils.isNotBlank(script)) {
					out.write(script);
					out.write("\n");
					String clientCode = page.getClientCode();
					if(StringUtils.isNotBlank(clientCode)) {
						out.write(clientCode);
						out.write("\n");
					}
				}
			}
		}
	}
}
