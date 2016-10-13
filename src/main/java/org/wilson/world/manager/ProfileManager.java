package org.wilson.world.manager;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.profile.BigFiveProfile;
import org.wilson.world.quiz.QuizResult;

public class ProfileManager implements EventListener {
    private static ProfileManager instance;
    
    private ProfileManager() {
        EventManager.getInstance().registerListener(EventType.DoBigFivePersonalityQuiz, this);
    }
    
    public static ProfileManager getInstance() {
        if(instance == null) {
            instance = new ProfileManager();
        }
        
        return instance;
    }
    
    public BigFiveProfile getBigFiveProfile() {
        BigFiveProfile profile = new BigFiveProfile();
        
        profile.extroversion = DataManager.getInstance().getValueAsInt("bigfive.E");
        profile.agreeableness = DataManager.getInstance().getValueAsInt("bigfive.A");
        profile.conscientiousness = DataManager.getInstance().getValueAsInt("bigfive.C");
        profile.neuroticism = DataManager.getInstance().getValueAsInt("bigfive.N");
        profile.opennessToExperience = DataManager.getInstance().getValueAsInt("bigfive.O");
        
        return profile;
    }
    
    public void setBigFiveProfile(BigFiveProfile profile) {
        if(profile == null) {
            return;
        }
        
        DataManager.getInstance().setValue("bigfive.E", profile.extroversion);
        DataManager.getInstance().setValue("bigfive.A", profile.agreeableness);
        DataManager.getInstance().setValue("bigfive.C", profile.conscientiousness);
        DataManager.getInstance().setValue("bigfive.N", profile.neuroticism);
        DataManager.getInstance().setValue("bigfive.O", profile.opennessToExperience);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        QuizResult result = (QuizResult) event.data.get("result");
        if(EventType.DoBigFivePersonalityQuiz == event.type) {
            this.handleBigFivePersonalityQuiz(result);
        }
    }
    
    private void handleBigFivePersonalityQuiz(QuizResult result) {
        BigFiveProfile profile = new BigFiveProfile();
        profile.extroversion = (Integer)result.data.get("extroversion");
        profile.agreeableness = (Integer)result.data.get("agreeableness");
        profile.conscientiousness = (Integer)result.data.get("conscientiousness");
        profile.neuroticism = (Integer)result.data.get("neuroticism");
        profile.opennessToExperience = (Integer)result.data.get("openness_to_experience");
        this.setBigFiveProfile(profile);
    }
}
