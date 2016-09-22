package org.wilson.world.personality;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.PersonalityManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class PersonalityQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoPersonalityQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("personality.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return PersonalityManager.getInstance().getPersonalityQuizPairs();
    }
}
