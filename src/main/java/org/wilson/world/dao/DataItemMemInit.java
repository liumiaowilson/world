package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.model.DataItem;

public class DataItemMemInit implements MemInit<DataItem> {

    @Override
    public void init(DAO<DataItem> dao) {
        dao.create(this.createDataItem("user.max_hp", "100"));
        dao.create(this.createDataItem("user.hp", "100"));
        dao.create(this.createDataItem("user.max_mp", "100"));
        dao.create(this.createDataItem("user.mp", "100"));
        dao.create(this.createDataItem("user.max_stamina", "100"));
        dao.create(this.createDataItem("user.stamina", "100"));
        
        dao.create(this.createDataItem("user.speed", "20"));
        dao.create(this.createDataItem("user.strength", "20"));
        dao.create(this.createDataItem("user.construction", "20"));
        dao.create(this.createDataItem("user.dexterity", "20"));
        dao.create(this.createDataItem("user.intelligence", "20"));
        dao.create(this.createDataItem("user.charisma", "20"));
        dao.create(this.createDataItem("user.willpower", "20"));
        dao.create(this.createDataItem("user.luck", "20"));
    }
    
    private DataItem createDataItem(String name, String value) {
        DataItem item = new DataItem();
        item.name = name;
        item.value = value;
        return item;
    }

    @Override
    public List<QueryTemplate<DataItem>> getQueryTemplates() {
        List<QueryTemplate<DataItem>> ret = new ArrayList<QueryTemplate<DataItem>>();
        ret.add(new DataItemDAO.DataItemQueryByNameTemplate());
        return ret;
    }

}
