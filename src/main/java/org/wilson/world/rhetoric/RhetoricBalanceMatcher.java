package org.wilson.world.rhetoric;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class RhetoricBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return RhetoricQuiz.class;
    }

}
