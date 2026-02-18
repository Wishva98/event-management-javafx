package dao;

import model.Order;
import model.User;

import java.util.List;

public interface OrderDao {
    void setUp();
    List<Order> getAllOrders();

    List<Order> getOrdersByUserName(String userName);

    void saveOrder(Order order);
    int numberOfOrdersByUser(User user);

}
