package org.wilson.world.quiz;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Extensible quiz", name = "system.quiz")
public abstract class ActiveQuiz extends QuizPairQuiz {
    
    public EventType getEventType() {
        return null;
    }
    
    public int getQuizSize() {
        return 5;
    }
    
    public abstract List<QuizPair> getQuizPairs();
}
