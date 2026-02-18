package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Create a single connection sing singleton design pattern
public class Database {
	private static Database instance;
	private Connection connection;
	// URL pattern for database
	private static final String DB_URL = "jdbc:sqlite:application.db";

	private Database() {
		try {
			connection = Database.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Database getInstance(){
		if(instance == null){
			instance = new Database();
		}
		return instance;
	}

	public static Connection getConnection() throws SQLException {
		// DriverManager is the basic service for managing a set of JDBC drivers
		// Can also pass username and password
		return DriverManager.getConnection(DB_URL);
	}
}
