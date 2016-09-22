package org.wilson.world.zodiac_sign;

import java.util.List;

import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ZodiacSignManager;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.quiz.QuizPairQuiz;

public class ZodiacSignQuiz extends QuizPairQuiz {
    private QuizType type;
    
    public QuizType getType() {
        return type;
    }

    public void setType(QuizType type) {
        this.type = type;
    }

    @Override
    public EventType getEventType() {
        if(QuizType.Date == type) {
            return EventType.DoZSDateQuiz;
        }
        else if(QuizType.Strengths == type) {
            return EventType.DoZSStrengthsQuiz;
        }
        else if(QuizType.Weaknesses == type) {
            return EventType.DoZSWeaknessesQuiz;
        }
        else if(QuizType.Likes == type) {
            return EventType.DoZSLikesQuiz;
        }
        else if(QuizType.Dislikes == type) {
            return EventType.DoZSDislikesQuiz;
        }
        else {
            return EventType.DoZSDateQuiz;
        }
    }

    @Override
    public int getQuizSize() {
        return ConfigManager.getInstance().getConfigAsInt("zodiac_sign.quiz.max_length", 5);
    }

    @Override
    public List<QuizPair> getQuizPairs() {
        return ZodiacSignManager.getInstance().getZodiacSignQuizPairs(this.type);
    }
}
