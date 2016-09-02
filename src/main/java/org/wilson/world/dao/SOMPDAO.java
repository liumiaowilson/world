package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.SOMP;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SOMPDAO extends AbstractDAO<SOMP> {
    private static final Logger logger = Logger.getLogger(SOMPDAO.class);
    
    private Map<Integer, SOMP> patterns = new HashMap<Integer, SOMP>();
    
    public SOMPDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("somp.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SOMP pattern = new SOMP();
                pattern.id = i + 1;
                pattern.name = obj.getString("name");
                pattern.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    JSONObject exampleObj = exampleArray.getJSONObject(j);
                    pattern.examples.put(exampleObj.getString("a"), exampleObj.getString("b"));
                }
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
    public void create(SOMP pattern) {
    }

    @Override
    public void update(SOMP pattern) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public SOMP get(int id) {
        return this.patterns.get(id);
    }

    @Override
    public List<SOMP> getAll() {
        return new ArrayList<SOMP>(this.patterns.values());
    }

    @Override
    public List<SOMP> query(QueryTemplate<SOMP> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(SOMP pattern) {
        return pattern.id;
    }

    @Override
    public StringBuffer exportSingle(SOMP t) {
        return null;
    }

}
