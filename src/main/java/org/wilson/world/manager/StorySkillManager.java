package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.StorySkill;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class StorySkillManager {
    public static final String NAME = "story_skill";
    
    private static StorySkillManager instance;
    
    private DAO<StorySkill> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private StorySkillManager() {
        this.dao = DAOManager.getInstance().getDAO(StorySkill.class);
        
        int id = 1;
        for(StorySkill skill : this.getStorySkills()) {
            for(String example : skill.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = skill.name;
                this.pairs.put(pair.id, pair);
            }
        }
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(StorySkill skill : getStorySkills()) {
                    boolean found = skill.name.contains(text) || skill.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = skill.id;
                        content.name = skill.name;
                        content.description = skill.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static StorySkillManager getInstance() {
        if(instance == null) {
            instance = new StorySkillManager();
        }
        return instance;
    }
    
    public void createStorySkill(StorySkill skill) {
    }
    
    public StorySkill getStorySkill(int id) {
        StorySkill skill = this.dao.get(id);
        if(skill != null) {
            return skill;
        }
        else {
            return null;
        }
    }
    
    public List<StorySkill> getStorySkills() {
        List<StorySkill> result = new ArrayList<StorySkill>();
        for(StorySkill skill : this.dao.getAll()) {
            result.add(skill);
        }
        return result;
    }
    
    public void updateStorySkill(StorySkill skill) {
    }
    
    public void deleteStorySkill(int id) {
    }
    
    public List<QuizPair> getStorySkillQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
    
    public StorySkill randomStorySkill() {
        List<StorySkill> skills = this.dao.getAll();
        if(skills.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(skills.size());
        return skills.get(n);
    }
}
