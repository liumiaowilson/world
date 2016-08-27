package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

public interface BalanceMatcher {
    public boolean match(HttpServletRequest request);
}
