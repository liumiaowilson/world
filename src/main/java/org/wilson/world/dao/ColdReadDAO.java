package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.ColdRead;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ColdReadDAO extends AbstractDAO<ColdRead> {
    private static final Logger logger = Logger.getLogger(ColdReadDAO.class);
    
    private Map<Integer, ColdRead> coldreads = new HashMap<Integer, ColdRead>();
    
    public ColdReadDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("cold_read.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ColdRead coldread = new ColdRead();
                coldread.id = i + 1;
                coldread.name = obj.getString("name");
                coldread.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    coldread.examples.add(example);
                }
                this.coldreads.put(coldread.id, coldread);
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
    public void create(ColdRead coldread) {
    }

    @Override
    public void update(ColdRead coldread) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public ColdRead get(int id, boolean lazy) {
        return this.coldreads.get(id);
    }

    @Override
    public List<ColdRead> getAll(boolean lazy) {
        return new ArrayList<ColdRead>(this.coldreads.values());
    }

    @Override
    public List<ColdRead> query(QueryTemplate<ColdRead> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(ColdRead coldread) {
        return coldread.id;
    }

    @Override
    public StringBuffer exportSingle(ColdRead t) {
        return null;
    }

}
