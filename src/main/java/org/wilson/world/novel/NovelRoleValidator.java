package org.wilson.world.novel;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.NovelRole;

public interface NovelRoleValidator {
    public static final String EXTENSION_POINT = "novel_role.validate";
    
    @Scriptable(name = EXTENSION_POINT, description = "Validate the novel role.", params = { "role" })
    public String validate(NovelRole role);
}
