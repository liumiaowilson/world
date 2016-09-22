package org.wilson.world.storyskill;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class StorySkillBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return StorySkillQuiz.class;
    }

}
