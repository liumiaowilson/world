package org.wilson.world.test;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ProfileManager;
import org.wilson.world.profile.PColorProfile;
import org.wilson.world.profile.PersonalityColorType;
import org.wilson.world.quiz.QuizResult;

public class PColorQuiz extends TestQuiz {

    @Override
    public String getQuizJSONFile() {
        return "personality_color_quiz.json";
    }

    private int getScore(TestQuizData data, int id) {
        return data.getScore(id);
    }

    @Override
    public QuizResult processData(TestQuizData score, boolean save) {
        int a_before = 0;
        int b_before = 0;
        int c_before = 0;
        int d_before = 0;
        for(int i = 1; i <= 15; i++) {
            int val = this.getScore(score, i);
            if(val == 1) {
                a_before++;
            }
            else if(val == 2) {
                b_before++;
            }
            else if(val == 3) {
                c_before++;
            }
            else if(val == 4) {
                d_before++;
            }
        }
        
        int a_after = 0;
        int b_after = 0;
        int c_after = 0;
        int d_after = 0;
        for(int i = 16; i <= 30; i++) {
            int val = this.getScore(score, i);
            if(val == 1) {
                a_after++;
            }
            else if(val == 2) {
                b_after++;
            }
            else if(val == 3) {
                c_after++;
            }
            else if(val == 4) {
                d_after++;
            }
        }
        
        PColorProfile profile = new PColorProfile();
        profile.red = a_before + d_after;
        profile.blue = b_before + c_after;
        profile.yellow = c_before + b_after;
        profile.green = d_before + a_after;
        profile.init();
        
        List<String> types = profile.getTypes();
        String typesStr = profile.getTypeDisplay();
        
        QuizResult result = new QuizResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("You are a(n) <b>");
        sb.append(typesStr);
        sb.append("</b>.<hr/>");
        
        for(String type : types) {
            PersonalityColorType pct = ProfileManager.getInstance().getPersonalityColorType(type);
            sb.append("<b>");
            sb.append(type);
            sb.append("</b><br/>");
            sb.append("<b>Strength</b>:");
            sb.append(pct.strength);
            sb.append("<br/><b>Weakness</b>:");
            sb.append(pct.weakness);
            sb.append("<hr/>");
        }
        
        result.message = sb.toString();
        result.data.put("profile", profile);
        
        Event event = new Event();
        event.type = EventType.DoPColorQuiz;
        event.data.put("result", result);
        event.data.put("save", save);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }
}
