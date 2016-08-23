package org.wilson.world.countdown;

import java.util.List;
import java.util.TimeZone;

public interface ICountdownProvider {
    public List<ICountdown> getCountdowns(TimeZone tz);
}
