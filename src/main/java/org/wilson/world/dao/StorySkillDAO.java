package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.StorySkill;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StorySkillDAO extends AbstractDAO<StorySkill> {
    private static final Logger logger = Logger.getLogger(StorySkillDAO.class);
    
    private Map<Integer, StorySkill> skills = new HashMap<Integer, StorySkill>();
    
    public StorySkillDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("story_skill.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                StorySkill skill = new StorySkill();
                skill.id = i + 1;
                skill.name = obj.getString("name");
                skill.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    skill.examples.add(example);
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
    public void create(StorySkill skill) {
    }

    @Override
    public void update(StorySkill skill) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public StorySkill get(int id, boolean lazy) {
        return this.skills.get(id);
    }

    @Override
    public List<StorySkill> getAll(boolean lazy) {
        return new ArrayList<StorySkill>(this.skills.values());
    }

    @Override
    public List<StorySkill> query(QueryTemplate<StorySkill> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(StorySkill skill) {
        return skill.id;
    }

    @Override
    public StringBuffer exportSingle(StorySkill t) {
        return null;
    }

}
