package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.model.Action;

public class ActionMemInit implements MemInit<Action> {
    @Override
    public void init(DAO<Action> dao) {
    }

    @Override
    public List<QueryTemplate<Action>> getQueryTemplates() {
        List<QueryTemplate<Action>> ret = new ArrayList<QueryTemplate<Action>>();
        ret.add(new ActionDAO.ActionQueryByNameTemplate());
        return ret;
    }
}
