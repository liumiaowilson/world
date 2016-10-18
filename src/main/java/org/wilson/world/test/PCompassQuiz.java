package org.wilson.world.test;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ProfileManager;
import org.wilson.world.profile.PCompassProfile;
import org.wilson.world.profile.PersonalityCompassType;
import org.wilson.world.quiz.QuizResult;

public class PCompassQuiz extends TestQuiz {

    @Override
    public String getQuizJSONFile() {
        return "personality_compass_quiz.json";
    }

    private int getScore(TestQuizData data, int id) {
        return data.getScore(id);
    }

    @Override
    public QuizResult processData(TestQuizData score, boolean save) {
        int north_count = 0;
        int south_count = 0;
        int east_count = 0;
        int west_count = 0;
        
        for(int i = 1; i <= 48; i++) {
            int val = this.getScore(score, i);
            if(val == 1) {
                north_count++;
            }
            else if(val == 2) {
                south_count++;
            }
            else if(val == 3) {
                east_count++;
            }
            else if(val == 4) {
                west_count++;
            }
        }
        
        PCompassProfile profile = new PCompassProfile();
        profile.north = north_count;
        profile.south = south_count;
        profile.west = west_count;
        profile.east = east_count;
        profile.init();
        
        String dominantType = profile.getDominantType();
        String subDominantType = profile.getSubDominantType();
        
        QuizResult result = new QuizResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("You are a(n) <b>");
        sb.append(dominantType);
        sb.append(" - ");
        sb.append(subDominantType);
        sb.append("</b>.<hr/>");
        
        sb.append("<b>");
        sb.append(dominantType);
        sb.append("</b><br/>");
        PersonalityCompassType pct = ProfileManager.getInstance().getPersonalityCompassType(dominantType);
        sb.append(pct.definition);
        sb.append("<hr/>");
        
        sb.append("<b>");
        sb.append(subDominantType);
        sb.append("</b><br/>");
        pct = ProfileManager.getInstance().getPersonalityCompassType(subDominantType);
        sb.append(pct.definition);
        sb.append("<hr/>");
        
        result.message = sb.toString();
        result.data.put("profile", profile);
        
        Event event = new Event();
        event.type = EventType.DoPCompassQuiz;
        event.data.put("result", result);
        event.data.put("save", save);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }
}
