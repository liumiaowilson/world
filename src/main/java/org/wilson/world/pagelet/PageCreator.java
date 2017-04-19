package org.wilson.world.pagelet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.file.DataFile;
import org.wilson.world.manager.DataFileManager;
import org.wilson.world.manager.PageletManager;
import org.wilson.world.model.Pagelet;

public class PageCreator {
	private List<Pagelet> pagelets = new ArrayList<Pagelet>();
	private List<Page> pages = new ArrayList<Page>();
	
	public PageCreator() {
	}
	
	public PageCreator(List<Pagelet> pagelets) {
		this.addPagelets(pagelets);
	}
	
	public PageCreator(Pagelet pagelet) {
		this.addPagelets(Arrays.asList(pagelet));
	}
	
	public void addPagelets(List<Pagelet> pagelets) {
		this.pagelets.addAll(pagelets);
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
	
	public void renderStyles(Writer out) throws IOException {
		if(out != null) {
			List<String> styles = new ArrayList<String>();
			for(Page page : this.pages) {
				for(String style : page.getStyles()) {
					if(!styles.contains(style)) {
						styles.add(style);
					}
				}
			}
			
			for(String style : styles) {
				out.write("<link href=\"" + style + "\" rel=\"stylesheet\">\n");
			}
		}
	}
	
	protected void renderStyleFile(Writer out) throws IOException {
		if(out != null) {
			List<String> styleFiles = new ArrayList<String>();
			for(Page page : this.pages) {
				for(String styleFile : page.getStyleFiles()) {
					if(!styleFiles.contains(styleFile)) {
						styleFiles.add(styleFile);
					}
				}
			}
			
			for(String styleFile : styleFiles) {
				DataFile file = DataFileManager.getInstance().getDataFile(styleFile);
				if(file != null) {
					String content = file.getContent();
					out.write(content + "\n");
				}
			}
		}
	}
	
	public void renderCSS(Writer out) throws IOException {
		if(out != null) {
			this.renderStyleFile(out);
			
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
                    for(Map.Entry<String, String> entry : page.getReplaces().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        html = html.replaceAll("%" + key + "%", value);
                    }
					out.write(html);
				}
			}
		}
	}
	
	public void renderScripts(Writer out) throws IOException {
		if(out != null) {
			List<String> scripts = new ArrayList<String>();
			for(Page page : this.pages) {
				for(String script : page.getScripts()) {
					if(!scripts.contains(script)) {
						scripts.add(script);
					}
				}
			}
			
			for(String script : scripts) {
				out.write("<script src=\"" + script + "\"></script>\n");
			}
		}
	}
	
	protected void renderScriptFile(Writer out) throws IOException {
		if(out != null) {
			List<String> scriptFiles = new ArrayList<String>();
			for(Page page : this.pages) {
				for(String scriptFile : page.getScriptFiles()) {
					if(!scriptFiles.contains(scriptFile)) {
						scriptFiles.add(scriptFile);
					}
				}
			}
			
			for(String scriptFile : scriptFiles) {
				DataFile file = DataFileManager.getInstance().getDataFile(scriptFile);
				if(file != null) {
					String content = file.getContent();
					out.write(content + "\n");
				}
			}
		}
	}
	
	public void renderClientScript(Writer out) throws IOException {
		if(out != null) {
			this.renderScriptFile(out);
			
			for(Page page : this.pages) {
				String script = page.getClientScript();
				if(StringUtils.isNotBlank(script)) {
					out.write(script);
					out.write("\n");
				}
				
				String clientCode = page.getClientCode();
				if(StringUtils.isNotBlank(clientCode)) {
					out.write(clientCode);
					out.write("\n");
				}
			}
		}
	}
	
	public List<Page> getPages() {
		return this.pages;
	}
}
