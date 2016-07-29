package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Account;

public class AccountManager implements ItemTypeProvider {
    public static final String NAME = "account";
    
    private static AccountManager instance;
    
    private DAO<Account> dao = null;
    
    @SuppressWarnings("unchecked")
    private AccountManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Account.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static AccountManager getInstance() {
        if(instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    public void createAccount(Account account) {
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

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
