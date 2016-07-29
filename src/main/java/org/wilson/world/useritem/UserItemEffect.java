package org.wilson.world.useritem;

import org.wilson.world.ext.Scriptable;

public interface UserItemEffect {
    public static final String EXTENSION_POINT = "item.effect";
    
    @Scriptable(name = EXTENSION_POINT, description = "User item takes effect. Assign the value of this action to user item data.", params = { })
    public void takeEffect();
}
