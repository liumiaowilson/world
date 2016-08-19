package org.wilson.world.quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizItem {
    public int id;
    
    public String name;
    
    public QuizItemMode mode;
    
    public String question;
    
    public List<QuizItemOption> options = new ArrayList<QuizItemOption>();
    
    public QuizItemOption getOptionById(int id) {
        for(QuizItemOption option : options) {
            if(option.id == id) {
                return option;
            }
        }
        
        return null;
    }
}
