package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.WritingSkill;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class WritingSkillManager {
    public static final String NAME = "writing_skill";
    
    private static WritingSkillManager instance;
    
    private DAO<WritingSkill> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private WritingSkillManager() {
        this.dao = DAOManager.getInstance().getDAO(WritingSkill.class);
        
        int id = 1;
        for(WritingSkill skill : this.getWritingSkills()) {
            for(Entry<String, String> entry : skill.examples.entrySet()) {
                String a = entry.getKey();
                String b = entry.getValue();
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "<h4>" + skill.topic + "</h4><p><b>" + a + "</b></p><p><i>" + b + "</i></p>";
                pair.bottom = skill.name;
                pair.url = "javascript:jumpTo('writing_skill_edit.jsp?id=" + skill.id + "')";
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
                
                for(WritingSkill skill : getWritingSkills()) {
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
    
    public static WritingSkillManager getInstance() {
        if(instance == null) {
            instance = new WritingSkillManager();
        }
        return instance;
    }
    
    public void createWritingSkill(WritingSkill skill) {
    }
    
    public WritingSkill getWritingSkill(int id) {
        WritingSkill skill = this.dao.get(id);
        if(skill != null) {
            return skill;
        }
        else {
            return null;
        }
    }
    
    public List<WritingSkill> getWritingSkills() {
        List<WritingSkill> result = new ArrayList<WritingSkill>();
        for(WritingSkill skill : this.dao.getAll()) {
            result.add(skill);
        }
        return result;
    }
    
    public void updateWritingSkill(WritingSkill skill) {
    }
    
    public void deleteWritingSkill(int id) {
    }
    
    public List<QuizPair> getWritingSkillQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}
