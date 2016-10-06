package org.wilson.world.bodylanguage;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.BodyLanguageManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class BodyLanguageQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoBodyLanguageQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("body_language.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return BodyLanguageManager.getInstance().getBodyLanguageQuizPairs();
    }
}
