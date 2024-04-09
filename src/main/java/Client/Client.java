package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static String serverIP;
    static int serverPort;
    private static String idolID;
    private static String fanID;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the IP address and port the server class is showing right now.");

        System.out.print("\nEnter the server IP address: ");
        serverIP = scanner.nextLine();

        System.out.print("Enter the server port: ");
        serverPort = scanner.nextInt();

        try {
            // Connect to the server
            Socket socket = new Socket(serverIP, serverPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Call the method for register/login prompt
            registerOrLoginPrompt(writer, reader, scanner);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void registerOrLoginPrompt(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean continuePrompt = true;

        while (continuePrompt) {
            System.out.println("\nWelcome to Eyeball!");

            // Ask the user if they want to register or login
            System.out.println("Choose an option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.print("Enter your choice: ");

            // Check if the input is an integer
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        registerUser(writer, reader, scanner);
                        break;
                    case 2:
                        loginUser(writer, reader, scanner);
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a valid option.");
                        break;
                }
            } else {
                // Consume non-integer input
                scanner.next();
                System.out.println("\nInvalid choice. Please enter a valid integer option.");
            }
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
        if (response.contains("FAN_LOGIN_SUCCESS")) {
            fanID = response.split(",")[1];
            System.out.println("\nLogging in as a fan...");
            fanMenu(writer, reader, scanner);
        } else if (response.contains("IDOL_LOGIN_SUCCESS")) {
            idolID = response.split(",")[1];
            System.out.println("\nLogging in as an idol...");
            idolMenu(writer, reader, scanner);
        } else {
            System.out.println("\nLogin failed. Please try again.");
        }
    }

    private static void fanMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean logout = false;

        while (!logout) {
            System.out.println("\nFan Menu:");
            System.out.println("1. Edit Profile");
            System.out.println("2. Browse Idols");
            System.out.println("3. View Interaction History");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            // Check if the input is an integer
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEditing profile...");
                        // Placeholder for editing profile
                        break;
                    case 2:
                        System.out.println("\nEnter the day you want to browse idols for (e.g., 'Monday'):");
                        scanner.nextLine(); // Clear the scanner buffer
                        String day = scanner.nextLine();
                        writer.write("BROWSE_IDOLS," + day + "\n");
                        writer.flush();

                        System.out.println("Available Idols:");
                        String response;
                        while (!(response = reader.readLine()).equals("END_OF_LIST")) {
                            System.out.println(response);
                        }
                        break;

                    case 3:
                        System.out.println("\nViewing interaction history...");
                        // Placeholder for viewing interaction history
                        break;
                    case 4:
                        System.out.println("\nLogging out...");
                        System.out.println("\nThank you for using the program.");
                        writer.write("LOGOUT\n");
                        writer.flush();
                        logout = true;
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a valid option.");
                        break;
                }
            } else {
                // Consume non-integer input
                scanner.next();
                System.out.println("\nInvalid choice. Please enter a valid integer option.");
            }
        }
    }

    private static void idolMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean logout = false;

        while (!logout) {
            System.out.println("\nIdol Menu:");
            System.out.println("1. Edit Profile");
            System.out.println("2. View Total Earnings");
            System.out.println("3. View Interaction History");
            System.out.println("4. View Feedbacks");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            // Check if the input is an integer
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEditing profile...");
                        menuEditIdolProfile(writer, reader, scanner);
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
                        System.out.println("\nThank you for using the program.");
                        writer.write("LOGOUT\n");
                        writer.flush();
                        logout = true;
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a valid option.");
                        break;
                }
            } else {
                // Consume non-integer input
                scanner.next();
                System.out.println("\nInvalid choice. Please enter a valid integer option.");
            }
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

    private static void menuEditIdolProfile(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean exitMenu = false;

        try (Socket socket = new Socket(serverIP, serverPort)) {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do {
                // Edit Profile Menu Options
                System.out.println("\nIdol Edit Profile Menu:");
                System.out.println("1. Edit Name");
                System.out.println("2. Edit Alias");
                System.out.println("3. Edit Idol Type");
                System.out.println("4. Set/Edit Bio");
                System.out.println("5. Edit QBit Rate per Minute");
                System.out.println("6. Set/Edit Availability");
                System.out.println("7. Exit Idol Edit Profile Menu");
                System.out.print("Enter your choice: ");

                // Check if the input is an integer
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            System.out.println("\nEditing Name...");
                            break;
                        case 2:
                            System.out.println("\nEditing Alias...");
                            break;
                        case 3:
                            System.out.println("\nEditing Idol Type...");
                            break;
                        case 4:
                            System.out.println("\nEditing Bio...");
                            break;
                        case 5:
                            System.out.println("\nEditing QBit Rate...");
                            editQBitRate(writer, reader, scanner);
                            break;
                        case 6:
                            System.out.println("\nEditing Availability...");
                            setAvailability(writer, reader, scanner);
                            break;
                        case 7:
                            exitMenu = true;
                            break;
                        default:
                            System.out.println("\nInvalid choice. Please enter a valid option.");
                            break;
                    }
                } else {
                    // Consume non-integer input
                    scanner.next();
                    System.out.println("\nInvalid choice. Please enter a valid integer option.");
                }
            } while (!exitMenu);
        } finally {
            // Close the socket after all operations are done
            if (writer != null) {
                writer.close(); // Close the writer
            }
            if (reader != null) {
                reader.close(); // Close the reader
            }
        }
    }

    private static void editQBitRate(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Get the new QBit rate from the user
        System.out.print("\nEnter the new QBit rate per 10 minutes: ");
        String qbitRatePer10Mins = scanner.nextLine();

        // Send QBit rate update request to the server
        writer.write("EDIT_QBIT_RATE," + idolID + "," + qbitRatePer10Mins + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\nServer response: " + response);
    }


    private static void setAvailability(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {

        // Get idol details from the user
        System.out.print("\nEnter your day of availability (e.g. Monday): \n");
        String availableDay = scanner.nextLine(); // Consume the newline character
        availableDay = scanner.nextLine();


        System.out.print("\nEnter the starting time you are available for said day: ");
        System.out.print("\nFollow the 24 hour format (e.g. '13:00:00' for 1:00PM)\n");
        String startTime = scanner.nextLine();


        System.out.print("\nEnter the time when your availablity will end for said day: ");
        System.out.print("\nFollow the 24 hour format (e.g. '18:00:00' for 6:00PM)\n");
        String endTime = scanner.nextLine();

        // Send availability set request to the server
        writer.write("SET_AVAILABILITY," + idolID + "," + availableDay + "," + startTime + "," + endTime + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\nServer response: " + response);
    }
}