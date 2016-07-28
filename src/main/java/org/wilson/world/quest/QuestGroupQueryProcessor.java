package org.wilson.world.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.QuestManager;
import org.wilson.world.model.QueryItem;
import org.wilson.world.model.Quest;
import org.wilson.world.query.SystemQueryProcessor;

public class QuestGroupQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Quest Group";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query(Map<String, String> args) {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        String defIdStr = args.get("defId");
        if(!StringUtils.isBlank(defIdStr)) {
            try {
                int defId = Integer.parseInt(defIdStr);
                List<Quest> quests = QuestManager.getInstance().getQuestsByDefId(defId);
                for(Quest quest : quests) {
                    QueryItem item = new QueryItem();
                    item.id = quest.id;
                    item.name = quest.name;
                    item.description = quest.content;
                    item.type = QuestManager.getInstance().getItemTypeName();
                    ret.add(item);
                }
            }
            catch(Exception e) {
            }
        }
        
        return ret;
    }

}
