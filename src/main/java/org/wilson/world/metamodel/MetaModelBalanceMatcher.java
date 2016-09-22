package org.wilson.world.metamodel;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class MetaModelBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return MetaModelQuiz.class;
    }

}
