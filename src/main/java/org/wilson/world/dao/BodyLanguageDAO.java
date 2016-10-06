package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.BodyLanguage;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BodyLanguageDAO extends AbstractDAO<BodyLanguage> {
    private static final Logger logger = Logger.getLogger(BodyLanguageDAO.class);
    
    private Map<Integer, BodyLanguage> langs = new HashMap<Integer, BodyLanguage>();
    
    public BodyLanguageDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("body_language.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                BodyLanguage lang = new BodyLanguage();
                lang.id = i + 1;
                lang.name = obj.getString("name");
                lang.image = obj.getString("image");
                lang.indicator = obj.getString("indicator");
                this.langs.put(lang.id, lang);
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
    public void create(BodyLanguage lang) {
    }

    @Override
    public void update(BodyLanguage lang) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public BodyLanguage get(int id, boolean lazy) {
        return this.langs.get(id);
    }

    @Override
    public List<BodyLanguage> getAll(boolean lazy) {
        return new ArrayList<BodyLanguage>(this.langs.values());
    }

    @Override
    public List<BodyLanguage> query(QueryTemplate<BodyLanguage> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(BodyLanguage lang) {
        return lang.id;
    }

    @Override
    public StringBuffer exportSingle(BodyLanguage t) {
        return null;
    }

}
