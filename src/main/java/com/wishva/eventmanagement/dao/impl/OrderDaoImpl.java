package dao.impl;

import dao.Database;
import dao.OrderDao;
import model.CartItem;
import model.Model;
import model.Order;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    private final Model model;

    public OrderDaoImpl(Model model) {
        this.model = model;
    }

    @Override
    public void setUp() {
        try(Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();){
            String sql = "CREATE TABLE IF NOT EXISTS `order` (" +
                    "id VARCHAR(4) NOT NULL PRIMARY KEY," +
                    "user_name VARCHAR(50) NOT NULL," +
                    "orderDateTime VARCHAR(20) NOT NULL," +
                    "totalPrice DECIMAL NOT NULL," +
                    "CONSTRAINT fk_order FOREIGN KEY (user_name) REFERENCES users (username) ON DELETE CASCADE" +
                    ")";
            statement.executeUpdate(sql);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `order` ORDER BY orderDateTime DESC";
        try (Connection connection = Database.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String dateTime = rs.getString("orderDateTime");
                LocalDateTime orderDateTime = LocalDateTime.parse(dateTime);
                String userName = rs.getString("user_name");
                Order order = new Order(
                        userName,
                        orderDateTime,
                        getCartItems(userName),
                        rs.getBigDecimal("totalPrice"));
                order.setId(rs.getString("id"));
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByUserName(String userName) {
        List<Order> userOrders = new ArrayList<>();
        String sql = "SELECT * FROM `order` WHERE user_name = ?";
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dateTime = rs.getString("orderDateTime");
                String orderId = rs.getString("id");
                LocalDateTime orderDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Order order = new Order(
                        userName,
                        orderDateTime,
                        getCartItems(orderId),
                        rs.getBigDecimal("totalPrice"));
                order.setId(orderId);
                userOrders.add(order);
            }
            return userOrders;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveOrder(Order order) {
        String saveOrderSql = "INSERT INTO `order` (id, user_name, orderDateTime, totalPrice) VALUES (?, ?, ?, ?)";
        String saveOrderItemsSql = "INSERT INTO order_item (" +
                "order_id, event, venue, day, qty, unit_price) VALUES (?, ?, ?, ?, ?,?)";
        Connection connection = null;
        try {
            connection= Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            if (order.getId() == null || order.getId().isEmpty()) {
                order.setId(generateOrderId());
            }
            String format = order.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //save order
            try (PreparedStatement statement = connection.prepareStatement(saveOrderSql)) {
                statement.setString(1, order.getId());
                statement.setString(2, order.getUserName());
                statement.setString(3, format);
                statement.setBigDecimal(4, order.getTotalPrice());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //save cart items in orderItem
            for (CartItem item : order.getOrderItems()) {
                try(PreparedStatement statement = connection.prepareStatement(saveOrderItemsSql)) {
                    statement.setString(1, order.getId());
                    statement.setString(2, item.getItemName());
                    statement.setString(3, item.getVenue());
                    statement.setString(4, item.getItemDay());
                    statement.setInt(5, item.getNumberOfSeats());
                    statement.setBigDecimal(6, item.getUnitPrice());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            connection.commit();


        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("Transaction rolled" + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        }finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public int numberOfOrdersByUser(User user) {
        try(Connection connection = Database.getInstance().getConnection();) {
            String sql = "SELECT COUNT(*) FROM `order` WHERE user_name = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private List<CartItem> getCartItems(String orderId) {
        return model.getOrderItemDao().getCartItemsByOrderId(orderId);
    }

    private String generateOrderId() {
        String sql = "SELECT MAX(CAST(id AS INTEGER)) FROM `order`";
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int nextId = 1;
            if (rs.next()) {
                int maxId = rs.getInt(1);
                nextId = maxId + 1;
            }

            return String.format("%04d", nextId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate ID", e);
        }
    }
}
