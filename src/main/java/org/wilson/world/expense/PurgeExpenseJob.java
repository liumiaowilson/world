package org.wilson.world.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.job.PurgeJob;
import org.wilson.world.manager.ExpenseItemManager;
import org.wilson.world.util.TimeUtils;

public class PurgeExpenseJob extends PurgeJob {
    @Override
    public PreparedStatement getPurgeStatement(Connection con) throws SQLException {
        long current = System.currentTimeMillis();
        long last = current - 30 * TimeUtils.DAY_DURATION;
        String sql = "delete from expense_items where time < ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, last);
        return ps;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyPurged() {
        DAO dao = ExpenseItemManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }
}
