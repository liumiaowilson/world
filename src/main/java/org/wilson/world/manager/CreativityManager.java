package org.wilson.world.manager;

import java.util.Collections;
import java.util.List;

import org.wilson.world.web.WordListJob;

public class CreativityManager {
    private static CreativityManager instance;
    
    private CreativityManager() {
        
    }
    
    public static CreativityManager getInstance() {
        if(instance == null) {
            instance = new CreativityManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getFragments() {
        List<String> words = (List<String>) WebManager.getInstance().get(WordListJob.WORD_LIST);
        if(words == null) {
            return Collections.emptyList();
        }
        
        List<String> ret = DiceManager.getInstance().random(words, 10);
        return ret;
    }
}
