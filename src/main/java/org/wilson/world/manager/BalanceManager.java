package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.balance.BalanceProbator;
import org.wilson.world.balance.BalanceStatus;
import org.wilson.world.balance.DefaultBalanceProbator;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class BalanceManager implements ManagerLifecycle {
    public static final String BALANCE_TRAIN_NAME = "train";
    public static final String BALANCE_MATCHER_NAME = "matcher";
    
    private static BalanceManager instance;
    
    private int trainBalance = 0;
    private int trainBalanceLimit;
    
    private List<BalanceProbator> probators = new ArrayList<BalanceProbator>();
    
    private BalanceManager() {
        this.trainBalanceLimit = ConfigManager.getInstance().getConfigAsInt("balance.train.limit", 100);
    }
    
    public static BalanceManager getInstance() {
        if(instance == null) {
            instance = new BalanceManager();
        }
        
        return instance;
    }
    
    public int getTrainBalance() {
        return this.trainBalance;
    }
    
    public int getTrainBalanceLimit() {
        return this.trainBalanceLimit;
    }
    
    public void setTrainBalance(int bal) {
        this.trainBalance = bal;
        
        if(this.trainBalance > this.trainBalanceLimit) {
            this.trainBalance = this.trainBalanceLimit;
        }
        else if(this.trainBalance < -(this.trainBalanceLimit)) {
            this.trainBalance = -(this.trainBalanceLimit);
        }
    }
    
    protected BalanceProbator getMatchedProbator(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        
        for(BalanceProbator probator : this.probators) {
            if(probator.match(request)) {
                return probator;
            }
        }
        
        return null;
    }
    
    public boolean isUnderProbation(HttpServletRequest request) {
        if(request == null) {
            return false;
        }
        
        BalanceProbator probator = this.getMatchedProbator(request);
        return probator != null;
    }
    
    public boolean probate(HttpServletRequest request) {
        if(request == null) {
            return false;
        }
        
        BalanceProbator probator = this.getMatchedProbator(request);
        if(probator == null) {
            return false;
        }
        
        return probator.doProbation();
    }
    
    private boolean acceptMenuItem(MenuItem item) {
        if(item == null) {
            return false;
        }
        
        if(MenuItemRole.Menu != item.role) {
            return false;
        }
        
        if(item.data.isEmpty()) {
            return false;
        }
        
        return item.data.containsKey(BALANCE_TRAIN_NAME);
    }

    @Override
    public void start() {
        List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
        
        for(String menuId : menuIds) {
            MenuItem item = MenuManager.getInstance().getMenuItem(menuId);
            if(this.acceptMenuItem(item)) {
                DefaultBalanceProbator probator = new DefaultBalanceProbator(item);
                this.probators.add(probator);
            }
        }
    }

    @Override
    public void shutdown() {
    }
    
    public BalanceStatus check() {
        if(this.trainBalance >= this.trainBalanceLimit) {
            return BalanceStatus.TooOutward;
        }
        
        if(this.trainBalance <= -(this.trainBalanceLimit)) {
            return BalanceStatus.TooInward;
        }
        
        return BalanceStatus.Maintained;
    }
}
