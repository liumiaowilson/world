package org.wilson.world.reaction;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ReactionManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class ReactionQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoReactionQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("reaction.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return ReactionManager.getInstance().getReactionQuizPairs();
    }
}
