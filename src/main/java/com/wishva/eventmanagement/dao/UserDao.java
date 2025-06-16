package com.wishva.eventmanagement.dao;

import com.wishva.eventmanagement.model.User;

import java.sql.SQLException;

public interface UserDao {
    void setup() throws SQLException;
    User getUser(String username) throws SQLException;
    User createUser(String username, String prefferedName, String password) throws SQLException;
    void updateUser(User user) throws SQLException;
    boolean isUsernameExist(String username) throws SQLException;
}
