package org.wilson.world.personality;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class PersonalityBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return PersonalityQuiz.class;
    }

}
