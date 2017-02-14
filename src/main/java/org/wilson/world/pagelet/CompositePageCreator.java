package org.wilson.world.pagelet;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.file.DataFile;
import org.wilson.world.manager.DataFileManager;
import org.wilson.world.manager.TemplateManager;
import org.wilson.world.model.Pagelet;

public class CompositePageCreator extends PageCreator {
	private List<FieldCreator> creators = new ArrayList<FieldCreator>();
	
	public CompositePageCreator(Pagelet pagelet) {
		super(pagelet);
	}
	
	public String executeServerCode(HttpServletRequest req, HttpServletResponse resp) {
		String next = super.executeServerCode(req, resp);
		for(Page page : this.getPages()) {
			List<FieldInfo> infos = page.getFieldInfos();
			if(!infos.isEmpty()) {
				FieldCreator creator = new FieldCreator(infos);
				creator.setStandalone(false);
				this.creators.add(creator);
				creator.executeServerCode(req, resp);
			}
		}
		
		return next;
	}
	
	public void renderStyles(Writer out) throws IOException {
		if(out != null) {
			List<String> styles = new ArrayList<String>();
			for(Page page : this.getPages()) {
				for(String style : page.getStyles()) {
					if(!styles.contains(style)) {
						styles.add(style);
					}
				}
			}
			for(FieldCreator creator : this.creators) {
				for(Page page : creator.getPages()) {
					for(String style : page.getStyles()) {
						if(!styles.contains(style)) {
							styles.add(style);
						}
					}
				}
			}
			
			for(String style : styles) {
				out.write("<link href=\"" + style + "\" rel=\"stylesheet\">\n");
			}
		}
	}
	
	public void renderCSS(Writer out) throws IOException {
		super.renderCSS(out);
		
		for(FieldCreator creator : this.creators) {
			creator.renderCSS(out);
		}
	}
	
	public void renderHTML(Writer out) throws IOException {
		if(this.creators.isEmpty()) {
			super.renderHTML(out);
		}
		else {
			StringWriter sw = new StringWriter();
			super.renderHTML(sw);
			String content = sw.toString();
			
			StringBuilder sb = new StringBuilder();
			for(FieldCreator creator : this.creators) {
				sw = new StringWriter();
				creator.renderHTML(sw);
				sb.append(sw.toString()).append("\n");
			}
			
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("fieldUIs", sb.toString());
			content = TemplateManager.getInstance().evaluate(content, context);
			
			out.write(content);
		}
	}
	
	public void renderScripts(Writer out) throws IOException {
		if(out != null) {
			List<String> scripts = new ArrayList<String>();
			for(Page page : this.getPages()) {
				for(String script : page.getScripts()) {
					if(!scripts.contains(script)) {
						scripts.add(script);
					}
				}
			}
			
			for(FieldCreator creator : this.creators) {
				for(Page page : creator.getPages()) {
					for(String script : page.getScripts()) {
						if(!scripts.contains(script)) {
							scripts.add(script);
						}
					}
				}
			}
			
			for(String script : scripts) {
				out.write("<script src=\"" + script + "\"></script>\n");
			}
		}
	}
	
	public void renderClientScript(Writer out) throws IOException {
		for(FieldCreator creator : this.creators) {
			creator.renderClientScript(out);
		}
		
		super.renderClientScript(out);
	}

	@Override
	protected void renderStyleFile(Writer out) throws IOException {
		if(out != null) {
			List<String> styleFiles = new ArrayList<String>();
			
			for(Page page : this.getPages()) {
				for(String styleFile : page.getStyleFiles()) {
					if(!styleFiles.contains(styleFile)) {
						styleFiles.add(styleFile);
					}
				}
			}
			
			for(FieldCreator creator : this.creators) {
				for(Page page : creator.getPages()) {
					for(String styleFile : page.getStyleFiles()) {
						if(!styleFiles.contains(styleFile)) {
							styleFiles.add(styleFile);
						}
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

	@Override
	protected void renderScriptFile(Writer out) throws IOException {
		if(out != null) {
			List<String> scriptFiles = new ArrayList<String>();
			
			for(Page page : this.getPages()) {
				for(String scriptFile : page.getScriptFiles()) {
					if(!scriptFiles.contains(scriptFile)) {
						scriptFiles.add(scriptFile);
					}
				}
			}
			
			for(FieldCreator creator : this.creators) {
				for(Page page : creator.getPages()) {
					for(String scriptFile : page.getScriptFiles()) {
						if(!scriptFiles.contains(scriptFile)) {
							scriptFiles.add(scriptFile);
						}
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
}
