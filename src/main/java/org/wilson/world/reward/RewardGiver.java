package org.wilson.world.reward;

import org.wilson.world.event.Event;
import org.wilson.world.ext.Scriptable;

public interface RewardGiver {
    public static final String EXTENSION_POINT = "reward_give";
    
    @Scriptable(name = EXTENSION_POINT, description = "Policy to give rewards. Configure a list of these in the config item [reward_giver.extensions] instead of binding them to the extension point.", params = { "event" })
    public void giveReward(Event event);
}
