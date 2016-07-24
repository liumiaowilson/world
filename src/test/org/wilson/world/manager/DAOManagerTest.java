package org.wilson.world.manager;

import org.junit.Test;

public class DAOManagerTest {

    @Test
    public void testEscape() {
        String sql = "a'b";
        sql = sql.replaceAll("'", "\\\\'");
        System.out.println(sql);
    }

}
