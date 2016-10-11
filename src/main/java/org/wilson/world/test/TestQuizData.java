package org.wilson.world.test;

import java.util.Map;

public class TestQuizData {
    private Map<Integer, Integer> data;
    
    public TestQuizData(Map<Integer, Integer> data) {
        this.data = data;
    }
    
    public int getScore(int id) {
        if(data != null) {
            if(data.containsKey(id)) {
                return data.get(id);
            }
        }
        
        return 0;
    }
}
