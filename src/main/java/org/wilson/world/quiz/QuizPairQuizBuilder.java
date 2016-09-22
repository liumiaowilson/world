package org.wilson.world.quiz;

public class QuizPairQuizBuilder extends QuizBuilder {
    public QuizPairQuizBuilder() {
        this.setQuizItemMode(QuizItemMode.Multiple);
        this.setStrategy(QuizBuilderStrategy.ShowTop);
    }
    
    @Override
    public int getId(Object target) {
        return ((QuizPair)target).id;
    }

    @Override
    public String getTop(Object target) {
        return ((QuizPair)target).top;
    }

    @Override
    public String getBottom(Object target) {
        return ((QuizPair)target).bottom;
    }

    @Override
    public String getUrl(Object target) {
        return ((QuizPair)target).url;
    }

}
