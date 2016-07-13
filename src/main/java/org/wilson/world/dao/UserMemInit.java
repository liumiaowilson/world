package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.model.User;

public class UserMemInit implements MemInit<User> {

    @Override
    public void init(DAO<User> dao) {
        User user = new User();
        user.username = "test";
        user.password = "test";
        dao.create(user);
    }

    @Override
    public List<QueryTemplate<User>> getQueryTemplates() {
        return new ArrayList<QueryTemplate<User>>();
    }
}
