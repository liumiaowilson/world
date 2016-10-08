package org.wilson.world.faceread;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.FaceReadManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class FaceReadQuiz extends QuizPairQuiz {

    @Override
    public EventType getEventType() {
        return EventType.DoFaceReadQuiz;
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("face_read.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return FaceReadManager.getInstance().getFaceReadQuizPairs();
    }
}
