package org.wilson.world.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.Strategy;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StrategyDAO extends AbstractDAO<Strategy> {
    private static final Logger logger = Logger.getLogger(StrategyDAO.class);
    
    private Map<Integer, Strategy> strategies = new HashMap<Integer, Strategy>();
    
    public StrategyDAO() {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("strategy.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            int id = 1;
            for(int i = 0; i < array.size(); i++) {
                JSONObject groupObj = array.getJSONObject(i);
                String groupName = groupObj.getString("name");
                JSONArray groupArray = groupObj.getJSONArray("skills");
                for(int k = 0; k < groupArray.size(); k++) {
                    JSONObject obj = groupArray.getJSONObject(k);
                    Strategy strategy = new Strategy();
                    strategy.id = id++;
                    strategy.name = obj.getString("name");
                    strategy.group = groupName;
                    strategy.definition = obj.getString("definition");
                    JSONArray exampleArray = obj.getJSONArray("examples");
                    for(int j = 0; j < exampleArray.size(); j++) {
                        strategy.examples.add(exampleArray.getString(j));
                    }
                    this.strategies.put(strategy.id, strategy);
                }
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void create(Strategy strategy) {
    }

    @Override
    public void update(Strategy strategy) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public Strategy get(int id) {
        return this.strategies.get(id);
    }

    @Override
    public List<Strategy> getAll() {
        return new ArrayList<Strategy>(this.strategies.values());
    }

    @Override
    public List<Strategy> query(QueryTemplate<Strategy> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(Strategy strategy) {
        return strategy.id;
    }

    @Override
    public StringBuffer exportSingle(Strategy t) {
        return null;
    }

}
