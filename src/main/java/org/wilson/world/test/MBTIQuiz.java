package org.wilson.world.test;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ProfileManager;
import org.wilson.world.profile.MBTIProfile;
import org.wilson.world.profile.MBTIType;
import org.wilson.world.quiz.QuizResult;

public class MBTIQuiz extends TestQuiz {

    @Override
    public String getQuizJSONFile() {
        return "MBTI_quiz.json";
    }

    private int getScore(TestQuizData data, int id) {
        return data.getScore(id);
    }

    @Override
    public QuizResult processData(TestQuizData score, boolean save) {
        int [] a = new int [7];
        int [] b = new int [7];
        for(int i = 1; i <= 70; i++) {
            int val = this.getScore(score, i);
            int group = i % 7;
            if(group == 0) {
                group = 7;
            }
            if(val == 1) {
                a[group - 1]++;
            }
            else if(val == 2) {
                b[group - 1]++;
            }
        }
        
        MBTIProfile profile = new MBTIProfile();
        profile.extraversion = a[0];
        profile.introversion = b[0];
        profile.sensing = a[1] + a[2];
        profile.intuition = b[1] + b[2];
        profile.thinking = a[3] + a[4];
        profile.feeling = b[3] + b[4];
        profile.judging = a[5] + a[6];
        profile.perceiving = b[5] + b[6];
        
        String name = profile.getType();
        MBTIType type = ProfileManager.getInstance().getMBTIType(name);
        
        QuizResult result = new QuizResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("You are a(n) <b>");
        sb.append(profile.getDisplay());
        sb.append("</b>.<hr/>");
        
        sb.append(type.definition);
        
        result.message = sb.toString();
        result.data.put("profile", profile);
        
        Event event = new Event();
        event.type = EventType.DoMBTIQuiz;
        event.data.put("result", result);
        event.data.put("save", save);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }
}
