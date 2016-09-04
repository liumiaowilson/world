package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.wilson.world.manager.BalanceManager;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.menu.MenuItem;

public class DefaultBalanceProbator implements BalanceProbator {
    private static final Logger logger = Logger.getLogger(DefaultBalanceProbator.class);
    
    private MenuItem item;
    private BalanceMatcher matcher;
    private int train;
    private int energy;
    
    @SuppressWarnings("rawtypes")
    public DefaultBalanceProbator(MenuItem item) {
        this.item = item;
        
        if(this.item.data.containsKey(BalanceManager.BALANCE_MATCHER_NAME)) {
            String matcherStr = (String) this.item.data.get(BalanceManager.BALANCE_MATCHER_NAME);
            try {
                Class clazz = Class.forName(matcherStr);
                BalanceMatcher matcher = (BalanceMatcher) clazz.newInstance();
                if(matcher instanceof DefaultBalanceMatcher) {
                    ((DefaultBalanceMatcher)matcher).setMenuItem(this.item);
                }
                
                this.matcher = matcher;
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
        
        if(this.matcher == null) {
            this.matcher = new DefaultBalanceMatcher(this.item);
        }
        
        if(this.item.data.containsKey(BalanceManager.BALANCE_TRAIN_NAME)) {
            String trainOpt = (String) this.item.data.get(BalanceManager.BALANCE_TRAIN_NAME);
            if("in".equals(trainOpt)) {
                this.train = -1;
            }
            else if("out".equals(trainOpt)) {
                this.train = 1;
            }
        }
        
        if(this.item.data.containsKey(BalanceManager.BALANCE_ENERGY_NAME)) {
            String energyOpt = (String) this.item.data.get(BalanceManager.BALANCE_ENERGY_NAME);
            if("negative".equals(energyOpt)) {
                this.energy = -1;
            }
            else if("positive".equals(energyOpt)) {
                this.energy = 1;
            }
        }
    }
    
    @Override
    public boolean match(HttpServletRequest request) {
        return this.matcher.match(request);
    }

    @Override
    public boolean doProbation() {
        boolean skip = false;
        int p = 0;
        int karma = CharManager.getInstance().getKarma();
        if(karma > 0) {
            p = 50 * karma / 100;
        }
        
        if(p > 0) {
            if(BalanceManager.getInstance().isBreakingTrainBalance(this.train) && DiceManager.getInstance().dice(p)) {
                skip = true;
            }
        }
        
        if(!skip) {
            int bal = BalanceManager.getInstance().getTrainBalance();
            bal += this.train;
            BalanceManager.getInstance().setTrainBalance(bal);
        }
        
        if(p > 0) {
            if(BalanceManager.getInstance().isBreakingEnergyBalance(this.train) && DiceManager.getInstance().dice(p)) {
                skip = true;
            }
        }
        
        if(!skip) {
            int energy = BalanceManager.getInstance().getEnergyBalance();
            energy += this.energy;
            BalanceManager.getInstance().setEnergyBalance(energy);
        }
        
        return true;
    }

    @Override
    public boolean canProceed(HttpServletRequest request, BalanceStatus status) {
        if(BalanceStatus.TooOutward == status && this.train > 0) {
            return false;
        }
        
        if(BalanceStatus.TooInward == status && this.train < 0) {
            return false;
        }
        
        if(BalanceStatus.TooPositive == status && this.energy > 0) {
            return false;
        }
        
        if(BalanceStatus.TooNegative == status && this.energy < 0) {
            return false;
        }
        
        return true;
    }

}
