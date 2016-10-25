package org.wilson.world.techview;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.TechViewManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class TechViewQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoTechViewQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("tech_view.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return TechViewManager.getInstance().getTechViewQuizPairs();
    }
}
