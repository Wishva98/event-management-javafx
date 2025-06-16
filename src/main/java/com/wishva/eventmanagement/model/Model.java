package com.wishva.eventmanagement.model;

import com.wishva.eventmanagement.dao.UserDao;

import java.sql.SQLException;

public class Model {
    private UserDao userDao;

    public Model() {
        this.userDao = userDao;
    }

    public void setup() throws SQLException {
        userDao.setup();

    }
}
