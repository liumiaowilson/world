package org.wilson.world.bodylanguage;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class BodyLanguageBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return BodyLanguageQuiz.class;
    }

}
