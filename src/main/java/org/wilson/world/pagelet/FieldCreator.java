package org.wilson.world.pagelet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.manager.PageletManager;
import org.wilson.world.model.Pagelet;

import org.apache.commons.lang.StringUtils;

public class FieldCreator extends PageCreator {
	private List<FieldInfo> infos = new ArrayList<FieldInfo>();
	/**
	 * Standalone means that the field creator is not used in a composite page creator, so it needs to handle all the style/script files
	 */
	private boolean standalone = true;

    private List<String> groupNames = new ArrayList<String>();
    private Map<String, List<Page>> groupPages = new HashMap<String, List<Page>>();

	public FieldCreator(List<FieldInfo> infos) {
		this.setFieldInfos(infos);
	}
	
	public FieldCreator(String json) {
		this(PageletManager.getInstance().parseFieldInfos(json));
	}
	
	public List<FieldInfo> getFieldInfos() {
		return this.infos;
	}
	
	public void setFieldInfos(List<FieldInfo> infos) {
		this.infos = infos;
	}
	
	public boolean isStandalone() {
		return this.standalone;
	}
	
	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}
	
	public String executeServerCode(HttpServletRequest req, HttpServletResponse resp) {
		for(FieldInfo info : this.infos) {
			Pagelet pagelet = PageletManager.getInstance().getPagelet(info.type);
			if(pagelet == null || !PageletType.Field.name().equals(pagelet.type)) {
				continue;
			}
			
			pagelet = PageletManager.getInstance().load(pagelet);
			
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("field", info);
			Page page = PageletManager.getInstance().executeServerCode(pagelet, req, resp, context);
			this.getPages().add(page);

            String groupName = (String)info.data.get("group");
            if(groupName != null) {
                if(!this.groupNames.contains(groupName)) {
                    this.groupNames.add(groupName);
                }

                List<Page> gPages = this.groupPages.get(groupName);
                if(gPages == null) {
                    gPages = new ArrayList<Page>();
                    this.groupPages.put(groupName, gPages);
                }
                gPages.add(page);
            }
		}

        if(!this.groupNames.isEmpty()) {
            this.addStyle("/css/jquery.steps.css");
            this.addScript("/js/jquery.steps.js");
        }
		
		return null;
	}
	
	public void renderClientScript(Writer out) throws IOException {
		if(out != null) {
			out.write("var C = {};\n");
			for(FieldInfo info : this.infos) {
				out.write("C['" + info.name + "'] = function() { return $('#" + info.name + "').val(); };\n");
			}
		}
		
		super.renderClientScript(out);
		
		if(out != null) {
			StringBuilder sb = new StringBuilder("var fieldNames = [");
			for(int i = 0; i < this.infos.size(); i++) {
				FieldInfo info = this.infos.get(i);
				sb.append("'").append(info.name).append("'");
				if(i != this.infos.size() - 1) {
					sb.append(",");
				}
			}
			sb.append("];\n");
			out.write(sb.toString());
		}
	}

	@Override
	protected void renderStyleFile(Writer out) throws IOException {
		if(this.isStandalone()) {
			super.renderStyleFile(out);
		}
	}

	@Override
	protected void renderScriptFile(Writer out) throws IOException {
		if(this.isStandalone()) {
			super.renderScriptFile(out);
		}
	}

    private void render(Writer out, List<Page> pages) throws IOException {
        if(pages != null && !pages.isEmpty()) {
            for(Page page : pages) {
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

    @Override
    public void renderHTML(Writer out) throws IOException {
        if(out != null) {
            if(this.groupNames.isEmpty()) {
                this.render(out, this.getPages());
            }
            else {
                for(String groupName : this.groupNames) {
                    List<Page> gPages = this.groupPages.get(groupName);
                    out.write("<h3>" + groupName + "</h3>");
                    out.write("<section>");
                    this.render(out, gPages);
                    out.write("</section>");
                }
            }
        }
    }
}
