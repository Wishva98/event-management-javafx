package dao.impl;

import dao.Database;
import dao.EventsDao;
import model.CartItem;
import model.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsDaoImpl implements EventsDao {
    @Override
    public void setup() {
        //Create table
        try(Connection connection = Database.getInstance().getConnection();
            Statement stmt = connection.createStatement()) {
            String sql_event = "CREATE TABLE IF NOT EXISTS events (Id INTEGER PRIMARY KEY  AUTOINCREMENT,event VARCHAR(20) NOT NULL,"
                    + "venue VARCHAR(20) NOT NULL," + "day VARCHAR(10) NOT NULL,"
                    + "price DECIMAL(6,2) NOT NULL," + "sold_tickets INT NOT NULL,"
                    + "total_tickets INT NOT NULL," +
                    "isEnabled BOOLEAN NOT NULL)";
            stmt.executeUpdate(sql_event);
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        //insert data into database
        if (!isDataInserted()) {
            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/events.dat"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] eventData = line.split(";");
                    Event event = new Event(eventData[0], eventData[1], eventData[2],
                            BigDecimal.valueOf(Long.parseLong(eventData[3])), Integer.parseInt(eventData[4]),
                            Integer.parseInt(eventData[5]));
                    saveEventData(event);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Event addEvent(String name, String venue, String day, BigDecimal price, int soldTickets, int totalTickets) {
        Event event = new Event(name, venue, day, price, soldTickets, totalTickets);
        saveEventData(event);
        return event;
    }

    @Override
    public Event getEvent(String name, String venue, String day) {
        String sql_getEvent = "SELECT * FROM events WHERE event = ? AND venue = ? AND day = ?";
        try(Connection connection = Database.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql_getEvent);) {
            stmt.setString(1, name);
            stmt.setString(2, venue);
            stmt.setString(3, day);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()){
                    return new Event(rs.getString("event"), rs.getString("venue"),
                            rs.getString("day"), rs.getBigDecimal("price"),
                            rs.getInt("sold_tickets"), rs.getInt("total_tickets"));
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return null;
    }

    public void saveEventData(Event event){
        String sql_event = "INSERT INTO events(event, venue, day, price, sold_tickets, total_tickets, isEnabled) VALUES (?, ?, ?, ?, ?, ?,?)";
        try(Connection connection = Database.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql_event);) {
            stmt.setString(1, event.getEvent());
            stmt.setString(2, event.getVenue());
            stmt.setString(3, event.getDay());
            stmt.setBigDecimal(4, event.getPrice());
            stmt.setInt(5, event.getSoldTickets());
            stmt.setInt(6, event.getTotalTickets());
            stmt.setBoolean(7, event.isEnabled());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
    }

    public void updateEvent(Event event){
        try(Connection connection = Database.getInstance().getConnection();) {
            String sql= "UPDATE events SET event = ?, venue = ?, day =?,price = ? , total_tickets =?" +
                    "isEnabled = ?" +
                    " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, event.getEvent());
            statement.setString(2, event.getVenue());
            statement.setString(3, event.getDay());
            statement.setBigDecimal(4, event.getPrice());
            statement.setInt(5, event.getTotalTickets());
            statement.setBoolean(6, event.isEnabled());
            statement.setInt(7, event.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Event> getAllEvents(){
        ArrayList<Event> events = new ArrayList<>();

        try(Connection connection = Database.getInstance().getConnection();
            Statement stmt = connection.createStatement()){
            String sql_event = "SELECT * FROM events";

            try(ResultSet rs = stmt.executeQuery(sql_event);){
                while(rs.next()){
                    Event event = new Event(rs.getInt("id"),
                            rs.getString("event"),
                            rs.getString("venue"),
                            rs.getString("day"),
                            rs.getBigDecimal("price"),
                            rs.getInt("sold_tickets"),
                            rs.getInt("total_tickets"),
                            rs.getBoolean("isEnabled"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    @Override
    public int getAvailableTickets(String name, String venue, String day) {
        try(Connection connection =  Database.getInstance().getConnection();) {
            String sql_event = "SELECT sold_tickets, total_tickets FROM events WHERE event = ? AND venue = ? AND day = ?";
            PreparedStatement stmt = connection.prepareStatement(sql_event);
            stmt.setString(1, name);
            stmt.setString(2, venue);
            stmt.setString(3, day);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("total_tickets") - rs.getInt("sold_tickets");
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSoldTickets(CartItem cartItem, String addOrRemove) {

        try (Connection connection =  Database.getInstance().getConnection();){
            Event event = getEvent(cartItem.getItemName(), cartItem.getVenue(), cartItem.getItemDay());
            int soldTickets = event.getSoldTickets() + cartItem.getNumberOfSeats();

            String sql_event = "UPDATE events SET sold_tickets = ? WHERE event = ? AND day = ?";
            PreparedStatement stmt = connection.prepareStatement(sql_event);
            stmt.setInt(1, soldTickets);
            stmt.setString(2, event.getEvent());
            stmt.setString(3, event.getDay());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private boolean isDataInserted(){
        try (Connection connection = Database.getInstance().getConnection();){
            String sql_event = "SELECT * FROM events";
            PreparedStatement stmt = connection.prepareStatement(sql_event);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}

