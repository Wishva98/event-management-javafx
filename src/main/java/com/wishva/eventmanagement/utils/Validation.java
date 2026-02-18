package utils;

import javafx.scene.control.Alert;
import model.CartItem;
import model.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Validation {
    private Model model;

    public Validation(Model model) {
        this.model = model;
    }

    public boolean isValidNumberOfSeats(CartItem cartItem) {
        int availableTickets = model.getEventsDao().getAvailableTickets(cartItem.getItemName(), cartItem.getVenue(), cartItem.getItemDay());
        if (cartItem.getNumberOfSeats() < 1) {
            return false;
        } else return cartItem.getNumberOfSeats() <= availableTickets;
    }

    public boolean isValidCode(String code) {
        return code.matches("\\d{6}");

    }

    public boolean isValidDayOfBooking(List<CartItem> cartItems) {
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int indexToday = LocalDate.now().getDayOfWeek().getValue()- 1;
        for (CartItem cartItem : cartItems) {
            int eventIndex = Arrays.asList(weekdays).indexOf(cartItem.getItemDay());
            if (eventIndex < indexToday) {
                new Alert(Alert.AlertType.ERROR,"Booking for " + cartItem.getItemDay() + "day already passed. Try again").showAndWait();
                return false;
            }
        }
        return true;
    }

    public boolean showInvalidEvents(List<CartItem> cartItems) {
        ArrayList<CartItem> invalidCartItems = new ArrayList<>();
        StringBuilder eventList = new StringBuilder();
        for (CartItem cartItem : cartItems) {
            if (!isValidNumberOfSeats(cartItem)) {
                eventList.append(cartItem.getItemName()).append("\n");
            }
        }
        if(eventList.isEmpty()) {
            return false;
        }
        new Alert(Alert.AlertType.INFORMATION, eventList.toString()).showAndWait();
        return true;
    }
}
