package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.ZodiacSign;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ZodiacSignDAO extends AbstractDAO<ZodiacSign> {
    private static final Logger logger = Logger.getLogger(ZodiacSignDAO.class);
    
    private Map<Integer, ZodiacSign> signs = new HashMap<Integer, ZodiacSign>();
    
    public ZodiacSignDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("zodiac_sign.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ZodiacSign sign = new ZodiacSign();
                sign.id = i + 1;
                sign.name = obj.getString("name");
                
                try {
                    String from = obj.getString("from");
                    String [] fromParts = from.trim().split("/");
                    sign.fromMonth = Integer.parseInt(fromParts[0].trim());
                    sign.fromDay = Integer.parseInt(fromParts[1].trim());
                    
                    String to = obj.getString("to");
                    String [] toParts = to.trim().split("/");
                    sign.toMonth = Integer.parseInt(toParts[0].trim());
                    sign.toDay = Integer.parseInt(toParts[1].trim());
                }
                catch(Exception e) {
                    logger.error(e);
                    continue;
                }
                
                sign.description = obj.getString("description");
                sign.strengths = obj.getString("strengths");
                sign.weaknesses = obj.getString("weaknesses");
                sign.likes = obj.getString("likes");
                sign.dislikes = obj.getString("dislikes");
                sign.compatibility = obj.getString("compatibility");
                sign.partnership = obj.getString("partnership");
                
                this.signs.put(sign.id, sign);
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
    public void create(ZodiacSign sign) {
    }

    @Override
    public void update(ZodiacSign sign) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public ZodiacSign get(int id) {
        return this.signs.get(id);
    }

    @Override
    public List<ZodiacSign> getAll() {
        return new ArrayList<ZodiacSign>(this.signs.values());
    }

    @Override
    public List<ZodiacSign> query(QueryTemplate<ZodiacSign> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(ZodiacSign sign) {
        return sign.id;
    }

    @Override
    public StringBuffer exportSingle(ZodiacSign t) {
        return null;
    }

}
