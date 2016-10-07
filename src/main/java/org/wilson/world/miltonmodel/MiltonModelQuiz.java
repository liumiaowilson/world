package org.wilson.world.miltonmodel;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.MiltonModelManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class MiltonModelQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoMiltonModelQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("milton_model.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return MiltonModelManager.getInstance().getMiltonModelQuizPairs();
    }
}
