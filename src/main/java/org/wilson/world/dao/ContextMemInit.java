package org.wilson.world.dao;

import java.util.Collections;
import java.util.List;

import org.wilson.world.model.Context;

public class ContextMemInit implements MemInit<Context> {
    @Override
    public void init(DAO<Context> dao) {
        dao.create(this.createContext("Work", "orange"));
        dao.create(this.createContext("Leisure", "limegreen"));
    }
    
    private Context createContext(String name, String color) {
        Context context = new Context();
        context.name = name;
        context.color = color;
        context.description = name;
        return context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QueryTemplate<Context>> getQueryTemplates() {
        return Collections.EMPTY_LIST;
    }
}
