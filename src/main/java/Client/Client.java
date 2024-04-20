package Client;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void registerOrLoginPrompt(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
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

    private static void loginUser(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
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

    private static void fanMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
        boolean logout = false;

        while (!logout) {
            System.out.println("\nFan Menu:");
            System.out.println("1. Edit Profile");
            System.out.println("2. Browse Idols");
            System.out.println("3. View Interaction History");
            System.out.println("4. View Available Idol Schedules");
            System.out.println("5. Reserve Meetup");
            System.out.println("6. Payments");
            System.out.println("7. Meetup Now");
            System.out.println("8. Feedback");
            System.out.println("9. Report");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");

            // Check if the input is an integer
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEditing Profile...");
                        menuEditFanProfile(writer, reader, scanner);
                        break;
                    case 2:
                        System.out.println("\nBrowsing Idols...");
                        browseIdols(writer, reader, scanner);
                        break;
                    case 3:
                        System.out.println("\nViewing Interaction History...");
                        fanViewInteractionHistory(writer, reader, scanner);
                        break;
                    case 4:
                        System.out.println("\nViewing Available Idol Schedules...");
                        viewSchedules(writer, reader, scanner);
                        break;
                    case 5:
                        System.out.println("\nReserving Meetups...");
                        reserveMeetup(writer, reader, scanner);
                        break;
                    case 6:
                        System.out.println("\nOpening Payments...");
                        System.out.println("1. Credit Card");
                        System.out.println("2. Debit Card");
                        System.out.println("3. PayPal");
                        System.out.println("4. GCash");
                        System.out.print("Enter your choice: ");

                        if (scanner.hasNextInt()) {
                            int paymentChoice = scanner.nextInt();
                            switch (paymentChoice) {
                                case 1:
                                    // Send a request to the server to make a payment using a credit card
                                    writer.write("MAKE_PAYMENT,Credit Card\n");
                                    writer.flush();
                                    System.out.println("\nPayment Successful...");
                                    System.out.println("\nReturning to Fan Menu...");
                                    break;
                                case 2:
                                    // Send a request to the server to make a payment using a debit card
                                    writer.write("MAKE_PAYMENT,Debit Card\n");
                                    writer.flush();
                                    System.out.println("\nPayment Successful...");
                                    System.out.println("\nReturning to Fan Menu...");
                                    break;
                                case 3:
                                    // Send a request to the server to make a payment using PayPal
                                    writer.write("MAKE_PAYMENT,PayPal\n");
                                    writer.flush();
                                    System.out.println("\nPayment Successful...");
                                    System.out.println("\nReturning to Fan Menu...");
                                    break;
                                case 4:
                                    // Send a request to the server to make a payment using GCash
                                    writer.write("MAKE_PAYMENT,GCash\n");
                                    writer.flush();
                                    System.out.println("\nPayment Successful...");
                                    System.out.println("\nReturning to Fan Menu...");
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
                        break;
                    case 7:
                        System.out.println("\nMeeting Your Idol...");
                        fanMeetup(reader, writer, scanner);
                        break;
                    case 8:
                        System.out.println("\nWriting a Feedback...");
                        writeFeedback(scanner, writer, reader);
                        break;
                    case 9:
                        System.out.println("\nReporting an Idol...");
                        reportIdol(scanner, writer, reader);
                        break;
                    case 10:
                        System.out.println("\nLogging Out...");
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

    private static void idolMenu(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
        boolean logout = false;

        while (!logout) {
            System.out.println("\nIdol Menu:");
            System.out.println("1. Edit Profile");
            System.out.println("2. View Total Earnings");
            System.out.println("3. View Interaction History");
            System.out.println("4. Meet Now");
            System.out.println("5. View Feedbacks");
            System.out.println("6. Report");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            // Check if the input is an integer
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEditing Profile...");
                        menuEditIdolProfile(writer, reader, scanner);
                        break;
                    case 2:
                        System.out.println("\nViewing Total Earnings...");
                        viewTotalEarnings(writer, reader, scanner);
                        break;
                    case 3:
                        System.out.println("\nViewing Interaction History...");
                        idolViewInteractionHistory(writer, reader, scanner);
                        break;
                    case 4:
                        System.out.println("\nMeeting Your Fan...");
                        idolMeetup(reader, writer, scanner);
                        break;
                    case 5:
                        System.out.println("\nViewing Feedbacks...");
                        idolViewFeedbacks(writer, reader);
                        break;
                    case 6:
                        System.out.println("\nReporting a Fan...");
                        reportFan(scanner, writer, reader);
                        break;
                    case 7:
                        System.out.println("\nLogging Out...");
                        System.out.println("\nThank you for using Eyeball!");
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
                System.out.println("1. Edit Full Name");
                System.out.println("2. Edit Alias");
                System.out.println("3. Edit Email");
                System.out.println("4. Edit Password");
                System.out.println("5. Edit Idol Type");
                System.out.println("6. Edit Bio");
                System.out.println("7. Edit QBit Rate per 10 Minutes");
                System.out.println("8. Edit Availability");
                System.out.println("9. Exit Idol Edit Profile Menu");
                System.out.print("Enter your choice: ");

                // Check if the input is an integer
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            System.out.println("\nEditing Full Name...");
                            editIdolFullName(writer, reader, scanner);
                            break;
                        case 2:
                            System.out.println("\nEditing Alias...");
                            editIdolAlias(writer, reader, scanner);
                            break;
                        case 3:
                            System.out.println("\nEditing Email...");
                            editIdolEmail(writer, reader, scanner);
                            break;
                        case 4:
                            System.out.println("\nEditing Password...");
                            editIdolPassword(writer, reader, scanner);
                            break;
                        case 5:
                            System.out.println("\nEditing Idol Type...");
                            editIdolType(writer, reader, scanner);
                            break;
                        case 6:
                            System.out.println("\nEditing Bio...");
                            editIdolBio(writer, reader, scanner);
                            break;
                        case 7:
                            System.out.println("\nEditing QBit Rate per 10 Minutes...");
                            editIdolQBitRate(writer, reader, scanner);
                            break;
                        case 8:
                            System.out.println("\nEditing Availability...");
                            setAvailability(writer, reader, scanner);
                            break;
                        case 9:
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

    private static void menuEditFanProfile(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean exitMenu = false;

        try (Socket socket = new Socket(serverIP, serverPort)) {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do {
                // Fan Edit Profile Menu Options
                System.out.println("\nFan Edit Profile Menu:");
                System.out.println("1. Edit Full Name");
                System.out.println("2. Edit Username");
                System.out.println("3. Edit Email");
                System.out.println("4. Edit Password");
                System.out.println("5. Edit Gender");
                System.out.println("6. Edit Birthdate");
                System.out.println("7. Edit Bio");
                System.out.println("8. Exit Fan Edit Profile Menu");
                System.out.print("Enter your choice: ");

                // Check if the input is an integer
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            System.out.println("\nEditing Full Name...");
                            editFanFullName(writer, reader, scanner);
                            break;
                        case 2:
                            System.out.println("\nEditing Username...");
                            editFanUsername(writer, reader, scanner);
                            break;
                        case 3:
                            System.out.println("\nEditing Email...");
                            editFanEmail(writer, reader, scanner);
                            break;
                        case 4:
                            System.out.println("\nEditing Password...");
                            editFanPassword(writer, reader, scanner);
                            break;
                        case 5:
                            System.out.println("\nEditing Gender...");
                            editFanGender(writer, reader, scanner);
                            break;
                        case 6:
                            System.out.println("\nEditing Birthdate...");
                            editFanBirthdate(writer, reader, scanner);
                            break;
                        case 7:
                            System.out.println("\nEditing Bio...");
                            editFanBio(writer, reader, scanner);
                            break;
                        case 8:
                            System.out.println("\nReturning to Fan Menu...");
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

    private static void editIdolFullName(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new name: ");
        String newName = scanner.nextLine(); // Consume the newline character
        newName = scanner.nextLine(); // Read the new name

        // Send the new name to the server for updating
        writer.write("EDIT_IDOL_NAME," + idolID + "," + newName + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolAlias(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new alias: ");
        String newAlias = scanner.nextLine(); // Consume the newline character
        newAlias = scanner.nextLine(); // Read the new alias

        // Send the new alias to the server for updating
        writer.write("EDIT_IDOL_ALIAS," + idolID + "," + newAlias + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolEmail(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new email: ");
        String newEmail = scanner.next(); // Read the new email

        // Send the new email to the server for updating
        writer.write("EDIT_IDOL_EMAIL," + idolID + "," + newEmail + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolPassword(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new password: ");
        String newPassword = scanner.next(); // Read the new password

        // Send the new password to the server for updating
        writer.write("EDIT_IDOL_PASSWORD," + idolID + "," + newPassword + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolType(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new idol type: ");
        String newIdolType = scanner.nextLine(); // Consume the newline character
        newIdolType = scanner.nextLine(); // Read the new idol type

        // Send the new idol type to the server for updating
        writer.write("EDIT_IDOL_TYPE," + idolID + "," + newIdolType + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolBio(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new bio: ");
        String newBio = scanner.nextLine(); // Consume the newline character
        newBio = scanner.nextLine(); // Read the new bio

        // Send the new bio to the server for updating
        writer.write("EDIT_IDOL_BIO," + idolID + "," + newBio + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editIdolQBitRate(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new QBit Rate per 10 mins: ");
        String newQBitRate = scanner.nextLine(); // Consume the newline character
        newQBitRate = scanner.nextLine(); // Read the new QBit Rate

        // Send the new QBit Rate to the server for updating
        writer.write("EDIT_IDOL_QBIT_RATE," + idolID + "," + newQBitRate + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
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
        System.out.println("\n" + response);
    }

    private static void viewSchedules(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
        boolean continueViewing = true;

        while (continueViewing) {
            System.out.println("\nChoose search option:");
            System.out.println("1. Search by idol alias");
            System.out.println("2. Search by available day");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            String searchTerm = "";
            if (choice == 1) {
                System.out.print("\nEnter the idol alias to search: ");
                scanner.nextLine(); // consume newline
                searchTerm = scanner.nextLine();
            } else if (choice == 2) {
                System.out.print("\nEnter the available day to search (e.g. Monday): ");
                scanner.nextLine(); // consume newline
                searchTerm = scanner.nextLine();
            } else {
                System.out.println("\nInvalid choice.");
                return;
            }

            // Send the request with the search term to the server
            writer.write("VIEW_SCHEDULES," + choice + "," + searchTerm + "\n");
            writer.flush();

            // Display the schedule table after receiving the response from the server
            String response = reader.readLine();
            if (response.equals("SCHEDULES_FOUND")) {
                System.out.println("\nAvailable Schedules:");
                System.out.println("--------------------------------------------------------------------");
                System.out.println("| Idol                 |  Available Day  | Start Time | End Time   |");
                System.out.println("--------------------------------------------------------------------");

                // Get the schedules from the response and display them in a table
                String[] schedules = reader.readLine().split(",");
                for (String schedule : schedules) {
                    String[] fields = schedule.split("\\|");
                    System.out.printf("| %-20s | %-15s | %-10s | %-10s |%n", fields[0].trim(), fields[1].trim(), fields[2].trim(), fields[3].trim());
                }

                System.out.println("--------------------------------------------------------------------");

                // Ask user if they want to view another schedule
                System.out.print("\nDo you want to view another schedule? (yes/no): ");
                String viewAnother = scanner.next();
                if (viewAnother.equalsIgnoreCase("no")) {
                    continueViewing = false;
                }
            } else if (response.equals("NO_SCHEDULES_FOUND")) {
                System.out.println("\nNo schedules found.");
                continueViewing = false;
            } else if (response.equals("INVALID_CHOICE")) {
                System.out.println("\nInvalid choice.");
                continueViewing = false;
            } else {
                System.out.println("\nUnexpected response from server.");
                continueViewing = false;
            }
        }

        // If user chose to exit, go back to fan menu
        if (!continueViewing) {
            System.out.println("\nReturning to Fan Menu...");
            fanMenu(writer, reader, scanner);
        }
    }

    private static void viewTotalEarnings(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Send request to the server to retrieve the earnings data
        writer.write("VIEW_TOTAL_EARNINGS," + idolID + "\n");
        writer.flush();

        // Receive earnings data from the server
        String response = reader.readLine();
        if (response.equals("EARNINGS_FOUND")) {
            System.out.println("\nEarnings:");
            System.out.println("--------------------------------------------");
            System.out.println("| Year | Total in Dollars | Total in Qbits |");
            System.out.println("--------------------------------------------");

            // Get the earnings data from the response and display them in a table
            String earningsData = reader.readLine();
            String[] earnings = earningsData.split(",");
            for (String earning : earnings) {
                String[] fields = earning.split("\\|");
                String year = fields[0];
                String[] yearField = year.split("-");
                String totalInDollars = fields[1];
                String totalInQbits = fields[2];

                System.out.printf("| %-5s | %-15s | %-14s |%n", yearField[0].trim(), totalInDollars, totalInQbits);
            }

            System.out.println("--------------------------------------------");
            System.out.println("\nReturning to Idol Menu...");
        } else if (response.equals("NO_EARNINGS_FOUND")) {
            System.out.println("\nNo earnings found.");
        } else {
            System.out.println("\nUnexpected response from server.");
        }
    }

    private static void idolViewFeedbacks(BufferedWriter writer, BufferedReader reader) throws IOException {
        // Send request to the server to view feedbacks
        writer.write("VIEW_FEEDBACKS," + idolID + "\n");
        writer.flush();

        // Receive and display the feedbacks data
        String response = reader.readLine();
        if (response.equals("FEEDBACKS_FOUND")) {
            System.out.println("\nFeedbacks:");
            System.out.println("------------------------------------");
            System.out.println("| Feedback ID | Meetup ID | Rating |");
            System.out.println("------------------------------------");

            String feedbacksData = reader.readLine();
            String[] feedbacks = feedbacksData.split(",");
            for (String feedback : feedbacks) {
                String[] fields = feedback.split("\\|");
                if (fields.length >= 3) { // Check if fields array has at least 3 elements
                    String feedbackID = fields[0];
                    String meetupID = fields[1];
                    String rating = fields[2];

                    System.out.printf("| %-11s | %-9s | %-6s |%n", feedbackID, meetupID, rating);
                } else {
                    System.out.println("Invalid feedback format: " + feedback);
                }
            }
            System.out.println("------------------------------------");
            System.out.println("\nReturning to Idol Menu...");
        } else if (response.equals("NO_FEEDBACKS_FOUND")) {
            System.out.println("\nNo feedbacks found.");
        }
    }

    private static void editFanFullName(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new full name: ");
        String newFullName = scanner.nextLine(); // Consume the newline character
        newFullName = scanner.nextLine(); // Read the new full name

        // Send the new full name to the server for updating
        writer.write("EDIT_FAN_FULLNAME," + fanID + "," + newFullName + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanUsername(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new username: ");
        String newUsername = scanner.nextLine(); // Consume the newline character
        newUsername = scanner.nextLine(); // Read the new username

        // Send the new username to the server for updating
        writer.write("EDIT_FAN_USERNAME," + fanID + "," + newUsername + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanEmail(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new email: ");
        String newEmail = scanner.next(); // Read the new email

        // Send the new email to the server for updating
        writer.write("EDIT_FAN_EMAIL," + fanID + "," + newEmail + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanPassword(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new password: ");
        String newPassword = scanner.next(); // Read the new password

        // Send the new password to the server for updating
        writer.write("EDIT_FAN_PASSWORD," + fanID + "," + newPassword + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanGender(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new gender: ");
        String newGender = scanner.next(); // Read the new gender

        // Send the new gender to the server for updating
        writer.write("EDIT_FAN_GENDER," + fanID + "," + newGender + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanBirthdate(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new birthdate (YYYY-MM-DD): ");
        String newBirthdate = scanner.next(); // Read the new birthdate

        // Send the new birthdate to the server for updating
        writer.write("EDIT_FAN_BIRTHDATE," + fanID + "," + newBirthdate + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void editFanBio(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter new bio: ");
        String newBio = scanner.nextLine(); // Consume the newline character
        newBio = scanner.nextLine(); // Read the new bio

        // Send the new bio to the server for updating
        writer.write("EDIT_FAN_BIO," + fanID + "," + newBio + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        System.out.println("\n" + response);
    }

    private static void browseIdols(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        boolean backToMenu = false;
        while (!backToMenu) {
            System.out.println("\nBrowse Idols Menu:");
            System.out.println("1. Search Idols by Alias");
            System.out.println("2. Exit Browse Idols Menu");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nSearching Idols by Alias...");
                        System.out.print("Enter the alias of the idol you want to search: ");
                        scanner.nextLine(); // Consume newline character
                        String searchAlias = scanner.nextLine().trim();
                        if (!searchAlias.isEmpty()) {
                            // Send search request to the server
                            writer.write("BROWSE_IDOL," + searchAlias + "\n");
                            writer.flush();
                            // Handle the search response
                            String searchResponse = reader.readLine();
                            if (searchResponse.equals("IDOL_FOUND")) {
                                String searchResult = reader.readLine();
                                String[] fields = searchResult.split("\\|");
                                String alias = fields[0];
                                String idolType = fields[1];
                                String idolBio = fields[2];
                                double qbitRatePer10Mins = Double.parseDouble(fields[3]);

                                System.out.println("\nSearch Result/s:");
                                System.out.println("-------------------------------");
                                System.out.println("Alias: " + alias);
                                System.out.println("Type: " + idolType);
                                System.out.println("Bio: " + idolBio);
                                System.out.println("Qbit Rate per 10 Mins: " + qbitRatePer10Mins);
                                System.out.println("-------------------------------");
                            } else {
                                System.out.println("\nIdol not found.");
                            }
                        }
                        break;
                    case 2:
                        System.out.println("\nReturning to Fan Menu...");
                        backToMenu = true;
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

    private static void fanViewInteractionHistory(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
        // Send request to the server to view the interaction history
        writer.write("VIEW_INTERACTION_HISTORY," + fanID + "\n");
        writer.flush();

        // Receive and display the interaction history data
        String response = reader.readLine();
        if (response.equals("INTERACTION_HISTORY_FOUND")) {
            System.out.println("\nInteraction History:");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("| Meetup ID  | Duration (mins) | Scheduled Date  | Scheduled Time  |      Idol Alias      |   Fan Full Name    |");
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            String interactionHistoryData = reader.readLine();
            String[] interactions = interactionHistoryData.split(",");
            for (String interaction : interactions) {
                String[] fields = interaction.split("\\|");
                String meetupID = fields[0];
                int durationInMinutes = Integer.parseInt(fields[1]);
                String scheduledDate = fields[2];
                String scheduledTime = fields[3];
                String idolAlias = fields[4];
                String fanFullName = fields[5];

                System.out.printf("| %-10s | %-15d | %-15s | %-15s | %-20s | %-15s |%n", meetupID, durationInMinutes, scheduledDate, scheduledTime, idolAlias, fanFullName);
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("\nReturning to Fan Menu...");
        } else if (response.equals("NO_INTERACTION_HISTORY_FOUND")) {
            System.out.println("\nNo interaction history found.");
        }
    }

    private static void idolViewInteractionHistory(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException, SQLException {
        // Send request to the server to view the interaction history
        writer.write("VIEW_INTERACTION_HISTORY_1," + idolID + "\n");
        writer.flush();

        // Receive and display the interaction history data
        String response = reader.readLine();
        if (response.equals("INTERACTION_HISTORY_FOUND_1")) {
            System.out.println("\nInteraction History:");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("| Meetup ID  | Duration (mins) | Scheduled Date  | Scheduled Time  |      Idol Alias      |   Fan Full Name    |");
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            String interactionHistoryData = reader.readLine();
            String[] interactions = interactionHistoryData.split(",");
            for (String interaction : interactions) {
                String[] fields = interaction.split("\\|");
                String meetupID = fields[0];
                int durationInMinutes = Integer.parseInt(fields[1]);
                String scheduledDate = fields[2];
                String scheduledTime = fields[3];
                String idolAlias = fields[4];
                String fanFullName = fields[5];

                System.out.printf("| %-10s | %-15d | %-15s | %-15s | %-20s | %-18s |%n", meetupID, durationInMinutes, scheduledDate, scheduledTime, idolAlias, fanFullName);
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("\nReturning to Idol Menu...");
        } else if (response.equals("NO_INTERACTION_HISTORY_FOUND_1")) {
            System.out.println("\nNo interaction history found.");
        }
    }

    private static void reserveMeetup(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.print("\nEnter the alias of the idol you want to reserve a meeting with: ");
        String idolAlias = scanner.nextLine();
        idolAlias = scanner.nextLine();

        System.out.print("Enter the duration of the meeting in minutes: ");
        int durationInMinutes = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter the scheduled date (YYYY-MM-DD): ");
        String scheduledDate = scanner.nextLine();

        System.out.print("Enter the scheduled time (HH:MM:SS): ");
        String scheduledTime = scanner.nextLine();

        // Send the meetup reservation request to the server
        writer.write("RESERVE_MEETUP," + fanID + "," + idolAlias + "," + durationInMinutes + "," + scheduledDate + "," + scheduledTime + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        if (response.equals("MEETUP_RESERVED")) {
            System.out.println("\nMeetup Reserved Successfully...");
        } else if (response.equals("MEETUP_OUTSIDE_IDOL_AVAILABILITY")) {
            System.out.println("Meetup is outside idol's availability.");
        } else if (response.equals("IDOL_NOT_FOUND")) {
            System.out.println("Idol not found.");
        } else if (response.equals("MEETUP_ALREADY_SCHEDULED")) {
            System.out.println("Meetup already scheduled.");
        } else {
            System.out.println("Error reserving meetup.");
        }
    }

    public static void fanMeetup(BufferedReader reader, BufferedWriter writer, Scanner scanner) throws IOException {
        System.out.print("\nEnter the meetup ID you want to access: ");
        String meetupID = scanner.nextLine();
        meetupID = scanner.nextLine();

        // Send the meetup access request to the server
        writer.write("ACCESS_MEETUP," + meetupID + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        String[] parts = response.split(",");

        if (response.startsWith("MEETUP_PENDING")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime scheduledDateTime = LocalDateTime.parse(parts[3] + " " + parts[4], formatter);
            if (currentDateTime.isBefore(scheduledDateTime) || currentDateTime.isAfter(scheduledDateTime)) {
                System.out.println("\nYou have no meeting at this moment...");
                System.out.println("\nReturning to Fan Menu...");
            } else {
                System.out.println("\nMeetup Details:");
                System.out.println("Idol: " + parts[1]);
                System.out.println("Duration: " + parts[2] + " minutes");
                System.out.println("Date: " + parts[3]);
                System.out.println("Time: " + parts[4]);

                boolean endMeetup = false;
                System.out.print("\nWould you like to end the meetup? (yes/no): ");
                String endMeetupChoice = scanner.nextLine().trim().toLowerCase();

                if (endMeetupChoice.equals("yes")) {
                    endMeetup = true;
                    writer.write("END_MEETUP," + meetupID + "\n");
                    writer.flush();
                    String endResponse = reader.readLine();
                    if (endResponse.equals("MEETUP_ENDED")) {
                        System.out.println("\nMeetup ended successfully...");
                    } else if (endResponse.equals("MEETUP_UPDATED")) {
                        System.out.println("\nReturning to Fan Menu...");
                    }
                }

                if (endMeetup) {
                    // Update the Meetup table status to "Finished"
                    writer.write("UPDATE_MEETUP_STATUS," + meetupID + ",Finished\n");
                    writer.flush();
                }
            }
        } else if (response.equals("MEETUP_FINISHED")) {
            System.out.println("\nThe meetup is already finished...");
        } else if (response.equals("MEETUP_TO_PAY")) {
            System.out.println("\nYou need to pay first before accessing this meetup...");
        } else if (response.equals("MEETUP_NOT_FOUND")) {
            System.out.println("\nMeetup ID not found...");
        }
    }

    public static void idolMeetup(BufferedReader reader, BufferedWriter writer, Scanner scanner) throws IOException {
        System.out.print("\nEnter the meetup ID you want to access: ");
        String meetupID = scanner.nextLine();
        meetupID = scanner.nextLine();

        // Send the meetup access request to the server
        writer.write("ACCESS_MEETUP_1," + meetupID + "\n");
        writer.flush();

        // Receive and display server response
        String response = reader.readLine();
        String[] parts = response.split(",");

        if (response.startsWith("MEETUP_PENDING")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime scheduledDateTime = LocalDateTime.parse(parts[3] + " " + parts[4], formatter);
            if (currentDateTime.isBefore(scheduledDateTime) || currentDateTime.isAfter(scheduledDateTime)) {
                System.out.println("\nYou have no meeting at this moment...");
                System.out.println("\nReturning to Idol Menu...");
            } else {
                System.out.println("\nMeetup Details:");
                System.out.println("Fan: " + parts[1]);
                System.out.println("Duration: " + parts[2] + " minutes");
                System.out.println("Date: " + parts[3]);
                System.out.println("Time: " + parts[4]);

                boolean endMeetup = false;
                System.out.print("\nWould you like to end the meetup? (yes/no): ");
                String endMeetupChoice = scanner.nextLine().trim().toLowerCase();

                if (endMeetupChoice.equals("yes")) {
                    endMeetup = true;
                    writer.write("END_MEETUP," + meetupID + "\n");
                    writer.flush();
                    String endResponse = reader.readLine();
                    if (endResponse.equals("MEETUP_ENDED")) {
                        System.out.println("\nMeetup ended successfully...");
                    } else if (endResponse.equals("MEETUP_UPDATED")) {
                        System.out.println("\nReturning to Idol Menu...");
                    }
                }

                if (endMeetup) {
                    // Update the Meetup table status to "Finished"
                    writer.write("UPDATE_MEETUP_STATUS," + meetupID + ",Finished\n");
                    writer.flush();
                }
            }
        } else if (response.equals("MEETUP_FINISHED")) {
            System.out.println("\nThe meetup is already finished...");
        } else if (response.equals("MEETUP_TO_PAY")) {
            System.out.println("\nThe fan needs to pay first before accessing this meetup...");
        } else if (response.equals("MEETUP_NOT_FOUND")) {
            System.out.println("\nMeetup ID not found...");
        }
    }

    public static void writeFeedback(Scanner scanner, BufferedWriter writer, BufferedReader reader) throws IOException {
        System.out.println("Enter the meetup ID: ");
        String meetupID = scanner.nextLine();
        meetupID = scanner.nextLine();

        // Prompt fan for rating and comment
        System.out.print("Enter your rating (1-5): ");
        String rating = scanner.nextLine();
        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();

        // Send feedback request to the server
        writer.write("WRITE_FEEDBACK," + meetupID + "," + rating + "," + comment + "\n");
        writer.flush();

        // Receive and display server response
        String feedbackResponse = reader.readLine();
        System.out.println(feedbackResponse);
        System.out.println("\nReturning to Fan Menu...");
    }

    public static void reportFan(Scanner scanner, BufferedWriter writer, BufferedReader reader) throws IOException {
        System.out.println("Enter the meetup ID: ");
        String meetupID = scanner.nextLine();
        meetupID = scanner.nextLine();

        // Prompt idol for report type and description
        System.out.println("\nChoose a report type:");
        System.out.println("1. Inappropriate Behavior");
        System.out.println("2. Verbal Abuse");
        System.out.println("3. Harassment");
        System.out.print("Enter your choice: ");
        String reportType = scanner.nextLine();
        String reportTypeText;
        switch (reportType) {
            case "1":
                reportTypeText = "Inappropriate Behavior";
                break;
            case "2":
                reportTypeText = "Verbal Abuse";
                break;
            case "3":
                reportTypeText = "Harassment";
                break;
            default:
                System.out.println("Invalid report type.");
                return;
        }
        System.out.print("Enter your report description: ");
        String reportDescription = scanner.nextLine();

        // Send report request to the server
        writer.write("REPORT_FAN," + meetupID + "," + reportTypeText + "," + reportDescription + "\n");
        writer.flush();

        // Receive and display server response
        String reportResponse = reader.readLine();
        System.out.print(reportResponse);
        System.out.println("\nFan Reported. Returning to Idol Menu...");
    }

    public static void reportIdol(Scanner scanner, BufferedWriter writer, BufferedReader reader) throws IOException {
        System.out.println("Enter the meetup ID: ");
        String meetupID = scanner.nextLine();
        meetupID = scanner.nextLine();

        // Prompt fan for report type and description
        System.out.println("\nChoose a report type:");
        System.out.println("1. Inappropriate Behavior");
        System.out.println("2. Verbal Abuse");
        System.out.println("3. Harassment");
        System.out.print("Enter your choice: ");
        String reportType = scanner.nextLine();
        String reportTypeText;
        switch (reportType) {
            case "1":
                reportTypeText = "Inappropriate Behavior";
                break;
            case "2":
                reportTypeText = "Verbal Abuse";
                break;
            case "3":
                reportTypeText = "Harassment";
                break;
            default:
                System.out.println("Invalid report type.");
                return;
        }
        System.out.print("Enter your report description: ");
        String reportDescription = scanner.nextLine();

        // Send report request to the server
        writer.write("REPORT_IDOL," + meetupID + "," + reportTypeText + "," + reportDescription + "\n");
        writer.flush();

        // Receive and display server response
        String reportResponse = reader.readLine();
        System.out.print(reportResponse);
        System.out.println("\nIdol Reported. Returning to Fan Menu...");
    }
}