package model;

import java.math.BigDecimal;

public class CartItem {
    private String itemName;
    private String itemDay;
    private String venue;
    private int numberOfSeats;
    private BigDecimal unitPrice;

    public CartItem(String itemName, String itemDay, String venue, int numberOfSeats, BigDecimal unitPrice) {
        this.itemName = itemName;
        this.itemDay = itemDay;
        this.venue = venue;
        this.numberOfSeats = numberOfSeats;
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-4s %-3s", itemName, "(" + itemDay + ")", "X " + numberOfSeats);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDay() {
        return itemDay;
    }

    public void setItemDay(String itemDay) {
        this.itemDay = itemDay;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(numberOfSeats));
    }
}
