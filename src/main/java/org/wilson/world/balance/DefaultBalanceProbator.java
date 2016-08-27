package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.wilson.world.manager.BalanceManager;
import org.wilson.world.menu.MenuItem;

public class DefaultBalanceProbator implements BalanceProbator {
    private static final Logger logger = Logger.getLogger(DefaultBalanceProbator.class);
    
    private MenuItem item;
    private BalanceMatcher matcher;
    private int train;
    
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
