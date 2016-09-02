package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.balance.BalanceProbator;
import org.wilson.world.balance.BalanceResetJob;
import org.wilson.world.balance.BalanceStatus;
import org.wilson.world.balance.DefaultBalanceProbator;
import org.wilson.world.balance.IdeaTaskBalanceMonitor;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;
import org.wilson.world.util.FormatUtils;

public class BalanceManager implements ManagerLifecycle {
    public static final String BALANCE_TRAIN_NAME = "train";
    public static final String BALANCE_MATCHER_NAME = "matcher";
    public static final String BALANCE_ENERGY_NAME = "energy";
    
    private static BalanceManager instance;
    
    private int trainBalance = 0;
    private int trainBalanceLimit;
    
    private int energyBalance = 0;
    private int energyBalanceLimit;
    
    private List<BalanceProbator> probators = new ArrayList<BalanceProbator>();
    
    private BalanceManager() {
        this.trainBalanceLimit = ConfigManager.getInstance().getConfigAsInt("balance.train.limit", 100);
        this.energyBalanceLimit = ConfigManager.getInstance().getConfigAsInt("balance.energy.limit", 100);
        
        ScheduleManager.getInstance().addJob(new BalanceResetJob());
        
        MonitorManager.getInstance().registerMonitorParticipant(new IdeaTaskBalanceMonitor());
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
    
    public int getEnergyBalance() {
        return this.energyBalance;
    }
    
    public int getEnergyBalanceLimit() {
        return this.energyBalanceLimit;
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
    
    public void setEnergyBalance(int bal) {
        this.energyBalance = bal;
        
        if(this.energyBalance > this.energyBalanceLimit) {
            this.energyBalance = this.energyBalanceLimit;
        }
        else if(this.energyBalance < -(this.energyBalanceLimit)) {
            this.energyBalance = -(this.energyBalanceLimit);
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
    
    public boolean canProceed(HttpServletRequest request, BalanceStatus status) {
        if(request == null) {
            return false;
        }
        
        BalanceProbator probator = this.getMatchedProbator(request);
        if(probator == null) {
            return false;
        }
        
        return probator.canProceed(request, status);
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
        
        return item.data.containsKey(BALANCE_TRAIN_NAME) || item.data.containsKey(BALANCE_ENERGY_NAME);
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
        
        if(this.energyBalance >= this.energyBalanceLimit) {
            return BalanceStatus.TooPositive;
        }
        
        if(this.energyBalance <= -(this.energyBalanceLimit)) {
            return BalanceStatus.TooNegative;
        }
        
        return BalanceStatus.Maintained;
    }
    
    public void recoverTrainBalance(int amount) {
        int new_train = this.trainBalance;
        if(this.trainBalance >= 0) {
            new_train = this.trainBalance - amount;
        }
        else {
            new_train = this.trainBalance + amount;
        }
        
        if(new_train * this.trainBalance <= 0) {
            new_train = 0;
        }
        this.setTrainBalance(new_train);
    }
    
    public void recoverEnergyBalance(int amount) {
        int new_energy = this.energyBalance;
        if(this.energyBalance >= 0) {
            new_energy = this.energyBalance - amount;
        }
        else {
            new_energy = this.energyBalance + amount;
        }
        
        if(new_energy * this.energyBalance <= 0) {
            new_energy = 0;
        }
        this.setEnergyBalance(new_energy);
    }
    
    public double getIdeaTaskBalance() {
        int numOfIdeas = IdeaManager.getInstance().getIdeas().size();
        int numOfTasks = TaskManager.getInstance().getIndividualTasks().size();
        
        return FormatUtils.getRoundedValue(numOfIdeas * 1.0 / numOfTasks);
    }
}
