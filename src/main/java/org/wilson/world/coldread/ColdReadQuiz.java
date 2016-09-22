package org.wilson.world.coldread;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ColdReadManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class ColdReadQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoColdReadQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("cold_read.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return ColdReadManager.getInstance().getColdReadQuizPairs();
    }
}
