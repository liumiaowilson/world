package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.BalanceManager;
import org.wilson.world.menu.MenuItem;

public class DefaultBalanceProbator implements BalanceProbator {
    private MenuItem item;
    private BalanceMatcher matcher;
    private int train;
    
    public DefaultBalanceProbator(MenuItem item) {
        this.item = item;
        this.matcher = new DefaultBalanceMatcher(item);
        
        if(this.item.data.containsKey(BalanceManager.BALANCE_TRAIN_NAME)) {
            String trainOpt = (String) this.item.data.get(BalanceManager.BALANCE_TRAIN_NAME);
            if("in".equals(trainOpt)) {
                this.train = -1;
            }
            else if("out".equals(trainOpt)) {
                this.train = 1;
            }
        }
    }
    
    @Override
    public boolean match(HttpServletRequest request) {
        return this.matcher.match(request);
    }

    @Override
    public void doProbation() {
        int bal = BalanceManager.getInstance().getTrainBalance();
        bal += this.train;
        BalanceManager.getInstance().setTrainBalance(bal);
    }

}
