package dao;

import model.CartItem;
import model.Event;

import java.math.BigDecimal;
import java.util.List;

public interface EventsDao {
    void setup();
    Event addEvent(String name, String venue, String day, BigDecimal price, int soldTickets, int totalTickets);
    Event getEvent(String name, String venue, String day);
    List<Event> getAllEvents();
    int getAvailableTickets(String name, String venue, String day);
    void updateSoldTickets(CartItem cartItem, String addOrRemove);


}
