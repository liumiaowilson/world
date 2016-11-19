package org.wilson.world.novel;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.NovelRole;

public interface NovelRoleDescriptor {
    public static final String EXTENSION_POINT = "novel_role.desc";
    
    @Scriptable(name = EXTENSION_POINT, description = "Get the description of the novel role.", params = { "role" })
    public String getDescription(NovelRole role);
}
