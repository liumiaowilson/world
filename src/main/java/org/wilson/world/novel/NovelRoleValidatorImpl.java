package org.wilson.world.novel;

import java.util.Map;

import org.wilson.world.model.NovelRole;

public class NovelRoleValidatorImpl implements NovelRoleValidator {

	@Override
	public String validate(NovelRole role) {
		if(role == null) {
			return null;
		}
		
		Map<String, String> vars = role.variables;
		
		if(vars.containsKey("age") && vars.containsKey("older")) {
			try {
				int age = Integer.parseInt(vars.get("age"));
				boolean older = Boolean.valueOf(vars.get("older"));
				
				if((age <= 20 && older) || (age >= 30 && !older)) {
					return "Conflict between age and older";
				}
			}
			catch(Exception e) {
			}
		}
		
		if(vars.containsKey("married")) {
			boolean married = Boolean.valueOf(vars.get("married"));
			boolean older = Boolean.valueOf(vars.get("older"));
			boolean single = Boolean.valueOf(vars.get("single"));
			boolean virgin = Boolean.valueOf(vars.get("virgin"));
			
			if(married && !older) {
				return "Conflict between married and older";
			}
			
			if(married && single) {
				return "Conflict between married and single";
			}
			
			if(married && virgin) {
				return "Conflict between married and virgin";
			}
		}
		
		return null;
	}

}
