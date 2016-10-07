package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.MiltonModel;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MiltonModelDAO extends AbstractDAO<MiltonModel> {
    private static final Logger logger = Logger.getLogger(MiltonModelDAO.class);
    
    private Map<Integer, MiltonModel> models = new HashMap<Integer, MiltonModel>();
    
    public MiltonModelDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("milton_model.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                MiltonModel model = new MiltonModel();
                model.id = i + 1;
                model.name = obj.getString("name");
                model.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    model.examples.add(example);
                }
                this.models.put(model.id, model);
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
    public void create(MiltonModel model) {
    }

    @Override
    public void update(MiltonModel model) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public MiltonModel get(int id, boolean lazy) {
        return this.models.get(id);
    }

    @Override
    public List<MiltonModel> getAll(boolean lazy) {
        return new ArrayList<MiltonModel>(this.models.values());
    }

    @Override
    public List<MiltonModel> query(QueryTemplate<MiltonModel> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(MiltonModel model) {
        return model.id;
    }

    @Override
    public StringBuffer exportSingle(MiltonModel t) {
        return null;
    }

}
