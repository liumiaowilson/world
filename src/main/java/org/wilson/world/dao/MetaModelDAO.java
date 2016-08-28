package org.wilson.world.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.MetaModel;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MetaModelDAO extends AbstractDAO<MetaModel> {
    private static final Logger logger = Logger.getLogger(MetaModelDAO.class);
    
    private Map<Integer, MetaModel> models = new HashMap<Integer, MetaModel>();
    
    public MetaModelDAO() {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("meta_model.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                MetaModel model = new MetaModel();
                model.id = i + 1;
                model.name = obj.getString("name");
                model.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    JSONObject exampleObj = exampleArray.getJSONObject(j);
                    model.examples.put(exampleObj.getString("a"), exampleObj.getString("b"));
                }
                this.models.put(model.id, model);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void create(MetaModel model) {
    }

    @Override
    public void update(MetaModel model) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public MetaModel get(int id) {
        return this.models.get(id);
    }

    @Override
    public List<MetaModel> getAll() {
        return new ArrayList<MetaModel>(this.models.values());
    }

    @Override
    public List<MetaModel> query(QueryTemplate<MetaModel> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(MetaModel model) {
        return model.id;
    }

    @Override
    public StringBuffer exportSingle(MetaModel t) {
        return null;
    }

}
