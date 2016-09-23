package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.ChatSkill;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ChatSkillDAO extends AbstractDAO<ChatSkill> {
    private static final Logger logger = Logger.getLogger(ChatSkillDAO.class);
    
    private Map<Integer, ChatSkill> skills = new HashMap<Integer, ChatSkill>();
    
    public ChatSkillDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("chat_skill.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ChatSkill skill = new ChatSkill();
                skill.id = i + 1;
                skill.name = obj.getString("name");
                skill.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    JSONArray example = exampleArray.getJSONArray(j);
                    List<String> list = new ArrayList<String>();
                    for(int k = 0; k < example.size(); k++) {
                        String statement = example.getString(k);
                        list.add(statement);
                    }
                    skill.examples.add(list);
                }
                this.skills.put(skill.id, skill);
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

    @Override
    public void create(ChatSkill skill) {
    }

    @Override
    public void update(ChatSkill skill) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public ChatSkill get(int id, boolean lazy) {
        return this.skills.get(id);
    }

    @Override
    public List<ChatSkill> getAll(boolean lazy) {
        return new ArrayList<ChatSkill>(this.skills.values());
    }

    @Override
    public List<ChatSkill> query(QueryTemplate<ChatSkill> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(ChatSkill skill) {
        return skill.id;
    }

    @Override
    public StringBuffer exportSingle(ChatSkill t) {
        return null;
    }

}
