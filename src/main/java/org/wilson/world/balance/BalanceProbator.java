package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

public interface BalanceProbator {
    public boolean match(HttpServletRequest request);
    
    public void doProbation();
}
