package dao;

import model.CartItem;

import java.util.List;

public interface OrderItemDao {
    void setUp();
    List<CartItem> getCartItemsByOrderId(String orderId);
    void saveCartItem(CartItem cartItem);
}
