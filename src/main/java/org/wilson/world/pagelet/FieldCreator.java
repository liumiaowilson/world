package org.wilson.world.pagelet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.PageletManager;
import org.wilson.world.model.Pagelet;
import org.wilson.world.util.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FieldCreator extends PageCreator {
	private static final Logger logger = Logger.getLogger(FieldCreator.class);
	
	private List<FieldInfo> infos = new ArrayList<FieldInfo>();
	
	public FieldCreator(List<FieldInfo> infos) {
		this.setFieldInfos(infos);
	}
	
	public FieldCreator(String json) {
		if(StringUtils.isBlank(json)) {
			return;
		}
		
		try {
			JSONArray array = JSONArray.fromObject(json);
			List<FieldInfo> infos = new ArrayList<FieldInfo>();
			for(int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				FieldInfo info = new FieldInfo();
				if(!(obj.containsKey("name") && obj.containsKey("label") && obj.containsKey("type"))) {
					continue;
				}
				info.name = obj.getString("name");
				info.label = obj.getString("label");
				info.type = obj.getString("type");
				if(obj.containsKey("data")) {
					JSONObject data = obj.getJSONObject("data");
					for(Object keyObj : data.keySet()) {
						String key = (String) keyObj;
						Object value = JSONUtils.convert(data.get(key));
						info.data.put(key, value);
					}
				}
				
				infos.add(info);
			}
			
			this.setFieldInfos(infos);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
	public List<FieldInfo> getFieldInfos() {
		return this.infos;
	}
	
	public void setFieldInfos(List<FieldInfo> infos) {
		this.infos = infos;
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
}
