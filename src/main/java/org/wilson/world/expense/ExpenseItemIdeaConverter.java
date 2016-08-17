package org.wilson.world.expense;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ExpenseItemManager;
import org.wilson.world.model.ExpenseItem;
import org.wilson.world.model.Idea;

public class ExpenseItemIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Expense Item";
    }
    
    private String getType(String type) {
        for(String t : ExpenseItemManager.getInstance().getTypes()) {
            if(t.equalsIgnoreCase(type)) {
                return t;
            }
        }
        
        return null;
    }

    @Override
    public Object convert(Idea idea) {
        try {
            String content = idea.content;
            String [] items = content.split(",");
            
            ExpenseItem item = new ExpenseItem();
            item.name = items[0];
            String type = this.getType(items[1]);
            if(type == null) {
                type = "Other";
            }
            item.type = type;
            item.description = item.name;
            item.amount = Integer.parseInt(items[2]);
            item.time = System.currentTimeMillis();
            return item;
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    public void save(Object converted) {
        ExpenseItemManager.getInstance().createExpenseItem((ExpenseItem) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToExpenseItem;
    }

}
