package org.wilson.world.shop;

import org.wilson.world.manager.ShopManager;
import org.wilson.world.schedule.DefaultJob;

public class ShopRestockJob extends DefaultJob {

    @Override
    public void execute() {
        ShopManager.getInstance().restock();
    }

}
