package org.wilson.world.web;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.WebManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WordListJob extends SystemWebJob {
    public static final String WORD_LIST = "word_list";

    public WordListJob() {
        this.setDescription("Get a list of words");
    }
    
    @Override
    public void run() throws Exception {
        String json = WebManager.getInstance().getContent("https://www.randomlists.com/data/words.json");
        JSONObject obj = WebManager.getInstance().toJSONObject(json);
        JSONArray array = obj.getJSONArray("data");
        List<String> words = new ArrayList<String>();
        for(int i = 0; i < array.size(); i++) {
            words.add(array.getString(i).trim());
        }
        
        WebManager.getInstance().put(WORD_LIST, words);
    }

}
