package org.wilson.world.designpattern;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class DesignPatternBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return DesignPatternQuiz.class;
    }

}
