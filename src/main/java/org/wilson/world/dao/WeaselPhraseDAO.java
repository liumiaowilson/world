package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.WeaselPhrase;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WeaselPhraseDAO extends AbstractDAO<WeaselPhrase> {
    private static final Logger logger = Logger.getLogger(WeaselPhraseDAO.class);
    
    private Map<Integer, WeaselPhrase> phrases = new HashMap<Integer, WeaselPhrase>();
    
    public WeaselPhraseDAO() {
        this.loadWeaselPhrases();
    }
    
    private void loadWeaselPhrases() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("weasel_phrase.json");
            String content = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(content);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                WeaselPhrase phrase = new WeaselPhrase();
                phrase.id = i + 1;
                phrase.pattern = obj.getString("pattern");
                phrase.usage = obj.getString("usage");
                this.phrases.put(phrase.id, phrase);
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
    public void create(WeaselPhrase phrase) {
    }

    @Override
    public void update(WeaselPhrase phrase) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public WeaselPhrase get(int id, boolean lazy) {
        return this.phrases.get(id);
    }

    @Override
    public List<WeaselPhrase> getAll(boolean lazy) {
        return new ArrayList<WeaselPhrase>(this.phrases.values());
    }

    @Override
    public List<WeaselPhrase> query(QueryTemplate<WeaselPhrase> template, Object... args) {
        return new ArrayList<WeaselPhrase>();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(WeaselPhrase phrase) {
        return phrase.id;
    }

    @Override
    public StringBuffer exportSingle(WeaselPhrase t) {
        return null;
    }

}
