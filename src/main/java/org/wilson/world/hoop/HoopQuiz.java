package org.wilson.world.hoop;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.HoopManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class HoopQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoHoopQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("hoop.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return HoopManager.getInstance().getHoopQuizPairs();
    }
}
