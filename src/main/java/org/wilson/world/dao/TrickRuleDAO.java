package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.TrickRule;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TrickRuleDAO extends AbstractDAO<TrickRule> {
    private static final Logger logger = Logger.getLogger(TrickRuleDAO.class);
    
    private Map<Integer, TrickRule> rules = new HashMap<Integer, TrickRule>();
    
    public TrickRuleDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("trick_rule.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                TrickRule rule = new TrickRule();
                rule.id = i + 1;
                rule.name = obj.getString("name");
                rule.definition = "";
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    rule.examples.add(example);
                }
                this.rules.put(rule.id, rule);
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
    public void create(TrickRule rule) {
    }

    @Override
    public void update(TrickRule rule) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public TrickRule get(int id, boolean lazy) {
        return this.rules.get(id);
    }

    @Override
    public List<TrickRule> getAll(boolean lazy) {
        return new ArrayList<TrickRule>(this.rules.values());
    }

    @Override
    public List<TrickRule> query(QueryTemplate<TrickRule> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(TrickRule rule) {
        return rule.id;
    }

    @Override
    public StringBuffer exportSingle(TrickRule t) {
        return null;
    }

}
