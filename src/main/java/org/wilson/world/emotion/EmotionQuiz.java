package org.wilson.world.emotion;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EmotionManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class EmotionQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoEmotionQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("emotion.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return EmotionManager.getInstance().getEmotionQuizPairs();
    }
    

    @Override
    public boolean isPublic() {
        return false;
    }
}
