package org.wilson.world.dress;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class DressBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return DressQuiz.class;
    }

}
