package org.wilson.world.somp;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.SOMPManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class SOMPQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoSleighOfMouthQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("sleigh_of_mouth_pattern.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return SOMPManager.getInstance().getSOMPQuizPairs();
    }
}
