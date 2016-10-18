package org.wilson.world.test;

import java.util.Map.Entry;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ProfileManager;
import org.wilson.world.profile.SmalleyPersonalityInterpretation;
import org.wilson.world.profile.SmalleyProfile;
import org.wilson.world.quiz.QuizResult;

public class SmalleyPersonalityQuiz extends TestQuiz {

    @Override
    public String getQuizJSONFile() {
        return "smalley_personality_quiz.json";
    }

    private int getScore(TestQuizData data, int id) {
        return data.getScore(id);
    }

    @Override
    public QuizResult processData(TestQuizData score, boolean save) {
        int lion_point = 0;
        for(int i = 1; i <= 19; i++) {
            lion_point += this.getScore(score, i);
        }
        
        int otter_point = 0;
        for(int i = 20; i <= 38; i++) {
            otter_point += this.getScore(score, i);
        }
        
        int golden_retriever_point = 0;
        for(int i = 39; i <= 57; i++) {
            golden_retriever_point += this.getScore(score, i);
        }
        
        int beaver_point = 0;
        for(int i = 58; i <= 76; i++) {
            beaver_point += this.getScore(score, i);
        }
        
        SmalleyProfile profile = new SmalleyProfile();
        profile.lion = lion_point;
        profile.otter = otter_point;
        profile.goldenRetriever = golden_retriever_point;
        profile.beaver = beaver_point;
        
        String type = profile.getType();
        SmalleyPersonalityInterpretation spi = ProfileManager.getInstance().getSmalleyPersonalityInterpretation(type);
        
        QuizResult result = new QuizResult();
        
        StringBuilder sb = new StringBuilder();
        sb.append("You are a(n) <b>");
        sb.append(spi.name);
        sb.append("</b>.<hr/>");
        for(Entry<String, String> entry : spi.aspects.entrySet()) {
            sb.append("<b>");
            sb.append(entry.getKey());
            sb.append("</b><br/>");
            sb.append(entry.getValue());
            sb.append("<hr/>");
        }
        
        result.message = sb.toString();
        result.data.put("profile", profile);
        
        Event event = new Event();
        event.type = EventType.DoSmalleyPersonalityQuiz;
        event.data.put("result", result);
        event.data.put("save", save);
        EventManager.getInstance().fireEvent(event);
        
        return result;
    }
}
