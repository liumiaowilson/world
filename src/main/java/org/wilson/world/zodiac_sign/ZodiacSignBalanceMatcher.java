package org.wilson.world.zodiac_sign;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class ZodiacSignBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return ZodiacSignQuiz.class;
    }

}
