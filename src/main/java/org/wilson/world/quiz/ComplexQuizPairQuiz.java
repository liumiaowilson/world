package org.wilson.world.quiz;

import java.util.Collections;
import java.util.List;

public abstract class ComplexQuizPairQuiz extends QuizPairQuiz {

    @Override
    public List<QuizPair> getQuizPairs() {
        return Collections.emptyList();
    }
    
    @Override
    public void init() {
        int maxLength = this.getQuizSize();
        List<List<QuizPair>> multiplePairs = this.getMultipleQuizPairs();
        
        ComplexQuizPairQuizBuilder builder = new ComplexQuizPairQuizBuilder();
        builder.setSize(maxLength);
        builder.addMultipleTargets(multiplePairs);
        
        this.setItems(builder.build());
    }

    public abstract List<List<QuizPair>> getMultipleQuizPairs();
}
