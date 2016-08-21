package org.wilson.world.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.schedule.DefaultJob;

public abstract class PurgeJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(PurgeJob.class);
    
    public abstract PreparedStatement getPurgeStatement(Connection con) throws SQLException;
    
    public abstract void notifyPurged();
    
    @Override
    public void execute() {
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = DBUtils.getConnection();
                ps = this.getPurgeStatement(con);
                int count = ps.executeUpdate();
                logger.info("Purged " + count + " row(s) by [" + this.getJobName() + "].");
                
                this.notifyPurged();
            }
            catch(Exception e) {
                logger.error("failed to run " + this.getJobName(), e);
            }
            finally {
                DBUtils.closeQuietly(con, ps, rs);
            }
        }
        else {
            logger.info(this.getJobName() + " is not run in memory mode.");
        }
    }
}
