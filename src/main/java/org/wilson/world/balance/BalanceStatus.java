package org.wilson.world.balance;

public enum BalanceStatus {
    Maintained(false, "The balance is maintained."),
    TooOutward(true, "The system is leaning too outward."),
    TooInward(true, "The system is leaning too inward."),
    TooPositive(true, "The system is leaning too positive."),
    TooNegative(true, "The system is leaning too negative.");
    
    private boolean broken;
    private String message;
    
    BalanceStatus(boolean broken, String message) {
        this.broken = broken;
        this.message = message;
    }

    public boolean isBroken() {
        return broken;
    }

    public String getMessage() {
        return message;
    }
}
