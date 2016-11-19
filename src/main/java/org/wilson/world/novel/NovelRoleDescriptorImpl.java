package org.wilson.world.novel;

import java.util.Map;

import org.wilson.world.model.NovelRole;

public class NovelRoleDescriptorImpl implements NovelRoleDescriptor {

	@Override
	public String getDescription(NovelRole role) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(role.name);
		sb.append(",");
		sb.append(role.description);
		sb.append(",");
		
		Map<String, String> vars = role.variables;
		
		if(vars.containsKey("age")) {
			sb.append(vars.get("age"));
			sb.append("岁,");
		}
		
		if(vars.containsKey("older")) {
			if(Boolean.valueOf(vars.get("older"))) {
				sb.append("成熟,");
			}
			else {
				sb.append("青春,");
			}
		}
		
		if(vars.containsKey("married")) {
			if(Boolean.valueOf(vars.get("married"))) {
				sb.append("已婚,");
			}
			else {
				sb.append("未婚,");
			}
		}
		
		if(vars.containsKey("single")) {
			if(Boolean.valueOf(vars.get("single"))) {
				sb.append("单身,");
			}
			else {
				sb.append("非单身,");
			}
		}
		
		if(vars.containsKey("virgin")) {
			if(Boolean.valueOf(vars.get("virgin"))) {
				sb.append("处女,");
			}
			else {
				sb.append("非处女,");
			}
		}
		
		if(vars.containsKey("role")) {
			sb.append(vars.get("role"));
			sb.append(",");
		}
		
		if(vars.containsKey("body_wear")) {
			sb.append(vars.get("body_wear"));
			sb.append(",");
		}
		
		if(vars.containsKey("leg_wear")) {
			sb.append(vars.get("leg_wear"));
			sb.append(",");
		}
		
		if(vars.containsKey("foot_wear")) {
			sb.append(vars.get("foot_wear"));
			sb.append(",");
		}
		
		if(vars.containsKey("appearance")) {
			sb.append(vars.get("appearance"));
		}
		
		return sb.toString();
	}

}
