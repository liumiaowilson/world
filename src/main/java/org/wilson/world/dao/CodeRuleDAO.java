package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.CodeRule;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CodeRuleDAO extends AbstractDAO<CodeRule> {
    private static final Logger logger = Logger.getLogger(CodeRuleDAO.class);
    
    private Map<Integer, CodeRule> rules = new HashMap<Integer, CodeRule>();
    
    public CodeRuleDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("code_rule.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                CodeRule rule = new CodeRule();
                rule.id = i + 1;
                rule.name = obj.getString("name");
                rule.topic = obj.getString("topic");
                rule.definition = obj.getString("definition");
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
    public void create(CodeRule rule) {
    }

    @Override
    public void update(CodeRule rule) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public CodeRule get(int id, boolean lazy) {
        return this.rules.get(id);
    }

    @Override
    public List<CodeRule> getAll(boolean lazy) {
        return new ArrayList<CodeRule>(this.rules.values());
    }

    @Override
    public List<CodeRule> query(QueryTemplate<CodeRule> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(CodeRule rule) {
        return rule.id;
    }

    @Override
    public StringBuffer exportSingle(CodeRule t) {
        return null;
    }

}
