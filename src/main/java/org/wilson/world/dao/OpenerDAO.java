package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.Opener;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OpenerDAO extends AbstractDAO<Opener> {
    private static final Logger logger = Logger.getLogger(OpenerDAO.class);
    
    private Map<Integer, Opener> openers = new HashMap<Integer, Opener>();
    
    public OpenerDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("opener.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Opener opener = new Opener();
                opener.id = i + 1;
                opener.name = obj.getString("name");
                opener.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    opener.examples.add(example);
                }
                this.openers.put(opener.id, opener);
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
    public void create(Opener opener) {
    }

    @Override
    public void update(Opener opener) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public Opener get(int id, boolean lazy) {
        return this.openers.get(id);
    }

    @Override
    public List<Opener> getAll(boolean lazy) {
        return new ArrayList<Opener>(this.openers.values());
    }

    @Override
    public List<Opener> query(QueryTemplate<Opener> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(Opener opener) {
        return opener.id;
    }

    @Override
    public StringBuffer exportSingle(Opener t) {
        return null;
    }

}
