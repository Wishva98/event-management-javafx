package dao.impl;

import dao.CartDao;
import dao.Database;
import dao.EventsDao;
import model.CartItem;
import model.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDaoImpl implements CartDao {


    @Override
    public void setUp() {
        try(Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();){
            String createCartTable = "CREATE TABLE IF NOT EXISTS cart (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_name VARCHAR(20) NOT NULL," +
                    "event_name VARCHAR(30) NOT NULL," +
                    "event_day VARCHAR(10) NOT NULL," +
                    "venue VARCHAR(10) NOT NULL," +
                    "qty INTEGER NOT NULL," +
                    "unit_price DECIMAL(8,2) NOT NULL," +
                    "CONSTRAINT fk_user FOREIGN KEY (user_name) REFERENCES users (username) ON DELETE CASCADE)";
            statement.executeUpdate(createCartTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveCartItem(CartItem cartItem, String userName) {

        try(Connection connection = Database.getInstance().getConnection();){
            //save cart
            String save_sql = "INSERT INTO cart (user_name, event_name, event_day,venue, qty, unit_price) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(save_sql);
            statement.setString(1,userName);
            statement.setString(2, cartItem.getItemName());
            statement.setString(3, cartItem.getItemDay());
            statement.setString(4, cartItem.getVenue());
            statement.setInt(5,cartItem.getNumberOfSeats());
            statement.setBigDecimal(6,cartItem.getUnitPrice());
            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<CartItem> getCartItemsByUser(String userName) {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        try(Connection connection = Database.getInstance().getConnection();){
            String select_sql = "SELECT * FROM cart WHERE user_name=?";
            PreparedStatement statement = connection.prepareStatement(select_sql);
            statement.setString(1,userName);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                cartItems.add(new CartItem(
                        resultSet.getString("event_name"),
                        resultSet.getString("event_day"),
                        resultSet.getString("venue"),
                        resultSet.getInt("qty"),
                        resultSet.getBigDecimal("unit_price")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cartItems;
    }

    @Override
    public void emptyCartItemByUser(String userName) {
        try (Connection connection = Database.getInstance().getConnection();){
            String delete_sql = "DELETE FROM cart WHERE user_name=?";
            PreparedStatement statement = connection.prepareStatement(delete_sql);
            statement.setString(1,userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCartItem(CartItem cartItem, String username) {
        try(Connection connection = Database.getInstance().getConnection();){
            String update_sql = "UPDATE cart SET " +
                    "qty=?" +
                    "WHERE user_name=?";
            PreparedStatement statement = connection.prepareStatement(update_sql);
            statement.setInt(1,cartItem.getNumberOfSeats());
            statement.setString(2,username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete cart Item when user removes from cart
    @Override
    public void deleteCartItem(CartItem cartItem, String username) {
        try(Connection connection = Database.getInstance().getConnection();){
            String delete_sql = "DELETE FROM cart WHERE user_name=? AND event_name =? AND event_day=?";
            PreparedStatement statement = connection.prepareStatement(delete_sql);
            statement.setString(1,username);
            statement.setString(2,cartItem.getItemName());
            statement.setString(3,cartItem.getItemDay());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
