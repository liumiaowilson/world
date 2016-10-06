package org.wilson.world.designpattern;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DesignPatternManager;
import org.wilson.world.quiz.ComplexQuizPairQuiz;
import org.wilson.world.quiz.QuizPair;

public class DesignPatternQuiz extends ComplexQuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoDesignPatternQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("design_pattern.quiz.max_length", 5);
    }

    @Override
    public List<List<QuizPair>> getMultipleQuizPairs() {
        return DesignPatternManager.getInstance().getDesignPatternQuizPairs();
    }
}
