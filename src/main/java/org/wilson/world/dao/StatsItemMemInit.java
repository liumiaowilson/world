package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.model.StatsItem;

public class StatsItemMemInit implements MemInit<StatsItem> {

    @Override
    public void init(DAO<StatsItem> dao) {
    }

    @Override
    public List<QueryTemplate<StatsItem>> getQueryTemplates() {
        List<QueryTemplate<StatsItem>> ret = new ArrayList<QueryTemplate<StatsItem>>();
        ret.add(new StatsItemDAO.StatsItemQueryAllTemplate());
        return ret;
    }

}
