package model;

import java.math.BigDecimal;

public class Event {
    private int id;
    private String event;
    private String venue;
    private String day;
    private BigDecimal price;
    private int soldTickets;
    private int totalTickets;
    private boolean isEnabled;

    public Event(String event, String venue, String day, BigDecimal price, int soldTickets, int totalTickets) {
        this.event = event;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.soldTickets = soldTickets;
        this.totalTickets = totalTickets;
        this.isEnabled = true;
    }

    public Event(String event, String venue, String day, BigDecimal price, int soldTickets, int totalTickets, boolean isEnabled) {
        this.event = event;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.soldTickets = soldTickets;
        this.totalTickets = totalTickets;
        this.isEnabled = isEnabled;
    }

    public Event(int id, String event, String venue, String day, BigDecimal price, int soldTickets, int totalTickets, boolean isEnabled) {
        this.id = id;
        this.event = event;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.soldTickets = soldTickets;
        this.totalTickets = totalTickets;
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return event + " " + venue + " " + day + " " + price + " " + soldTickets + " " + totalTickets + " " + "\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
