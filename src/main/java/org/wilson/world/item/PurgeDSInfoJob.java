package org.wilson.world.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.wilson.world.job.PurgeJob;
import org.wilson.world.util.TimeUtils;

public class PurgeDSInfoJob extends PurgeJob {
    @Override
    public PreparedStatement getPurgeStatement(Connection con) throws SQLException {
        long current = System.currentTimeMillis();
        long last = current - 30 * TimeUtils.DAY_DURATION;
        String sql = "delete from data_size_infos where time < ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, last);
        return ps;
    }

    @Override
    public void notifyPurged() {
    }
}
