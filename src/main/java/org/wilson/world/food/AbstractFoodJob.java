package org.wilson.world.food;

import org.wilson.world.manager.FoodManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractFoodJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        FoodInfo selected = FoodManager.getInstance().getSelected();
        if(selected == null) {
            FoodManager.getInstance().clear(this.getFrom());
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(FoodInfo info) throws Exception;
}
