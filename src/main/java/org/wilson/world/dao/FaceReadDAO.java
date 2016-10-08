package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.FaceRead;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FaceReadDAO extends AbstractDAO<FaceRead> {
    private static final Logger logger = Logger.getLogger(FaceReadDAO.class);
    
    private Map<Integer, FaceRead> reads = new HashMap<Integer, FaceRead>();
    
    @SuppressWarnings("rawtypes")
    public FaceReadDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("face_read.json");
            String json = IOUtils.toString(in);
            int id = 1;
            JSONObject obj = JSONObject.fromObject(json);
            Iterator iter = obj.keys();
            while(iter.hasNext()) {
                String key = (String) iter.next();
                JSONArray array = obj.getJSONArray(key);
                for(int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    FaceRead read = new FaceRead();
                    read.id = id++;
                    read.type = key;
                    read.name = item.getString("name");
                    read.definition = item.getString("definition");
                    read.image = item.getString("image");
                    read.indicator = item.getString("indicator");
                    this.reads.put(read.id, read);
                }
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
    public void create(FaceRead read) {
    }

    @Override
    public void update(FaceRead read) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public FaceRead get(int id, boolean lazy) {
        return this.reads.get(id);
    }

    @Override
    public List<FaceRead> getAll(boolean lazy) {
        return new ArrayList<FaceRead>(this.reads.values());
    }

    @Override
    public List<FaceRead> query(QueryTemplate<FaceRead> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(FaceRead read) {
        return read.id;
    }

    @Override
    public StringBuffer exportSingle(FaceRead t) {
        return null;
    }

}
