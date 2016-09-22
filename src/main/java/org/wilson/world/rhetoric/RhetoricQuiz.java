package org.wilson.world.rhetoric;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.RhetoricManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class RhetoricQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoRhetoricQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("rhetoric.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return RhetoricManager.getInstance().getRhetoricQuizPairs();
    }
}
