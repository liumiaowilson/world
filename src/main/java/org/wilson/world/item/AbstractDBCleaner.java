package org.wilson.world.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;

public abstract class AbstractDBCleaner implements DBCleaner {
    private static final Logger logger = Logger.getLogger(AbstractDBCleaner.class);
    
    @Override
    public void clean() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = this.getSQL();
            ps = con.prepareStatement(sql);
            int count = ps.executeUpdate();
            if(count > 0) {
                logger.info("Deleted [" + count + "] rows by " + this.getClass().getSimpleName());
            }
        }
        catch(Exception e) {
            logger.error("failed to clean", e);
            throw new DataException("failed to clean");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
        
        this.notifyAfterClean();
    }

    public abstract String getSQL();
    
    public abstract void notifyAfterClean();
}
