package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dao.Database;
import dao.UserDao;
import model.User;

public class UserDaoImpl implements UserDao {
	private final String TABLE_NAME = "users";

	public UserDaoImpl() {
	}

	@Override
	public void setup() throws SQLException {
		try (Connection connection = Database.getConnection();
			 Statement stmt = connection.createStatement();) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (username VARCHAR(10) NOT NULL," +
					"prefered_name VARCHAR(10) NOT NULL,"
					+ "password VARCHAR(64) NOT NULL," + "PRIMARY KEY (username))";
			stmt.executeUpdate(sql);
		}
	}

	@Override
	public User getUser(String username) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
		try (Connection connection = Database.getConnection(); 
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					User user = new User();
					user.setUsername(rs.getString("username"));
					user.setPreferedName(rs.getString("prefered_name"));
					user.setPassword(rs.getString("password"));
					return user;
				}
				return null;
			} 
		}
	}

	@Override
	public User createUser(String username, String preferredName, String password) throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			stmt.setString(2, preferredName);
			stmt.setString(3, password);

			stmt.executeUpdate();
			return new User(username,preferredName, password);
		} 
	}

	@Override
	public void updateUser(User user) throws SQLException {
		if (!isUsernameExist(user.getUsername())) {
			String sql = "UPDATE " + TABLE_NAME + " SET prefered_name = ?, password = ? WHERE username = ?";
			try (Connection connection = Database.getConnection();
				 PreparedStatement stmt = connection.prepareStatement(sql);) {
				stmt.setString(1, user.getPreferedName());
				stmt.setString(2, user.getPassword());
				stmt.setString(3, user.getUsername());
				stmt.executeUpdate();
			}

		}

	}

	public boolean isUsernameExist(String username) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}
}
