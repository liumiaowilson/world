package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.Hoop;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HoopDAO extends AbstractDAO<Hoop> {
    private static final Logger logger = Logger.getLogger(HoopDAO.class);
    
    private Map<Integer, Hoop> hoops = new HashMap<Integer, Hoop>();
    
    public HoopDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("hoop.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Hoop hoop = new Hoop();
                hoop.id = i + 1;
                hoop.name = obj.getString("name");
                hoop.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    hoop.examples.add(example);
                }
                this.hoops.put(hoop.id, hoop);
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
    public void create(Hoop hoop) {
    }

    @Override
    public void update(Hoop hoop) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public Hoop get(int id, boolean lazy) {
        return this.hoops.get(id);
    }

    @Override
    public List<Hoop> getAll(boolean lazy) {
        return new ArrayList<Hoop>(this.hoops.values());
    }

    @Override
    public List<Hoop> query(QueryTemplate<Hoop> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(Hoop hoop) {
        return hoop.id;
    }

    @Override
    public StringBuffer exportSingle(Hoop t) {
        return null;
    }

}
