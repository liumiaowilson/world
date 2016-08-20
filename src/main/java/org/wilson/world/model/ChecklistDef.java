package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.checklist.ChecklistDefItem;

public class ChecklistDef {
    public int id;
    
    public String name;
    
    public String description;
    
    public String content;
    
    public List<ChecklistDefItem> items = new ArrayList<ChecklistDefItem>();
}
