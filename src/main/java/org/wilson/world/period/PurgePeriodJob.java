package org.wilson.world.period;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.job.PurgeJob;
import org.wilson.world.manager.PeriodManager;
import org.wilson.world.util.TimeUtils;

public class PurgePeriodJob extends PurgeJob {
    @Override
    public PreparedStatement getPurgeStatement(Connection con) throws SQLException {
        long current = System.currentTimeMillis();
        long last = current - 12 * 30 * TimeUtils.DAY_DURATION;
        String sql = "delete from periods where time < ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, last);
        return ps;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyPurged() {
        DAO dao = PeriodManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }
}
