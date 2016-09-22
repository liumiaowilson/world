package org.wilson.world.somp;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class SOMPBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return SOMPQuiz.class;
    }

}
