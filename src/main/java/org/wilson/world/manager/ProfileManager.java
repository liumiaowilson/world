package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.profile.BigFiveProfile;
import org.wilson.world.profile.MBTIProfile;
import org.wilson.world.profile.MBTIType;
import org.wilson.world.profile.PColorProfile;
import org.wilson.world.profile.PCompassProfile;
import org.wilson.world.profile.PersonalityColorType;
import org.wilson.world.profile.PersonalityCompassType;
import org.wilson.world.profile.SmalleyPersonalityInterpretation;
import org.wilson.world.profile.SmalleyProfile;
import org.wilson.world.quiz.QuizResult;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProfileManager implements EventListener {
    private static final Logger logger = Logger.getLogger(ProfileManager.class);
    
    private static ProfileManager instance;
    
    private Map<String, SmalleyPersonalityInterpretation> spis = new HashMap<String, SmalleyPersonalityInterpretation>();
    private Map<String, PersonalityCompassType> pcts = new HashMap<String, PersonalityCompassType>();
    private Map<String, PersonalityColorType> colorTypes = new HashMap<String, PersonalityColorType>();
    private Map<String, MBTIType> mbtiTypes = new HashMap<String, MBTIType>();
    
    private ProfileManager() {
        this.loadSmalleyPersonalityInterpretations();
        this.loadPersonalityCompassTypes();
        this.loadPersonalityColorTypes();
        this.loadMBTITypes();
        
        EventManager.getInstance().registerListener(EventType.DoBigFivePersonalityQuiz, this);
        EventManager.getInstance().registerListener(EventType.DoSmalleyPersonalityQuiz, this);
        EventManager.getInstance().registerListener(EventType.DoPCompassQuiz, this);
        EventManager.getInstance().registerListener(EventType.DoPColorQuiz, this);
        EventManager.getInstance().registerListener(EventType.DoMBTIQuiz, this);
    }
    
    private void loadMBTITypes() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("MBTI.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                MBTIType type = new MBTIType();
                type.name = obj.getString("name");
                type.definition = obj.getString("definition");
                
                this.mbtiTypes.put(type.name, type);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
    
    private void loadPersonalityColorTypes() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("personality_color.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PersonalityColorType pct = new PersonalityColorType();
                pct.name = obj.getString("name");
                pct.strength = obj.getString("strength");
                pct.weakness = obj.getString("weakness");
                
                this.colorTypes.put(pct.name, pct);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
    
    private void loadPersonalityCompassTypes() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("personality_compass.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PersonalityCompassType pct = new PersonalityCompassType();
                pct.name = obj.getString("name");
                pct.definition = obj.getString("definition");
                pct.motto = obj.getString("motto");
                pct.strength = obj.getString("strength");
                pct.weakness = obj.getString("weakness");
                pct.aptitude = obj.getString("aptitude");
                pct.priority = obj.getString("priority");
                pct.motivation = obj.getString("motivation");
                pct.pet_peeve = obj.getString("pet_peeve");
                pct.work_style = obj.getString("work_style");
                pct.work_competency = obj.getString("work_competency");
                pct.pace = obj.getString("pace");
                pct.image = obj.getString("image");
                
                this.pcts.put(pct.name, pct);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
    
    private void loadSmalleyPersonalityInterpretations() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("smalley_personality_interpretation.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SmalleyPersonalityInterpretation spi = new SmalleyPersonalityInterpretation();
                spi.name = obj.getString("name");
                JSONArray aspectArray = obj.getJSONArray("aspects");
                for(int j = 0; j < aspectArray.size(); j++) {
                    JSONObject aspectObj = aspectArray.getJSONObject(j);
                    String key = aspectObj.getString("name");
                    String value = aspectObj.getString("value");
                    spi.aspects.put(key, value);
                }
                
                this.spis.put(spi.name, spi);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
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
    
    public boolean hasBigFiveProfile() {
        boolean has = DataManager.getInstance().hasKey("bigfive.E") && 
                DataManager.getInstance().hasKey("bigfive.A") &&
                DataManager.getInstance().hasKey("bigfive.C") &&
                DataManager.getInstance().hasKey("bigfive.N") &&
                DataManager.getInstance().hasKey("bigfive.O");
        return has;
    }
    
    public void setSmalleyProfile(SmalleyProfile profile) {
        if(profile == null) {
            return;
        }
        
        DataManager.getInstance().setValue("smalley.L", profile.lion);
        DataManager.getInstance().setValue("smalley.O", profile.otter);
        DataManager.getInstance().setValue("smalley.G", profile.goldenRetriever);
        DataManager.getInstance().setValue("smalley.B", profile.beaver);
    }
    
    public SmalleyProfile getSmalleyProfile() {
        SmalleyProfile profile = new SmalleyProfile();
        
        profile.lion = DataManager.getInstance().getValueAsInt("smalley.L");
        profile.otter = DataManager.getInstance().getValueAsInt("smalley.O");
        profile.goldenRetriever = DataManager.getInstance().getValueAsInt("smalley.G");
        profile.beaver = DataManager.getInstance().getValueAsInt("smalley.B");
        
        return profile;
    }
    
    public boolean hasSmalleyProfile() {
        boolean has = DataManager.getInstance().hasKey("smalley.L") &&
                DataManager.getInstance().hasKey("smalley.O") &&
                DataManager.getInstance().hasKey("smalley.G") &&
                DataManager.getInstance().hasKey("smalley.B");
        return has;
    }
    
    public PCompassProfile getPCompassProfile() {
        PCompassProfile profile = new PCompassProfile();
        
        profile.north = DataManager.getInstance().getValueAsInt("PCompass.north");
        profile.south = DataManager.getInstance().getValueAsInt("PCompass.south");
        profile.west = DataManager.getInstance().getValueAsInt("PCompass.west");
        profile.east = DataManager.getInstance().getValueAsInt("PCompass.east");
        profile.init();
        
        return profile;
    }
    
    public void setPCompassProfile(PCompassProfile profile) {
        if(profile == null) {
            return;
        }
        
        DataManager.getInstance().setValue("PCompass.north", profile.north);
        DataManager.getInstance().setValue("PCompass.south", profile.south);
        DataManager.getInstance().setValue("PCompass.west", profile.west);
        DataManager.getInstance().setValue("PCompass.east", profile.east);
    }
    
    public boolean hasPCompassProfile() {
        boolean has = DataManager.getInstance().hasKey("PCompass.north") &&
                DataManager.getInstance().hasKey("PCompass.south") &&
                DataManager.getInstance().hasKey("PCompass.west") &&
                DataManager.getInstance().hasKey("PCompass.east");
        return has;
    }
    
    public PColorProfile getPColorProfile() {
        PColorProfile profile = new PColorProfile();
        
        profile.red = DataManager.getInstance().getValueAsInt("PColor.red");
        profile.blue = DataManager.getInstance().getValueAsInt("PColor.blue");
        profile.yellow = DataManager.getInstance().getValueAsInt("PColor.yellow");
        profile.green = DataManager.getInstance().getValueAsInt("PColor.green");
        profile.init();
        
        return profile;
    }
    
    public void setPColorProfile(PColorProfile profile) {
        if(profile == null) {
            return;
        }
        
        DataManager.getInstance().setValue("PColor.red", profile.red);
        DataManager.getInstance().setValue("PColor.blue", profile.blue);
        DataManager.getInstance().setValue("PColor.yellow", profile.yellow);
        DataManager.getInstance().setValue("PColor.green", profile.green);
    }
    
    public boolean hasPColorProfile() {
        boolean has = DataManager.getInstance().hasKey("PColor.red") &&
                DataManager.getInstance().hasKey("PColor.blue") &&
                DataManager.getInstance().hasKey("PColor.yellow") &&
                DataManager.getInstance().hasKey("PColor.green");
        return has;
    }
    
    public MBTIProfile getMBTIProfile() {
        MBTIProfile profile = new MBTIProfile();
        profile.extraversion = DataManager.getInstance().getValueAsInt("MBTI.E");
        profile.introversion = DataManager.getInstance().getValueAsInt("MBTI.I");
        profile.sensing = DataManager.getInstance().getValueAsInt("MBTI.S");
        profile.intuition = DataManager.getInstance().getValueAsInt("MBTI.N");
        profile.thinking = DataManager.getInstance().getValueAsInt("MBTI.T");
        profile.feeling = DataManager.getInstance().getValueAsInt("MBTI.F");
        profile.judging = DataManager.getInstance().getValueAsInt("MBTI.J");
        profile.perceiving = DataManager.getInstance().getValueAsInt("MBTI.P");
        
        return profile;
    }
    
    public void setMBTIProfile(MBTIProfile profile) {
        if(profile == null) {
            return;
        }
        
        DataManager.getInstance().setValue("MBTI.E", profile.extraversion);
        DataManager.getInstance().setValue("MBTI.I", profile.introversion);
        DataManager.getInstance().setValue("MBTI.S", profile.sensing);
        DataManager.getInstance().setValue("MBTI.N", profile.intuition);
        DataManager.getInstance().setValue("MBTI.T", profile.thinking);
        DataManager.getInstance().setValue("MBTI.F", profile.feeling);
        DataManager.getInstance().setValue("MBTI.J", profile.judging);
        DataManager.getInstance().setValue("MBTI.P", profile.perceiving);
    }
    
    public boolean hasMBTIProfile() {
        boolean has = DataManager.getInstance().hasKey("MBTI.E") &&
                DataManager.getInstance().hasKey("MBTI.I") &&
                DataManager.getInstance().hasKey("MBTI.S") &&
                DataManager.getInstance().hasKey("MBTI.N") &&
                DataManager.getInstance().hasKey("MBTI.T") &&
                DataManager.getInstance().hasKey("MBTI.F") &&
                DataManager.getInstance().hasKey("MBTI.J") &&
                DataManager.getInstance().hasKey("MBTI.P");
        return has;
    }
    
    public SmalleyPersonalityInterpretation getSmalleyPersonalityInterpretation(String name) {
        return this.spis.get(name);
    }
    
    public PersonalityCompassType getPersonalityCompassType(String name) {
        return this.pcts.get(name);
    }
    
    public PersonalityColorType getPersonalityColorType(String name) {
        return this.colorTypes.get(name);
    }
    
    public MBTIType getMBTIType(String name) {
        return this.mbtiTypes.get(name);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        QuizResult result = (QuizResult) event.data.get("result");
        Boolean save = (Boolean) event.data.get("save");
        if(save != null && !save) {
            return;
        }
        
        if(EventType.DoBigFivePersonalityQuiz == event.type) {
            this.handleBigFivePersonalityQuiz(result);
        }
        else if(EventType.DoSmalleyPersonalityQuiz == event.type) {
            this.handleSmalleyPersonalityQuiz(result);
        }
        else if(EventType.DoPCompassQuiz == event.type) {
            this.handlePCompassQuiz(result);
        }
        else if(EventType.DoPColorQuiz == event.type) {
            this.handlePColorQuiz(result);
        }
        else if(EventType.DoMBTIQuiz == event.type) {
            this.handleMBTIQuiz(result);
        }
    }
    
    private void handleMBTIQuiz(QuizResult result) {
        MBTIProfile profile = (MBTIProfile) result.data.get("profile");
        this.setMBTIProfile(profile);
    }
    
    private void handlePColorQuiz(QuizResult result) {
        PColorProfile profile = (PColorProfile) result.data.get("profile");
        this.setPColorProfile(profile);
    }
    
    private void handlePCompassQuiz(QuizResult result) {
        PCompassProfile profile = (PCompassProfile) result.data.get("profile");
        this.setPCompassProfile(profile);
    }
    
    private void handleSmalleyPersonalityQuiz(QuizResult result) {
        SmalleyProfile profile = (SmalleyProfile) result.data.get("profile");
        this.setSmalleyProfile(profile);
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
