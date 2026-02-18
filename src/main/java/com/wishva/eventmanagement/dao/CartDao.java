package dao;

import model.CartItem;

import java.util.List;

public interface CartDao {
    void setUp();
    void saveCartItem(CartItem cartItem, String username);
    List<CartItem> getCartItemsByUser(String userName);
    void emptyCartItemByUser(String userName);
    void updateCartItem(CartItem cartItem, String username);
    void deleteCartItem(CartItem cartItem, String username);
}
