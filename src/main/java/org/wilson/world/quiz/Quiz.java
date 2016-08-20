package org.wilson.world.quiz;

import java.util.List;

public interface Quiz {
    public int getId();
    
    public void setId(int id);
    
    public String getName();
    
    public String getDescription();
    
    public List<QuizItem> getQuizItems();
    
    public QuizResult process(QuizPaper paper);
    
    public void init();
}
