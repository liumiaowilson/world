package org.wilson.world.chatskill;

import org.wilson.world.quiz.QuizBalanceMatcher;

public class ChatSkillBalanceMatcher extends QuizBalanceMatcher {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getQuizClass() {
        return ChatSkillQuiz.class;
    }

}
