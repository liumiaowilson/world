package org.wilson.world.writingskill;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class WritingSkillBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return WritingSkillQuiz.class;
    }

}
