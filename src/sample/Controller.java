package sample;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Controller {

    private StringBuilder sb;

    private static List<Item> items = new ArrayList<>();
    private List <Item> orderedItems = new ArrayList<>();
    private List<String> itemsOrderedDetails = new ArrayList<>();

    private String id;
    private int itemCounter = 1;
    private int numItemsInOrder = 0;
    private int quantity = 0;
    private float subtotal = 0;

    public Label numberItemsLabel = null;
    public Label itemIdLabel = null;
    public Label quantityLabel = null;
    public Label itemInfoLabel = null;
    public Label subtotalLabel = null;

    public TextField numberItemsTextField = null;
    public TextField itemIdTextField = null;
    public TextField quantityTextField = null;
    public TextField itemInfoTextField = null;
    public TextField subtotalTextField = null;

    public Button processItemBtn = null;
    public Button confirmItemBtn = null;
    public Button viewOrderBtn = null;
    public Button finishOrderBtn = null;
    public Button newOrderBtn = null;
    public Button exitBtn = null;

    // Load the file that's storing all the inventory details
    // Store all the inventory in a List
    public static void loadFile() throws FileNotFoundException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader("src\\sample\\inventory.txt"));
        try{
            String line = bufferedReader.readLine();
            while(line != null){
                // Take inventory item from file and parse as an Item object, then add to list of items
                Item item = parseItem(line);
                items.add(item);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            }
            catch (IOException ioe)
            {
                System.out.println("Error closing BufferedReader");
            }
        }
    }

    // Function to parse Item from inventory file
    // Structure of the data :
    // e.g. 14, "Stanley #2 Philips Screwdriver", true, 6.95
    private static Item parseItem(String itemAsString) {

        // Get rid of the ", " that's separating the data
        String[] data = itemAsString.split(", ");

        // Item id
        String id = data[0];

        // Item name
        // Get rid of quotation marks
        String name = data[1].substring(1, data[1].length() - 1);

        // Is Item available
        boolean isAvailable = Boolean.parseBoolean(data[2]);

        // Item price
        float price = Float.parseFloat(data[3]);

        // Return Item object
        return new Item(id, name, isAvailable, price);
    }

    // Function to get item details in form of a String
    private String getItemDetails(Item item, int quantity) {

        // Get discount amount and total cost
        int discount = getDiscount(quantity);
        float cost = item.getPrice() * quantity;

        // Update cost if discount is applied
        if (discount > 0) {
            cost = cost - (cost / discount);
        }

        // Update order subtotal
        subtotal = subtotal + cost;

        // Get individual item price and total cost of items as Strings
        // in order to create item ordered details as a String
        String itemPriceStr = String.format("%.02f", item.getPrice());
        String costStr = String.format("%.02f", cost);

        // Item ordered details as a String
        String itemDetails =   item.getId() +
                            " \""  + item.getName() + "\" $"
                            + itemPriceStr + " "
                            + quantity + " " +
                            + discount + "%"
                            + " $" + costStr;

        // Add to list of items ordered details
        itemsOrderedDetails.add(itemCounter + ". " + itemDetails);

        // Return Item details as a string
        return itemDetails;
    }

    // Function used to search for an Item by its id
    private Item getItemById(String id) {

        // Loops through list of items
        for(Item item : items) {
            if (item.getId().contentEquals(id)) {
                return item;
            }
        }
        return null;
    }

    // Function to calculate if a discount needs to be applied depending on
    // the quantity of item ordered
    private int getDiscount(int quantity) {

        // No discount
        if (quantity < 5)
            return 0;
        // 10% discount
        else if (quantity < 10)
            return 10;
        // 15% discount
        else if (quantity < 15)
            return 15;
        // 20% discount
        else
            return 20;
    }

    // Function to create a transaction file to store completed orders details
    public void createTransactionFile() {

        // Objects used to write to file
        try (FileWriter fileWriter = new FileWriter("transactions.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)){
            for (Item item : orderedItems){
                // Write each items order details to transactions file
                printWriter.println(item.getDetails());
            }
        } catch (IOException e) {
            System.out.println("Error writing file.");
            e.printStackTrace();
        }
    }

    @FXML
    public void processItemClicked(Event e) {

        // Store information customer inputs
        numItemsInOrder = Integer.parseInt(numberItemsTextField.getText());
        id = itemIdTextField.getText();
        quantity = Integer.parseInt(quantityTextField.getText());

        // Find the item the customer wants to order
        Item item = getItemById(id);

        // If item does not exist, alert customer
        if (item == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Item ID " + id + " not in file");
            alert.showAndWait();
        }
        // If item is out of stock, alert customer
        else if (item.getInStock() == false) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Sorry... that item is out of stock, please try another item");
            alert.showAndWait();
        }
        // Item is in stock and gets added to order
        else {
            // Populate itemInfoTextField with the item ordered details
            itemInfoTextField.setText(getItemDetails(item, quantity));

            // Disable processItemBtn and enable confirmItemBtn
            processItemBtn.setDisable(true);
            confirmItemBtn.setDisable(false);

            // Used to append details to itemOrderedDetails String
            sb = new StringBuilder();

            //                (Day)(Month)(Year)(Hr)(Min)
            // Military time format: 23 01 2021 18 47 (with no spaces)
            SimpleDateFormat dateFormatMilitary = new SimpleDateFormat("ddMMyyyyHHmm");

            //                (Month)(Day)(Year)(Hr)(Min)(Sec)(AM or Pm)(Timezone)
            // Regular time format: 01/23/21, 6:47:26 PM EST
            SimpleDateFormat dateFormatTimeZone = new SimpleDateFormat("MM/dd/yy, h:mm:ss a z");

            // Timestamp for item ordered
            String dateMilitary = dateFormatMilitary.format(new Date());
            String dateTimeZone = dateFormatTimeZone.format(new Date());

            // Item ordered details with data about when item was ordered
            String itemOrderedDetails;
            itemOrderedDetails = sb.append(dateMilitary + ", " + item.getId() + ", " + "\"" + item.getName() + "\""
                    + ", " + item.getPrice() + ", " + quantity + ", " + (float)getDiscount(quantity) + ", "
                    + "$" + String.format("%.02f", (quantity * item.getPrice())) + ", " + dateTimeZone).toString();

            // Update Item Object with details about its order
            item.setDetails(itemOrderedDetails);

            // Add to list of ordered items
            orderedItems.add(item);
        }
    }

    @FXML
    public void confirmItemClicked(Event e) {

        // Update subtotalTextField
        subtotalTextField.setText(String.format("$%.02f", subtotal));

        // Clear itemId and quatity TextField
        itemIdTextField.clear();
        quantityTextField.clear();

        // Increment number of items ordered
        itemCounter++;

        // Customer finished with order
        if (itemCounter > numItemsInOrder){
            // Remove the itemLabel and quantityLabel text
            itemIdLabel.setText("");
            quantityLabel.setText("");

            // Disable processItemBtn
            processItemBtn.setDisable(true);

            // Remove # from beside the process and confirm item buttons
            processItemBtn.setText("Process Item");
            confirmItemBtn.setText("Confirm Item");
        }
        // Order is still in progress, update labels and buttons with correct item numbers
        else {
            processItemBtn.setText("Process Item #" + itemCounter);
            confirmItemBtn.setText("Confirm Item #" + itemCounter);
            itemIdLabel.setText("Enter item ID for Item #" + itemCounter + ":");
            quantityLabel.setText("Enter quantity for Item #" + itemCounter + ":");

            // Enable processItemBtn
            processItemBtn.setDisable(false);
        }

        // Update labels and buttons with correct item numbers
        itemInfoLabel.setText("Item #" + (itemCounter - 1) + " info:");
        subtotalLabel.setText("Order subtotal for " + (itemCounter - 1) + " item(s):");

        // Enable view order and finish order buttons, disable confirm item button
        viewOrderBtn.setDisable(false);
        finishOrderBtn.setDisable(false);
        confirmItemBtn.setDisable(true);

        // Alert customer item was successfully received
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Item #" + (itemCounter - 1) + " accepted");
        alert.showAndWait();
    }

    @FXML
    public void viewOrderClicked(Event e) {

        // Used to build items in order Details to display to customer
        sb = new StringBuilder();

        // Iterate through ordered items and add to String
        String itemsAsString = "";
        for (String transactionItem : itemsOrderedDetails) {
            itemsAsString = sb.append(transactionItem + "\n").toString();
        }

        // Show customer all their items ordered order
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(itemsAsString);
        alert.showAndWait();
    }

    @FXML
    public void finishOrderClicked(Event e) {

        // Used to build receipt to display to customer
        sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yy, h:mm:ss a z");
        String date = dateFormat.format(new Date());

        // String to build receipt
        String orderAsString;
        orderAsString = sb.append("Date: " + date + "\n\n"
                + "Number of line items: " + (itemCounter - 1) + "\n\n"
                + "Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n").toString();

        // Loop through items ordered and add to receipt
        for (String transactionItem : itemsOrderedDetails) {
            orderAsString = sb.append(transactionItem + "\n").toString();
        }

        // Add subtotal, tax, total details and a thank you message to the receipt
        orderAsString = sb.append("\n\n" + "Order Subtotal: "
                        + String.format("$%.02f", subtotal) + "\n\n"
                        + "Tax Rate:    6%\n\n Tax amount:    " + String.format("$%.02f", (subtotal * .06)) + "\n\n"
                        + "Order total:    " + String.format("$%.02f", (subtotal * 1.06)) + "\n\n"
                        + "Thanks for shopping at Nile Dot Com!").toString();

        // Display receipt to customer
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(orderAsString);
        alert.showAndWait();

        // Call function that creates the transactions file with all the item details
        createTransactionFile();
    }

    @FXML
    public void newOrderClicked(Event e) {

        // Reset all the buttons to their original state
        processItemBtn.setDisable(false);
        confirmItemBtn.setDisable(true);
        viewOrderBtn.setDisable(true);
        finishOrderBtn.setDisable(true);
        newOrderBtn.setDisable(false);
        exitBtn.setDisable(false);

        // Clear list of items ordered and its details
        orderedItems.clear();
        itemsOrderedDetails.clear();

        // Clear TextFields to original state
        numberItemsTextField.clear();
        itemInfoTextField.clear();
        subtotalTextField.clear();

        // Reset itemCounter, numItemsInOrder and subtotal to original state
        itemCounter = 1;
        numItemsInOrder = 0;
        subtotal = 0;
    }

    @FXML
    public void exitClicked(Event e) {

        // Exit application
        Platform.exit();
    }
}
