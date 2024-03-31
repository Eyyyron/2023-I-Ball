package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the IP address and port the server class is showing right now.");

        System.out.print("\nEnter the server IP address: ");
        String serverIP = scanner.nextLine();

        System.out.print("Enter the server port: ");
        int serverPort = scanner.nextInt();

        try {
            // Connect to the server
            Socket socket = new Socket(serverIP, serverPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("\nWelcome to Eyeball!");

            // Ask the user if they want to register or login
            System.out.println("\nChoose an option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            // Perform registration or login based on user's choice
            if (choice == 1) {
                registerUser(writer, reader, scanner);
            } else if (choice == 2) {
                loginUser(writer, reader, scanner);
            } else {
                System.out.println("\nInvalid choice. Please enter 1 for registration or 2 for login.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void registerUser(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Ask the user which type of user they are
        System.out.println("\nAre you registering as a Fan or an Idol?");
        System.out.print("Enter 'F' for Fan or 'I' for Idol: ");
        String userType = scanner.next().toUpperCase();

        // Register user based on the type
        if (userType.equals("F")) {
            registerFan(writer, reader, scanner);
        } else if (userType.equals("I")) {
            registerIdol(writer, reader, scanner);
        } else {
            System.out.println("\nInvalid input. Please enter 'F' for Fan or 'I' for Idol.");
        }
    }

    private static void loginUser(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Get user login details
        System.out.print("\nEnter your email: ");
        String email = scanner.next();

        System.out.print("Enter your password: ");
        String password = scanner.next();

        // Send login request to the server
        writer.write("LOGIN," + email + "," + password + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        if (response.equals("FAN_LOGIN_SUCCESS")) {
            System.out.println("\nYou are logged in as a fan.");
            fanMenu(writer, reader, scanner);
        } else if (response.equals("IDOL_LOGIN_SUCCESS")) {
            System.out.println("\nYou are logged in as an idol.");
            idolMenu(writer, reader, scanner);
        } else {
            System.out.println("\nLogin failed. Please try again.");
        }
    }

    private static void fanMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Fan Menu Options
        System.out.println("\nFan Menu:");
        System.out.println("1. Edit Profile");
        System.out.println("2. Browse Idols");
        System.out.println("3. View Interaction History");
        System.out.println("4. Logout");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        // Process fan menu choice
        switch (choice) {
            case 1:
                /**
                 * PLACEHOLDER FOR (FAN) EDITING PROFILE
                 */
                System.out.println("\nEditing profile...");
                break;
            case 2:
                /**
                 * PLACEHOLDER FOR (FAN) BROWSING IDOLS
                 */
                System.out.println("\nBrowsing idols...");
                break;
            case 3:
                /**
                 * PLACEHOLDER FOR (FAN) VIEWING INTERACTION HISTORY
                 */
                System.out.println("\nViewing interaction history...");
                break;
            case 4:
                System.out.println("\nLogging out...");
                // Send logout request to the server
                writer.write("LOGOUT\n");
                writer.flush();
                break;
            default:
                System.out.println("\nInvalid choice.");
                break;
        }
    }

    private static void idolMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Idol Menu Options
        System.out.println("\nIdol Menu:");
        System.out.println("1. Edit Profile");
        System.out.println("2. View Total Earnings");
        System.out.println("3. View Interaction History");
        System.out.println("4. View Feedbacks");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        // Process idol menu choice
        switch (choice) {
            case 1:
                /**
                 * PLACEHOLDER FOR (IDOL) EDITING PROFILE
                 */
                System.out.println("\nEditing profile...");
                break;
            case 2:
                /**
                 * PLACEHOLDER FOR (IDOL) VIEWING TOTAL EARNINGS
                 */
                System.out.println("\nViewing total earnings...");
                break;
            case 3:
                /**
                 * PLACEHOLDER FOR (IDOL) VIEWING INTERACTION HISTORY
                 */
                System.out.println("\nViewing interaction history...");
                break;
            case 4:
                /**
                 * PLACEHOLDER FOR (IDOL) VIEWING FEEDBACKS
                 */
                System.out.println("\nViewing feedbacks...");
                break;
            case 5:
                System.out.println("\nLogging out...");
                // Send logout request to the server
                writer.write("LOGOUT\n");
                writer.flush();
                break;
            default:
                System.out.println("\nInvalid choice.");
                break;
        }
    }

    private static void registerFan(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Get fan details from the user
        System.out.print("\nEnter your full name: ");
        String fanFullName = scanner.nextLine(); // Consume the newline character
        fanFullName = scanner.nextLine(); // Read the full name

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your email: ");
        String fanEmail = scanner.nextLine();

        System.out.print("Enter your password: ");
        String fanPassword = scanner.nextLine();

        System.out.print("Enter your gender: ");
        String gender = scanner.nextLine();

        System.out.print("Enter your birthdate (YYYY-MM-DD): ");
        String birthdate = scanner.nextLine();

        System.out.print("Enter your bio: ");
        String fanBio = scanner.nextLine();

        // Send registration request to the server
        writer.write("REGISTER_FAN," + fanFullName + "," + username + "," + fanEmail + "," + fanPassword + "," + gender + "," + birthdate + "," + fanBio + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\nServer response: " + response);
    }

    private static void registerIdol(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Get idol details from the user
        System.out.print("\nEnter your full name: ");
        String idolFullName = scanner.nextLine(); // Consume the newline character
        idolFullName = scanner.nextLine(); // Read the full name

        System.out.print("Enter your alias: ");
        String alias = scanner.nextLine();

        System.out.print("Enter your email: ");
        String idolEmail = scanner.nextLine();

        System.out.print("Enter your password: ");
        String idolPassword = scanner.nextLine();

        System.out.print("Enter your idol type: ");
        String idolType = scanner.nextLine();

        System.out.print("Enter your bio: ");
        String idolBio = scanner.nextLine();

        System.out.print("Enter your Qbit rate per 10 mins: ");
        String qbitRatePer10Mins = scanner.nextLine();

        // Send registration request to the server
        writer.write("REGISTER_IDOL," + idolFullName + "," + alias + "," + idolEmail + "," + idolPassword + "," + idolType + "," + idolBio + "," + qbitRatePer10Mins + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\nServer response: " + response);
    }
}