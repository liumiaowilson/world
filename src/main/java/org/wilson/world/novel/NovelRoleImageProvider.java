package org.wilson.world.novel;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.NovelRole;

public interface NovelRoleImageProvider {
    public static final String EXTENSION_POINT = "novel_role.image";
    
    @Scriptable(name = EXTENSION_POINT, description = "Get the image of the novel role.", params = { "role" })
    public String getImage(NovelRole role);
}
