package org.wilson.world.web;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.WebManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RandomNounsJob extends SystemWebJob {
    public static final String RANDOM_NOUNS = "random_nouns";

    public RandomNounsJob() {
        this.setDescription("Get a random list of nouns");
    }
    
    @Override
    public void run() throws Exception {
        String json = WebManager.getInstance().parseJSON("https://www.randomlists.com/data/nouns.json");
        JSONObject obj = WebManager.getInstance().toJSONObject(json);
        JSONArray array = obj.getJSONArray("data");
        List<String> nouns = new ArrayList<String>();
        for(int i = 0; i < array.size(); i++) {
            nouns.add(array.getString(i).trim());
        }
        
        List<String> randomNouns = DiceManager.getInstance().random(nouns, 10);
        WebManager.getInstance().put(RANDOM_NOUNS, randomNouns);
    }

}
