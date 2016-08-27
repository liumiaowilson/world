package org.wilson.world.useritem;

import org.wilson.world.manager.NotifyManager;

public class TicketEffect implements UserItemEffect {

    @Override
    public void takeEffect() {
        NotifyManager.getInstance().notifyInfo("Tickets cannot be used, but consumed.");
    }

}
