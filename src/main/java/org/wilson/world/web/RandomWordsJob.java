package org.wilson.world.web;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.WebManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RandomWordsJob extends SystemWebJob {
    public static final String RANDOM_WORDS = "random_words";

    public RandomWordsJob() {
        this.setDescription("Get a random list of words");
    }
    
    @Override
    public void run() throws Exception {
        String json = WebManager.getInstance().parseJSON("https://www.randomlists.com/data/words.json");
        JSONObject obj = WebManager.getInstance().toJSONObject(json);
        JSONArray array = obj.getJSONArray("data");
        List<String> words = new ArrayList<String>();
        for(int i = 0; i < array.size(); i++) {
            words.add(array.getString(i).trim());
        }
        
        List<String> randomWords = DiceManager.getInstance().random(words, 10);
        WebManager.getInstance().put(RANDOM_WORDS, randomWords);
    }

}
