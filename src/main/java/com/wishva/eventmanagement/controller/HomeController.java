package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.*;
import utils.Validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {
    private Stage stage;
    private Model model;
    private Stage parentStage;

    //Label
    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblTotal;

    //Buttons
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnAddToCart;
    @FXML
    private Button btnEditItem;
    @FXML
    private Button btnCheckout;
    @FXML
    private Button btnRemoveFromCart;
    @FXML
    private TableView<Event> tblEvents;
    @FXML
    private ListView<CartItem> lvlCart;
    @FXML
    private Spinner<Integer> spnQty;

    @FXML
    MenuItem btnEditProfile;
    @FXML
    MenuItem btnPastOrders;
    private Event selectedEvent;
    private CartItem selectedCartItem;
    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private BigDecimal totalPrice = new BigDecimal(0);
    private final User user;
    Validation validation;


    public HomeController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
        user = model.getCurrentUser();
    }

    @FXML
    public void initialize() {
        lblTotal.setText("");
        validation = new Validation(model);
        loadCart();
        resetPage();
        updateTotal();
        spnQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        btnEditProfile.setOnAction(event -> {
            handleEditProfile();
        });

        btnLogout.setOnMouseClicked(event -> {
            stage.close();
            parentStage.show();
        });
        lblWelcome.setText("Welcome " + model.getCurrentUser().getPreferedName());
        //setup table
        tblEvents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("event"));
        tblEvents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("venue"));
        tblEvents.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("day"));
        tblEvents.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
        //load event data to the table
        loadEvents();

        tblEvents.getSelectionModel().selectedItemProperty().addListener((observable, prev, current) -> {
            if(current != null) {
                selectedEvent = current;
                spnQty.setDisable(false);
                spnQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000));
                btnAddToCart.setDisable(false);
            }
        });
        lvlCart.setItems(cartItems);
        lvlCart.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                btnEditItem.setDisable(false);
                selectedCartItem = newValue;
            }
        });
        //Add to cart
        btnAddToCart.setOnMouseClicked(e ->{
            handleAddToCart();
        });

        //Edit item
        btnEditItem.setOnMouseClicked(event -> {
            int availableTickets = model.getEventsDao().getAvailableTickets(selectedCartItem.getItemName(), selectedCartItem.getVenue(), selectedCartItem.getItemDay());
            btnAddToCart.setText("save");
            btnAddToCart.setDisable(false);
            btnRemoveFromCart.setVisible(true);
            spnQty.requestFocus();
            spnQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                    (1, availableTickets,selectedCartItem.getNumberOfSeats()));
            spnQty.setDisable(false);
        });

        //Remove from cart
        btnRemoveFromCart.setOnMouseClicked(event -> {
            model.getCartDao().deleteCartItem(selectedCartItem, user.getUsername());
            cartItems.remove(selectedCartItem);
            updateTotal();
            resetPage();
        });

        //Checkout
        btnCheckout.setOnAction(event -> {
            handleCheckout();
        });
        int numberOfPastOrders = model.getOrderDao().numberOfOrdersByUser(user);
        btnPastOrders.setDisable(numberOfPastOrders < 1);
        btnPastOrders.setOnAction(event -> {
            handlePastOrder();
        });

    }

    //checkout
    private void handleCheckout() {
        //recheck availability when checkout in case of other users already booked
        if (!validation.showInvalidEvents(cartItems)) {
            if (!validation.isValidDayOfBooking(cartItems)){
                return;
            }
            String strCheck = String.format("Total Price: %.2f\nDo you want to checkout?", totalPrice);
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, strCheck, ButtonType.YES, ButtonType.NO);
            confirmationAlert.setTitle("Confirm Checkout");
            confirmationAlert.showAndWait();
            if (confirmationAlert.getResult() == ButtonType.YES) {
                //Get confirmation code
                TextInputDialog confirmationCode = new TextInputDialog();
                confirmationCode.setTitle("Confirm code");
                confirmationCode.setHeaderText("Enter 6-digit confirmation code");
                confirmationCode.setContentText("Code:");
                confirmationCode.showAndWait().ifPresent(code -> {
                    if (!validation.isValidCode(code)) {
                        new Alert(Alert.AlertType.ERROR, "Invalid code. Retry again").showAndWait();
                        return;
                    }
                    placeOrder();
                    new Alert(Alert.AlertType.INFORMATION, "Checkout successful").showAndWait();
                    cartItems.clear();

                    resetPage();
                });
            }
        }

    }

    private void handleAddToCart() {
        if (btnAddToCart.getText().equalsIgnoreCase("Add To Cart")) {
            if(selectedEvent != null) {
                int qty = spnQty.getValue();
                CartItem cartItem = new CartItem(selectedEvent.getEvent(), selectedEvent.getDay(), selectedEvent.getVenue(), qty, selectedEvent.getPrice());
                if(validation.isValidNumberOfSeats(cartItem)) {
                    updateCartItems(cartItem,qty);
                    updateTotal();
                    resetPage();
                }else {
                    new Alert(Alert.AlertType.ERROR, "Ticket count is higher than available tickets").show();
                    resetPage();
                }
            }
        } else if (btnAddToCart.getText().equalsIgnoreCase("Save")) {
            updateCartItems(selectedCartItem, spnQty.getValue());
            lvlCart.getSelectionModel().clearSelection();
            resetPage();
        }
    }



    //show past orders
    private void handlePastOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PastOrderView.fxml"));
            PastOrderController pastOrderController = new PastOrderController(model, stage);
            loader.setController(pastOrderController);

            AnchorPane root = loader.load();
            pastOrderController.showStage(root);
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //edit profile
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileView.fxml"));
            ProfileController profileController = new ProfileController(model, stage);
            loader.setController(profileController);

            AnchorPane root = loader.load();
            profileController.showStage(root);
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Home");
        stage.show();
    }

    private void loadEvents() {
        List<Event> filteredEvents = model.getEventsDao().getAllEvents()
                .stream().filter(Event::isEnabled).toList();
        events.addAll(filteredEvents);
        tblEvents.setItems(events);
    }

    private void placeOrder(){
        List<CartItem> cartItemList = model.getCartDao().getCartItemsByUser(user.getUsername());
        //change sold tickets in a database
        cartItemList.forEach(cartItem -> {
            model.getEventsDao().updateSoldTickets(cartItem, "add");
        });
        updateTotal();
        //save order in a database
        model.getOrderDao().saveOrder(new Order(
                user.getUsername(),
                LocalDateTime.now(),
                cartItemList, totalPrice
        ));
        model.getCartDao().emptyCartItemByUser(user.getUsername());
    }

    private void loadCart() {
        List<CartItem> cartItemList = model.getCartDao().getCartItemsByUser(user.getUsername());
        if (!cartItemList.isEmpty()) {
            cartItems.setAll(cartItemList);
        }
    }

    private void resetPage(){
        spnQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000));
        spnQty.setDisable(true);
        btnAddToCart.setDisable(true);
        tblEvents.getSelectionModel().clearSelection();
        lvlCart.getSelectionModel().clearSelection();
        btnEditItem.setDisable(true);
        btnRemoveFromCart.setVisible(false);
        btnAddToCart.setText("Add To Cart");
        btnCheckout.setDisable(cartItems.isEmpty());
        selectedEvent = null;
        selectedCartItem = null;
    }

    private void updateCartItems(CartItem cartItem, int qty) {
        //when update selected cart item
        if (selectedCartItem != null) {
            cartItem.setNumberOfSeats(qty);
            if(validation.isValidNumberOfSeats(cartItem)) {
                int i = cartItems.indexOf(selectedCartItem);
                model.getCartDao().updateCartItem(cartItem, user.getUsername());
                cartItems.set(i, cartItem);
                updateTotal();
                selectedCartItem = null;
            }else {
                new Alert(Alert.AlertType.ERROR, "You have ordered tickets more than available").show();
            }

        }else {
            //if adding an event to the cart, and already, there are tickets from the same event
            CartItem eventExistInCart = eventExistInCart(cartItem);
            if ( eventExistInCart!= null) {
                eventExistInCart.setNumberOfSeats(eventExistInCart.getNumberOfSeats() + qty);
                if(validation.isValidNumberOfSeats(eventExistInCart)) {
                    int i = cartItems.indexOf(eventExistInCart);
                    model.getCartDao().updateCartItem(cartItem, user.getUsername());
                    cartItems.set(i, eventExistInCart);
                }else {
                    new Alert(Alert.AlertType.ERROR, "You have ordered tickets more than available").show();
                }
                //if item adding to the cart is new
            }else {
                model.getCartDao().saveCartItem(cartItem, user.getUsername());
                cartItems.add(cartItem);
            }
        }

    }

    private void updateTotal(){
        if(cartItems.isEmpty()) {
            totalPrice = new BigDecimal(0);
            lblTotal.setText("");
            return;
        }
        totalPrice = new BigDecimal(0);
        //recalculate the total price inside the cart
        cartItems.forEach(cartItem -> {
            totalPrice = totalPrice.add(cartItem.getTotalPrice());
            lblTotal.setText("Total Price: " + totalPrice);
        });
    }

    //check item whether it exists in the cart
    private CartItem eventExistInCart(CartItem cartItem) {
        for (CartItem item : cartItems) {
            if(item.getItemName().equalsIgnoreCase(cartItem.getItemName()) && item.getItemDay().equalsIgnoreCase(cartItem.getItemDay())) {
                return item;
            }
        }
        return null;
    }


}
