package org.wilson.world.chatskill;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ChatSkillManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class ChatSkillQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoChatSkillQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("chat_skill.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return ChatSkillManager.getInstance().getChatSkillQuizPairs();
    }
}
