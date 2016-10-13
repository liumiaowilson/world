package org.wilson.world.test;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.quiz.QuizResult;

public class BigFivePersonalityQuiz extends TestQuiz {

    @Override
    public String getQuizJSONFile() {
        return "big_five_personality_quiz.json";
    }

    private int getScore(TestQuizData data, int id) {
        return data.getScore(id);
    }

    @Override
    public QuizResult processData(TestQuizData score) {
        int extroversion = 20 + getScore(score, 1) - getScore(score, 6) + getScore(score, 11) - getScore(score, 16)
                + getScore(score, 21) - getScore(score, 26) + getScore(score, 31) - getScore(score, 36) 
                + getScore(score, 41) - getScore(score, 46);
        
        int agreeableness = 14 - getScore(score, 2) + getScore(score, 7) - getScore(score, 12) + getScore(score, 17)
                - getScore(score, 22) + getScore(score, 27) - getScore(score, 32) + getScore(score, 37) 
                + getScore(score, 42) + getScore(score, 47);
        
        int conscientiousness = 14 + getScore(score, 3) - getScore(score, 8) + getScore(score, 13) - getScore(score, 18)
                + getScore(score, 23) - getScore(score, 28) + getScore(score, 33) - getScore(score, 38) 
                + getScore(score, 43) + getScore(score, 48);
        
        int neuroticism = 2 + getScore(score, 4) - getScore(score, 9) + getScore(score, 14) - getScore(score, 19)
                + getScore(score, 24) + getScore(score, 29) + getScore(score, 34) + getScore(score, 39) 
                + getScore(score, 44) + getScore(score, 49);
        
        int openness_to_experience = 8 + getScore(score, 5) - getScore(score, 10) + getScore(score, 15) - getScore(score, 20)
                + getScore(score, 25) - getScore(score, 30) + getScore(score, 35) + getScore(score, 40) 
                + getScore(score, 45) + getScore(score, 50);
        
        QuizResult result = new QuizResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Extroversion (E)[0, 40]</b>: ");
        sb.append(extroversion);
        sb.append("<br/>It is the personality trait of seeking fulfillment from sources outside the self or in community. High scorers tend to be very social while low scorers prefer to work on their projects alone.<hr/>");
        sb.append("<b>Agreeableness (A)[0, 40]</b>: ");
        sb.append(agreeableness);
        sb.append("<br/>It reflects much individuals adjust their behavior to suit others. High scorers are typically polite and like people. Low scorers tend to 'tell it like it is'.<hr/>");
        sb.append("<b>Conscientiousness (C)[0, 40]</b>: ");
        sb.append(conscientiousness);
        sb.append("<br/>It is the personality trait of being honest and hardworking. High scorers tend to follow rules and prefer clean homes. Low scorers may be messy and cheat others.<hr/>");
        sb.append("<b>Neuroticism (N)[0, 40]</b>: ");
        sb.append(neuroticism);
        sb.append("<br/>It is the personality trait of being emotional. High scores tend to be more emotionally instable. Low scores may change their mood fairly uneasily.<hr/>");
        sb.append("<b>Openness to Experience (O)[0, 40]</b>: ");
        sb.append(openness_to_experience);
        sb.append("<br/>It is the personality trait of seeking new experience and intellectual pursuits. High scores may day dream a lot. Low scorers may be very down to earth.<hr/>");
        
        result.message = sb.toString();
        result.data.put("extroversion", extroversion);
        result.data.put("agreeableness", agreeableness);
        result.data.put("conscientiousness", conscientiousness);
        result.data.put("neuroticism", neuroticism);
        result.data.put("openness_to_experience", openness_to_experience);
        
        Event event = new Event();
        event.type = EventType.DoBigFivePersonalityQuiz;
        event.data.put("result", result);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }
}
