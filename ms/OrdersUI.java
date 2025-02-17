import java.io.Console;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * OrdersUI is the main console-based user interface for the microservices system.
 * It uses MSClientAPI to access the AuthServices, CreateServices, RetrieveServices,
 * and DeleteServices via RMI.
 */
public class OrdersUI {

    private static Scanner keyboard = new Scanner(System.in);
    private static Console console = System.console();  // might be null in some IDEs
    private static MSClientAPI api;
    // sessionToken is null if user is not logged in
    private static String sessionToken = null; 

    public static void main(String[] args) {
        try {
            // Initialize the MSClientAPI which reads from registry.properties
            api = new MSClientAPI();
        } catch (Exception e) {
            System.out.println("Failed to initialize MSClientAPI: " + e);
            return;
        }

        boolean done = false;

        while (!done) {
            // If not logged in, show only registration/login options
            if (sessionToken == null) {
                System.out.println("\nYou are currently NOT logged in!");
                System.out.println("1: Register new user");
                System.out.println("2: Login");
                System.out.println("X: Exit\n");
                System.out.print("Enter choice -> ");

                char choice = readChar();
                switch (choice) {
                    case '1':
                        registerUser();
                        break;
                    case '2':
                        loginUser();
                        break;
                    case 'x':
                    case 'X':
                        done = true;
                        break;
                    default:
                        System.out.println("Unknown option. Try again.");
                }
            }
            // If logged in, show main order menu
            else {
                System.out.println("\nOrders Database (Microservices) -- Logged In");
                System.out.println("1: Retrieve all orders");
                System.out.println("2: Retrieve an order by ID");
                System.out.println("3: Create a new order");
                System.out.println("4: Delete an order by ID");
                System.out.println("L: Logout");
                System.out.println("X: Exit");
                System.out.print("Enter choice -> ");

                char choice = readChar();
                switch (choice) {
                    case '1':
                        retrieveAllOrders();
                        break;
                    case '2':
                        retrieveOrderById();
                        break;
                    case '3':
                        createOrder();
                        break;
                    case '4':
                        deleteOrder();
                        break;
                    case 'l':
                    case 'L':
                        logoutUser();
                        break;
                    case 'x':
                    case 'X':
                        done = true;
                        break;
                    default:
                        System.out.println("Unknown option. Try again.");
                }
            }
        }

        System.out.println("\nExiting OrdersUI... Goodbye.");
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //                      AUTHENTICATION METHODS
    ///////////////////////////////////////////////////////////////////////////////////////

    private static void registerUser() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Enter desired username: ");
        String username = keyboard.nextLine();
        System.out.print("Enter desired password: ");
        String password = keyboard.nextLine();

        try {
            String result = api.authCreateUser(username, password);
            System.out.println("Server response: " + result);
        } catch (Exception e) {
            System.out.println("Error registering user: " + e);
        }

        promptContinue();
    }

    private static void loginUser() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = keyboard.nextLine();
        System.out.print("Password: ");
        String password = keyboard.nextLine();

        try {
            // Attempt to login and retrieve a session token
            String token = api.authLoginUser(username, password);
            if (token.startsWith("ERROR")) {
                System.out.println("Login failed: " + token);
            } else {
                sessionToken = token;
                System.out.println("Login successful! Token: " + sessionToken);
            }
        } catch (Exception e) {
            System.out.println("Error logging in: " + e);
        }

        promptContinue();
    }

    private static void logoutUser() {
        System.out.println("\n--- Logout ---");
        if (sessionToken == null) {
            System.out.println("You are not logged in!");
        } else {
            try {
                String resp = api.authLogoutUser(sessionToken);
                System.out.println("Logout response: " + resp);
                sessionToken = null; // remove local token
            } catch (Exception e) {
                System.out.println("Error logging out: " + e);
            }
        }

        promptContinue();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //                      ORDER OPERATIONS
    ///////////////////////////////////////////////////////////////////////////////////////

    private static void retrieveAllOrders() {
        System.out.println("\n--- Retrieve All Orders ---");
        try {
            // The retrieveOrders method in the microservices now should require a token
            String result = api.retrieveOrders(sessionToken);
            System.out.println("Orders: " + result);
        } catch (Exception e) {
            System.out.println("Error retrieving orders: " + e);
        }

        promptContinue();
    }

    private static void retrieveOrderById() {
        System.out.println("\n--- Retrieve Order by ID ---");
        System.out.print("Enter Order ID: ");
        String id = keyboard.nextLine();

        try {
            String result = api.retrieveOrders(sessionToken, id);
            System.out.println("Order: " + result);
        } catch (Exception e) {
            System.out.println("Error retrieving order: " + e);
        }

        promptContinue();
    }

    private static void createOrder() {
        System.out.println("\n--- Create New Order ---");
        System.out.print("First Name: ");
        String first = keyboard.nextLine();
        System.out.print("Last Name: ");
        String last = keyboard.nextLine();
        System.out.print("Address: ");
        String address = keyboard.nextLine();
        System.out.print("Phone: ");
        String phone = keyboard.nextLine();

        // Typically you'd auto-generate the date in yyyy-MM-dd
        // Example: Using system date
        String date = java.time.LocalDate.now().toString();

        System.out.println("\nYou are about to create an order with:");
        System.out.println(" Date: " + date);
        System.out.println(" First Name: " + first);
        System.out.println(" Last Name: " + last);
        System.out.println(" Address: " + address);
        System.out.println(" Phone: " + phone);

        System.out.print("\nConfirm creation (y/n)? ");
        char confirm = readChar();
        if (confirm == 'y' || confirm == 'Y') {
            try {
                String resp = api.newOrder(sessionToken, date, first, last, address, phone);
                System.out.println("Creation response: " + resp);
            } catch (Exception e) {
                System.out.println("Error creating order: " + e);
            }
        } else {
            System.out.println("Order creation canceled.");
        }

        promptContinue();
    }

    private static void deleteOrder() {
        System.out.println("\n--- Delete Order ---");
        System.out.print("Enter Order ID to delete: ");
        String id = keyboard.nextLine();

        System.out.print("Are you sure you want to delete order " + id + "? (y/n) ");
        char confirm = readChar();
        if (confirm == 'y' || confirm == 'Y') {
            try {
                String resp = api.deleteOrder(sessionToken, id);
                System.out.println("Delete response: " + resp);
            } catch (Exception e) {
                System.out.println("Error deleting order: " + e);
            }
        } else {
            System.out.println("Delete operation canceled.");
        }

        promptContinue();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //                      HELPER METHODS
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reads a single character from the user input. If no input, returns a blank char.
     */
    private static char readChar() {
        String line = keyboard.nextLine();
        if (line.isEmpty()) {
            return ' ';
        }
        return line.charAt(0);
    }

    /**
     * Simple utility to pause until user hits ENTER.
     */
    private static void promptContinue() {
        System.out.println("\nPress ENTER to continue...");
        if (console != null) {
            console.readLine();
        } else {
            // Fallback for IDEs where System.console() is null
            keyboard.nextLine();
        }
    }
}
