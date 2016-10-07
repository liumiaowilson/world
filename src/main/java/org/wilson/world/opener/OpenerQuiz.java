package org.wilson.world.opener;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.OpenerManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class OpenerQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoOpenerQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("opener.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return OpenerManager.getInstance().getOpenerQuizPairs();
    }
}
