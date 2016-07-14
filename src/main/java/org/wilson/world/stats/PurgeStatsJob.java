package org.wilson.world.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.schedule.DefaultJob;

public class PurgeStatsJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(PurgeStatsJob.class);
    
    @Override
    public void execute() {
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = DBUtils.getConnection();
                long current = System.currentTimeMillis();
                long last = current - 30 * 24 * 60 * 60 * 1000L;
                String sql = "delete from stats where time < ?";
                ps = con.prepareStatement(sql);
                ps.setLong(1, last);
                int count = ps.executeUpdate();
                logger.info("Purged " + count + " row(s) from stats.");
            }
            catch(Exception e) {
                logger.error("failed to purge stats", e);
            }
            finally {
                DBUtils.closeQuietly(con, ps, rs);
            }
        }
        else {
            logger.info("Purge stats job is not run in memory mode.");
        }
    }
}
