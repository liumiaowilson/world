package org.wilson.world.strategy;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class StrategyBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return StrategyQuiz.class;
    }

}
