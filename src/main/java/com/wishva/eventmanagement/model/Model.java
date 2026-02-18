package model;

import java.sql.SQLException;

import dao.*;
import dao.impl.*;

public class Model {
	private UserDao userDao;
	private EventsDao eventsDao;
	private User currentUser;
	private OrderDao orderDao;
	private OrderItemDao orderItemDao;
	private CartDao cartDao;
	
	public Model() {
		userDao = new UserDaoImpl();
		eventsDao = new EventsDaoImpl();
		orderDao = new OrderDaoImpl(this);
		orderItemDao = new OrderItemDaoImpl();
		cartDao = new CartDaoImpl();
	}
	
	public void setup() throws SQLException {
		userDao.setup();
		eventsDao.setup();
		orderDao.setUp();
		orderItemDao.setUp();
		cartDao.setUp();
	}
	public UserDao getUserDao() {
		return userDao;
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public EventsDao getEventsDao() {
		return eventsDao;
	}

	public OrderDao getOrderDao() {
		return orderDao;
	}

	public OrderItemDao getOrderItemDao() {
		return orderItemDao;
	}
	public CartDao getCartDao() {
		return cartDao;
	}
}
