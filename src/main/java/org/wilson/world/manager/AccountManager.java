package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.account.AccountIdeaConverter;
import org.wilson.world.account.UpdateAccountTaskGenerator;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Account;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.TimeUtils;

public class AccountManager implements ItemTypeProvider {
    public static final String NAME = "account";
    
    private static AccountManager instance;
    
    private DAO<Account> dao = null;
    
    @SuppressWarnings("unchecked")
    private AccountManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Account.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        TaskSeedManager.getInstance().addTaskGenerator(new UpdateAccountTaskGenerator());
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new AccountIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Account account : getAccounts()) {
                    if(account.name.contains(text) || account.description.contains(text)) {
                        Content content = new Content();
                        content.id = account.id;
                        content.name = account.name;
                        content.description = account.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AccountManager getInstance() {
        if(instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    public void createAccount(Account account) {
        ItemManager.getInstance().checkDuplicate(account);
        
        this.dao.create(account);
    }
    
    public Account getAccount(int id) {
        Account account = this.dao.get(id);
        if(account != null) {
            return account;
        }
        else {
            return null;
        }
    }
    
    public List<Account> getAccounts() {
        List<Account> result = new ArrayList<Account>();
        for(Account account : this.dao.getAll()) {
            result.add(account);
        }
        return result;
    }
    
    public void updateAccount(Account account) {
        this.dao.update(account);
        
        this.setUpdatedTime(System.currentTimeMillis());
    }
    
    public void setUpdatedTime(long time) {
        DataManager.getInstance().setValue("account.last_update", time);
    }
    
    public long getUpdatedTime() {
        return DataManager.getInstance().getValueAsLong("account.last_update");
    }
    
    public String getUpdatedTime(TimeZone tz) {
        long last = this.getUpdatedTime();
        if(last <= 0) {
            return "NA";
        }
        else {
            return TimeUtils.toDateTimeString(last, tz);
        }
    }
    
    public void deleteAccount(int id) {
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
        return target instanceof Account;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Account account = (Account)target;
        return String.valueOf(account.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }

    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Account account = (Account)target;
        return account.name;
    }
}
