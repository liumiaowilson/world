package org.wilson.world.flashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.FlashCardManager;
import org.wilson.world.model.FlashCard;
import org.wilson.world.model.QueryItem;
import org.wilson.world.query.SystemQueryProcessor;

public class FlashCardQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Flash Card";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query(Map<String, String> args) {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        int setId = -1;
        try {
            setId = Integer.parseInt(args.get("setId"));
        }
        catch(Exception e) {
        }
        if(setId < 0) {
            return Collections.emptyList();
        }
        
        for(FlashCard card : FlashCardManager.getInstance().getFlashCardsBySet(setId)) {
            QueryItem item = new QueryItem();
            item.id = card.id;
            item.name = card.name;
            item.description = card.top;
            item.type = FlashCardManager.getInstance().getItemTypeName();
            ret.add(item);
        }
        
        return ret;
    }
}
