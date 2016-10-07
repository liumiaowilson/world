package org.wilson.world.opener;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class OpenerBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return OpenerQuiz.class;
    }

}
