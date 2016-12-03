package org.wilson.world.pushpull;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.PushPullManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class PushPullQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoPushPullQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("push_pull.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return PushPullManager.getInstance().getPushPullQuizPairs();
    }
}
