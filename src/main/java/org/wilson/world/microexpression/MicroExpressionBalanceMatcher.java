package org.wilson.world.microexpression;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class MicroExpressionBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return MicroExpressionQuiz.class;
    }

}
