package com.wishva.eventmanagement.dao.impl;

import com.wishva.eventmanagement.dao.UserDao;
import com.wishva.eventmanagement.model.User;

import java.sql.SQLException;

public class UserDaoImpl implements UserDao {
    @Override
    public void setup() throws SQLException {

    }

    @Override
    public User getUser(String username) throws SQLException {
        return null;
    }

    @Override
    public User createUser(String username, String prefferedName, String password) throws SQLException {
        return null;
    }

    @Override
    public void updateUser(User user) throws SQLException {

    }

    @Override
    public boolean isUsernameExist(String username) throws SQLException {
        return false;
    }
}
