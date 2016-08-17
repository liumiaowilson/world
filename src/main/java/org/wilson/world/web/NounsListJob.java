package org.wilson.world.web;

import java.util.List;

import org.wilson.world.manager.WebManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NounsListJob extends SystemWebJob {
    public static final String NOUN_LIST = "noun_list";

    public NounsListJob() {
        this.setDescription("Get a list of nouns");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        String json = WebManager.getInstance().getContent("https://www.randomlists.com/data/nouns.json");
        JSONObject obj = WebManager.getInstance().toJSONObject(json);
        JSONArray array = obj.getJSONArray("data");
        List<String> nouns = WebManager.getInstance().getList(NOUN_LIST);
        nouns.clear();
        
        for(int i = 0; i < array.size(); i++) {
            nouns.add(array.getString(i).trim());
        }
    }

}
