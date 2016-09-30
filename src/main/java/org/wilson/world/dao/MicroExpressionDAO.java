package org.wilson.world.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.MicroExpression;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MicroExpressionDAO extends AbstractDAO<MicroExpression> {
    private static final Logger logger = Logger.getLogger(MicroExpressionDAO.class);
    
    private Map<Integer, MicroExpression> expressions = new HashMap<Integer, MicroExpression>();
    
    public MicroExpressionDAO() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("micro_expression.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                MicroExpression expression = new MicroExpression();
                expression.id = i + 1;
                expression.name = obj.getString("name");
                JSONArray definitionArray = obj.getJSONArray("definition");
                expression.definition = new String[definitionArray.size()];
                for(int j = 0; j < definitionArray.size(); j++) {
                    String definition = definitionArray.getString(j);
                    expression.definition[j] = definition;
                }
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    expression.examples.add(example);
                }
                this.expressions.put(expression.id, expression);
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
    public void create(MicroExpression expression) {
    }

    @Override
    public void update(MicroExpression expression) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public MicroExpression get(int id, boolean lazy) {
        return this.expressions.get(id);
    }

    @Override
    public List<MicroExpression> getAll(boolean lazy) {
        return new ArrayList<MicroExpression>(this.expressions.values());
    }

    @Override
    public List<MicroExpression> query(QueryTemplate<MicroExpression> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(MicroExpression expression) {
        return expression.id;
    }

    @Override
    public StringBuffer exportSingle(MicroExpression t) {
        return null;
    }

}
