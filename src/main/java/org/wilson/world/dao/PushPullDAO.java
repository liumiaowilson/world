package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.PushPull;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PushPullDAO extends AbstractDAO<PushPull> {
    private static final Logger logger = Logger.getLogger(PushPullDAO.class);
    
    private Map<Integer, PushPull> pushpulls = new HashMap<Integer, PushPull>();
    
    public PushPullDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("push_pull.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PushPull pushpull = new PushPull();
                pushpull.id = i + 1;
                pushpull.name = obj.getString("name");
                pushpull.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    pushpull.examples.add(example);
                }
                this.pushpulls.put(pushpull.id, pushpull);
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
    public void create(PushPull pushpull) {
    }

    @Override
    public void update(PushPull pushpull) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public PushPull get(int id, boolean lazy) {
        return this.pushpulls.get(id);
    }

    @Override
    public List<PushPull> getAll(boolean lazy) {
        return new ArrayList<PushPull>(this.pushpulls.values());
    }

    @Override
    public List<PushPull> query(QueryTemplate<PushPull> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(PushPull pushpull) {
        return pushpull.id;
    }

    @Override
    public StringBuffer exportSingle(PushPull t) {
        return null;
    }

}
