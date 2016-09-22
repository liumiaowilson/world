package org.wilson.world.strategy;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.StrategyManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class StrategyQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoStrategyQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("strategy.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return StrategyManager.getInstance().getStrategyQuizPairs();
    }
}
