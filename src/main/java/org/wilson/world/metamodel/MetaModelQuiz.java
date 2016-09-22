package org.wilson.world.metamodel;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.MetaModelManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class MetaModelQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoMetaModelQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("meta_model.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return MetaModelManager.getInstance().getMetaModelQuizPairs();
    }
}
