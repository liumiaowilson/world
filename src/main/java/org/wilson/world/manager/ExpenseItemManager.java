package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.expense.ExpenseItemIdeaConverter;
import org.wilson.world.expense.ExpenseRecord;
import org.wilson.world.expense.ExpenseReport;
import org.wilson.world.expense.ExpenseStatsItem;
import org.wilson.world.expense.PurgeExpenseJob;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ExpenseItem;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.util.UTCInfo;

public class ExpenseItemManager implements ItemTypeProvider {
    public static final String NAME = "expense_item";
    
    private static ExpenseItemManager instance;
    
    private DAO<ExpenseItem> dao = null;
    
    private List<String> types = new ArrayList<String>();
    
    private ExpenseItem last = null;
    
    @SuppressWarnings("unchecked")
    private ExpenseItemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ExpenseItem.class);
        
        this.loadTypes();
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new ExpenseItemIdeaConverter());
        
        ScheduleManager.getInstance().addJob(new PurgeExpenseJob());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ExpenseItem item : getExpenseItems()) {
                    boolean found = item.name.contains(text) || item.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = item.id;
                        content.name = item.name;
                        content.description = item.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ExpenseItemManager getInstance() {
        if(instance == null) {
            instance = new ExpenseItemManager();
        }
        return instance;
    }
    
    private void loadTypes() {
        String types = ConfigManager.getInstance().getConfig("expense.types", "Food,Clothes,Travel,Gift,Other");
        for(String type : types.split(",")) {
            this.types.add(type);
        }
        
        Collections.sort(this.types);
        if(this.types.contains("Other")) {
            this.types.remove("Other");
            this.types.add("Other");
        }
    }
    
    public void createExpenseItem(ExpenseItem item) {
        ItemManager.getInstance().checkDuplicate(item);
        
        this.dao.create(item);
        
        this.last = item;
    }
    
    public ExpenseItem getExpenseItem(int id) {
        ExpenseItem item = this.dao.get(id);
        if(item != null) {
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<ExpenseItem> getExpenseItems() {
        List<ExpenseItem> result = new ArrayList<ExpenseItem>();
        for(ExpenseItem item : this.dao.getAll()) {
            result.add(item);
        }
        return result;
    }
    
    public void updateExpenseItem(ExpenseItem item) {
        this.dao.update(item);
    }
    
    public void deleteExpenseItem(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof ExpenseItem;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ExpenseItem item = (ExpenseItem)target;
        return String.valueOf(item.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<String> getTypes() {
        return this.types;
    }
    
    public ExpenseItem getLastCreatedExpenseItem() {
        return this.last;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return null;
    }
    
    public Map<String, Double> getExpenseItemStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        Map<String, Integer> data = new HashMap<String, Integer>();
        int sum = 0;
        for(ExpenseItem item : this.getExpenseItems()) {
            String type = item.type;
            Integer i = data.get(type);
            if(i == null) {
                i = 0;
            }
            i += item.amount;
            sum += item.amount;
            data.put(type, i);
        }
        
        for(Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            int val = entry.getValue();
            double pct = FormatUtils.getRoundedValue(val * 100.0 / sum);
            ret.put(key, pct);
        }
        
        return ret;
    }
    
    public Map<Long, ExpenseStatsItem> getExpenseItemTrend(TimeZone tz) {
        Map<Long, ExpenseStatsItem> ret = new HashMap<Long, ExpenseStatsItem>();
        
        for(ExpenseItem item : this.getExpenseItems()) {
            long time = item.time;
            UTCInfo info = TimeUtils.getDateUTCInfo(time, tz);
            ExpenseStatsItem statsItem = ret.get(info.time);
            if(statsItem == null) {
                statsItem = new ExpenseStatsItem();
                ret.put(info.time, statsItem);
            }
            statsItem.display = info.display;
            statsItem.amount += item.amount;
        }
        
        return ret;
    }
    
    public List<ExpenseItem> getExpenseItemsByType(String type) {
        if(StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        
        List<ExpenseItem> ret = new ArrayList<ExpenseItem>();
        
        for(ExpenseItem item : this.getExpenseItems()) {
            if(type.equals(item.type)) {
                ret.add(item);
            }
        }
        
        return ret;
    }
    
    public List<ExpenseReport> getExpenseReports() {
        List<ExpenseReport> ret = new ArrayList<ExpenseReport>();
        
        for(String type : this.getTypes()) {
            List<ExpenseItem> items = this.getExpenseItemsByType(type);
            if(items.isEmpty()) {
                continue;
            }
            
            int total = 0;
            for(ExpenseItem item : items) {
                total += item.amount;
            }
            ExpenseReport report = new ExpenseReport();
            report.name = type;
            report.total = total;
            report.average = FormatUtils.getRoundedValue(total * 1.0 / items.size());
            ret.add(report);
        }
        
        return ret;
    }
    
    public List<ExpenseRecord> getExpenseRecords() {
        Map<String, ExpenseRecord> data = new HashMap<String, ExpenseRecord>();
        for(ExpenseItem item : this.getExpenseItems()) {
            String key = item.name.trim() + "_" + item.type + "_" + item.amount;
            ExpenseRecord record = data.get(key);
            if(record == null) {
                record = new ExpenseRecord();
                record.name = item.name.trim();
                record.type = item.type;
                record.amount = item.amount;
                data.put(key, record);
            }
            record.count += 1;
        }
        
        return new ArrayList<ExpenseRecord>(data.values());
    }
    
    public List<ExpenseRecord> getTopExpenseRecords(int n) {
        List<ExpenseRecord> ret = new ArrayList<ExpenseRecord>();
        
        List<ExpenseRecord> records = this.getExpenseRecords();
        Collections.sort(records, new Comparator<ExpenseRecord>(){

            @Override
            public int compare(ExpenseRecord o1, ExpenseRecord o2) {
                return -Integer.compare(o1.count, o2.count);
            }
            
        });
        
        for(int i = 0; i < n && i < records.size(); i++) {
            ret.add(records.get(i));
        }
        
        return ret;
    }
    
    public List<ExpenseRecord> getTopExpenseRecords() {
        int limit = ConfigManager.getInstance().getConfigAsInt("expense_record.top.limit", 10);
        return this.getTopExpenseRecords(limit);
    }
}
