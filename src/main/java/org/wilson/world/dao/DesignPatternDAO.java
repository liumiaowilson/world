package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.DesignPattern;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DesignPatternDAO extends AbstractDAO<DesignPattern> {
    private static final Logger logger = Logger.getLogger(DesignPatternDAO.class);
    
    private Map<Integer, DesignPattern> patterns = new HashMap<Integer, DesignPattern>();
    
    public DesignPatternDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("design_pattern.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                DesignPattern pattern = new DesignPattern();
                pattern.id = i + 1;
                pattern.name = obj.getString("name");
                pattern.definition = obj.getString("definition");
                pattern.type = obj.getString("type");
                pattern.image = obj.getString("image");
                this.patterns.put(pattern.id, pattern);
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
    public void create(DesignPattern pattern) {
    }

    @Override
    public void update(DesignPattern pattern) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public DesignPattern get(int id, boolean lazy) {
        return this.patterns.get(id);
    }

    @Override
    public List<DesignPattern> getAll(boolean lazy) {
        return new ArrayList<DesignPattern>(this.patterns.values());
    }

    @Override
    public List<DesignPattern> query(QueryTemplate<DesignPattern> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(DesignPattern pattern) {
        return pattern.id;
    }

    @Override
    public StringBuffer exportSingle(DesignPattern t) {
        return null;
    }

}
