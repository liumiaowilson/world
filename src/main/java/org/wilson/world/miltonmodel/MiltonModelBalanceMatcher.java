package org.wilson.world.miltonmodel;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class MiltonModelBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return MiltonModelQuiz.class;
    }

}
