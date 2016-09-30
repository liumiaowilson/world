package org.wilson.world.microexpression;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.MicroExpressionManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class MicroExpressionQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoMicroExpressionQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("micro_expression.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return MicroExpressionManager.getInstance().getMicroExpressionQuizPairs();
    }
}
