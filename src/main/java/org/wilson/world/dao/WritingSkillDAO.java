package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.WritingSkill;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WritingSkillDAO extends AbstractDAO<WritingSkill> {
    private static final Logger logger = Logger.getLogger(WritingSkillDAO.class);
    
    private Map<Integer, WritingSkill> skills = new HashMap<Integer, WritingSkill>();
    
    public WritingSkillDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("writing_skill.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            int id = 1;
            for(int i = 0; i < array.size(); i++) {
                JSONObject topicObj = array.getJSONObject(i);
                String topic = topicObj.getString("name");
                JSONArray skillArray = topicObj.getJSONArray("skills");
                for(int j = 0; j < skillArray.size(); j++) {
                    JSONObject skillObj = skillArray.getJSONObject(j);
                    String name = skillObj.getString("name");
                    String definition = skillObj.getString("definition");
                    WritingSkill skill = new WritingSkill();
                    skill.id = id++;
                    skill.topic = topic;
                    skill.name = name;
                    skill.definition = definition;
                    JSONArray exampleArray = skillObj.getJSONArray("examples");
                    for(int k = 0; k < exampleArray.size(); k++) {
                        JSONObject exampleObj = exampleArray.getJSONObject(k);
                        String a = exampleObj.getString("a");
                        String b = exampleObj.getString("b");
                        skill.examples.put(a, b);
                    }
                    this.skills.put(skill.id, skill);
                }
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
    public void create(WritingSkill skill) {
    }

    @Override
    public void update(WritingSkill skill) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public WritingSkill get(int id, boolean lazy) {
        return this.skills.get(id);
    }

    @Override
    public List<WritingSkill> getAll(boolean lazy) {
        return new ArrayList<WritingSkill>(this.skills.values());
    }

    @Override
    public List<WritingSkill> query(QueryTemplate<WritingSkill> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(WritingSkill skill) {
        return skill.id;
    }

    @Override
    public StringBuffer exportSingle(WritingSkill t) {
        return null;
    }

}
