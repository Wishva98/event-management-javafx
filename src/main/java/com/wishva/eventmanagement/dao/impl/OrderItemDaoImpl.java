package dao.impl;

import dao.Database;
import dao.OrderItemDao;
import model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {
    @Override
    public void setUp() {
        try(Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();){
            String sql = "CREATE TABLE IF NOT EXISTS order_item (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_id VARCHAR(4) NOT NULL," +
                    "event VARCHAR(20) NOT NULL," +
                    "venue VARCHAR(20) NOT NULL," +
                    "day VARCHAR(10) NOT NULL," +
                    "qty INT(3) NOT NULL," +
                    "unit_price DECIMAL(8,2) NOT NULL," +
                    "CONSTRAINT fK_order FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE" +
                    ")";
            statement.executeUpdate(sql);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<CartItem> getCartItemsByOrderId(String orderId) {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM `order_item` WHERE order_id=?";

        try(Connection connection = Database.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);){
            statement.setString(1, orderId);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                cartItems.add(new CartItem(
                    rs.getString("event"),
                    rs.getString("day"),
                    rs.getString("venue"),
                    rs.getInt("qty"),
                    rs.getBigDecimal("unit_price")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  cartItems;
    }

    @Override
    public void saveCartItem(CartItem cartItem) {

    }
}
