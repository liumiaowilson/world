package org.wilson.world.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.model.RhetoricalDevice;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RhetoricalDeviceDAO extends AbstractDAO<RhetoricalDevice> {
    private static final Logger logger = Logger.getLogger(RhetoricalDeviceDAO.class);
    
    private Map<Integer, RhetoricalDevice> devices = new HashMap<Integer, RhetoricalDevice>();
    
    public RhetoricalDeviceDAO() {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("rhetoric.json");
            String json = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(json);
            for(int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RhetoricalDevice device = new RhetoricalDevice();
                device.id = i + 1;
                device.name = obj.getString("name");
                device.definition = obj.getString("definition");
                JSONArray exampleArray = obj.getJSONArray("examples");
                for(int j = 0; j < exampleArray.size(); j++) {
                    String example = exampleArray.getString(j);
                    device.examples.add(example);
                }
                this.devices.put(device.id, device);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void create(RhetoricalDevice device) {
    }

    @Override
    public void update(RhetoricalDevice device) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public RhetoricalDevice get(int id) {
        return this.devices.get(id);
    }

    @Override
    public List<RhetoricalDevice> getAll() {
        return new ArrayList<RhetoricalDevice>(this.devices.values());
    }

    @Override
    public List<RhetoricalDevice> query(QueryTemplate<RhetoricalDevice> template, Object... args) {
        return Collections.emptyList();
    }

    @Override
    public String getItemTableName() {
        return null;
    }

    @Override
    public int getId(RhetoricalDevice device) {
        return device.id;
    }

    @Override
    public StringBuffer exportSingle(RhetoricalDevice t) {
        return null;
    }

}
