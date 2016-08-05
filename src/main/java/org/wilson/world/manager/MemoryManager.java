package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.web.WordListJob;

public class MemoryManager {
    private static MemoryManager instance;
    
    private MemoryManager() {
        
    }
    
    public static MemoryManager getInstance() {
        if(instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getPieces() {
        List<String> words = (List<String>) WebManager.getInstance().get(WordListJob.WORD_LIST);
        if(words == null) {
            return Collections.emptyList();
        }
        
        int limit = DataManager.getInstance().getValueAsInt("memory.limit");
        if(limit == 0) {
            limit = 10;
            DataManager.getInstance().setValue("memory.limit", limit);
        }
        
        return DiceManager.getInstance().random(words, limit);
    }
    
    /**
     * Returns exp
     * 
     * @param raw
     * @param input
     * @return
     */
    public int check(String raw, String input) {
        if(StringUtils.isBlank(raw) || StringUtils.isBlank(input)) {
            return 0;
        }
        
        List<String> old = new ArrayList<String>();
        for(String item : raw.split(",")) {
            if(!StringUtils.isBlank(item)) {
                old.add(item.trim().toLowerCase());
            }
        }
        
        List<String> now = new ArrayList<String>();
        for(String item : input.split(" ")) {
            if(!StringUtils.isBlank(item)) {
                now.add(item.trim().toLowerCase());
            }
        }
        
        int sum = 0;
        for(String item : old) {
            if(now.contains(item)) {
                sum++;
            }
        }
        
        if(sum == old.size()) {
            int limit = DataManager.getInstance().getValueAsInt("memory.limit");
            limit += 1;
            DataManager.getInstance().setValue("memory.limit", limit);
        }
        else if(sum == 0) {
            int limit = DataManager.getInstance().getValueAsInt("memory.limit");
            limit -= 1;
            DataManager.getInstance().setValue("memory.limit", limit);
        }
        
        int p = (int) (sum * 100.0 / old.size());
        if(DiceManager.getInstance().dice(p)) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
