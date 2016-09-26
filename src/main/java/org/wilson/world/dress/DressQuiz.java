package org.wilson.world.dress;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DressManager;
import org.wilson.world.quiz.ComplexQuizPairQuiz;
import org.wilson.world.quiz.QuizPair;

public class DressQuiz extends ComplexQuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoDressQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("dress.quiz.max_length", 5);
    }

    @Override
    public List<List<QuizPair>> getMultipleQuizPairs() {
        return DressManager.getInstance().getDressQuizPairs();
    }
}
