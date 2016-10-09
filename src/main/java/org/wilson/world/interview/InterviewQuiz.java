package org.wilson.world.interview;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.InterviewManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class InterviewQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoInterviewQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("interview.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return InterviewManager.getInstance().getInterviewQuizPairs();
    }
}
