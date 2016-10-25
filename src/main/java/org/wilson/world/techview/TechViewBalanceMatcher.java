package org.wilson.world.techview;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class TechViewBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return TechViewQuiz.class;
    }

}
