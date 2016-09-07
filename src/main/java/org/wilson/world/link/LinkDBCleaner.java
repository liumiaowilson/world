package org.wilson.world.link;

import java.util.List;

import org.wilson.world.item.DBCleaner;
import org.wilson.world.manager.ItemManager;
import org.wilson.world.manager.LinkManager;
import org.wilson.world.model.Link;

public class LinkDBCleaner implements DBCleaner {

    @Override
    public void clean() {
        List<Link> links = LinkManager.getInstance().getLinks();
        for(Link link : links) {
            Object target = ItemManager.getInstance().getItem(link.itemType, String.valueOf(link.itemId));
            if(target == null) {
                LinkManager.getInstance().deleteLink(link.id);
            }
        }
    }

}
