package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.ChatSkill;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ChatSkillManager {
    public static final String NAME = "chat_skill";
    
    private static ChatSkillManager instance;
    
    private DAO<ChatSkill> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private ChatSkillManager() {
        this.dao = DAOManager.getInstance().getDAO(ChatSkill.class);
        
        int id = 1;
        for(ChatSkill skill : this.getChatSkills()) {
            for(List<String> example : skill.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                StringBuilder sb = new StringBuilder();
                for(String statement : example) {
                    sb.append(statement);
                    sb.append("<br/>");
                }
                pair.top = sb.toString();
                pair.bottom = skill.name;
                pair.url = "javascript:jumpTo('chat_skill_edit.jsp?id=" + skill.id + "')";
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
                
                for(ChatSkill skill : getChatSkills()) {
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
    
    public static ChatSkillManager getInstance() {
        if(instance == null) {
            instance = new ChatSkillManager();
        }
        return instance;
    }
    
    public void createChatSkill(ChatSkill skill) {
    }
    
    public ChatSkill getChatSkill(int id) {
        ChatSkill skill = this.dao.get(id);
        if(skill != null) {
            return skill;
        }
        else {
            return null;
        }
    }
    
    public List<ChatSkill> getChatSkills() {
        List<ChatSkill> result = new ArrayList<ChatSkill>();
        for(ChatSkill skill : this.dao.getAll()) {
            result.add(skill);
        }
        return result;
    }
    
    public void updateChatSkill(ChatSkill skill) {
    }
    
    public void deleteChatSkill(int id) {
    }
    
    public List<QuizPair> getChatSkillQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
    
    public ChatSkill randomChatSkill() {
        List<ChatSkill> skills = this.dao.getAll();
        if(skills.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(skills.size());
        return skills.get(n);
    }
}
