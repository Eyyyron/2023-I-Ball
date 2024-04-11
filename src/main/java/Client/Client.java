package Client;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
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
            System.out.println("5. Logout");
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
                        viewInteractionHistory(writer, reader, scanner, Integer.parseInt(fanID));
                        break;
                    case 4:
                        System.out.println("\nViewing Available Idol Schedules...");
                        viewSchedules(writer, reader, scanner);
                        break;
                    case 5:
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
                        System.out.println("\nEditing Profile...");
                        menuEditIdolProfile(writer, reader, scanner);
                        break;
                    case 2:
                        /**
                         * PLACEHOLDER FOR (IDOL) VIEWING TOTAL EARNINGS
                         */
                        System.out.println("\nViewing Total Earnings...");
                        viewTotalEarnings(writer, reader, scanner);
                        break;
                    case 3:
                        /**
                         * PLACEHOLDER FOR (IDOL) VIEWING INTERACTION HISTORY
                         */
                        System.out.println("\nViewing Interaction History...");
                        break;
                    case 4:
                        System.out.println("\nViewing Feedbacks...");
                        viewFeedbacks(writer, reader, scanner);
                        break;
                    case 5:
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
            System.out.println("--------------------------------------------------------------------");
            System.out.println("| Year | Total in Dollars | Total in Qbits |");
            System.out.println("--------------------------------------------------------------------");

            // Get the earnings data from the response and display them in a table
            String earningsData = reader.readLine();
            String[] earnings = earningsData.split(",");
            for (String earning : earnings) {
                String[] fields = earning.split("\\|");
                String year = fields[0];
                String[] yearField = year.split("-");
                String totalInDollars = fields[1];
                String totalInQbits = fields[2];

                System.out.printf("| %-5s | %-15s | %-15s |%n", yearField[0].trim(), totalInDollars, totalInQbits);
            }

            System.out.println("--------------------------------------------------------------------");
        } else if (response.equals("NO_EARNINGS_FOUND")) {
            System.out.println("\nNo earnings found.");
        } else {
            System.out.println("\nUnexpected response from server.");
        }
    }

    private static void viewFeedbacks(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        // Send feedback request to the server
        writer.write("VIEW_FEEDBACKS," + idolID + "\n");
        writer.flush();

        // Receive feedbacks from the server
        String response = reader.readLine();
        if (response.equals("FEEDBACKS_FOUND")) {
            System.out.println("\nFeedbacks:");
            System.out.println("-------------------------------");
            String feedbacksData = reader.readLine();
            String[] feedbacks = feedbacksData.split(",");
            for (String feedback : feedbacks) {
                String[] fields = feedback.split("\\|");
                String username = fields[0]; // Fan's Username
                int rating = Integer.parseInt(fields[1]);
                String comment = fields[2];

                System.out.println("Fan's Username: " + username);
                System.out.println("Rating: " + rating);
                System.out.println("Comment: " + comment);
                System.out.println("-------------------------------");
                System.out.println("\nReturning to Idol Menu...");
            }
        } else if (response.equals("NO_FEEDBACKS_FOUND")) {
            System.out.println("\nNo feedbacks found.");
        } else {
            System.out.println("\nUnexpected response from server.");
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

    private static void viewInteractionHistory(BufferedWriter writer, BufferedReader reader, Scanner scanner, int fanID) throws IOException, SQLException {
        // Send interaction history request to the server for the logged-in user
        writer.write("VIEW_INTERACTION_HISTORY," + fanID + "\n");
        writer.flush();

        // Receive interaction history from the server
        String response = reader.readLine();
        if (response.equals("INTERACTION_HISTORY_FOUND")) {
            System.out.println("\nInteraction History:");
            System.out.println("-------------------------------");
            String interactionHistoryData = reader.readLine();
            String[] interactions = interactionHistoryData.split(",");
            for (String interaction : interactions) {
                String[] fields = interaction.split("\\|");
                String meetupID = fields[0];
                String idolName = fields[1];
                String status = fields[2];
                // Display relevant interaction information
                System.out.println("Meetup ID: " + meetupID);
                System.out.println("Idol Name: " + idolName);
                System.out.println("Status: " + status);
                System.out.println("-------------------------------");
            }
            System.out.println("\nOptions:");
            System.out.println("1. Interact");
            System.out.println("2. Return");
            System.out.println("Enter your choice: ");

            // Handle additional options
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("\nEnter the meetup ID: ");
                    int meetupID = scanner.nextInt();
                    handleMeetupActions(writer, reader, scanner, meetupID, fanID);
                    break;
                case 2:
                    System.out.println("\nReturning to Fan Menu...");
                    break;
                default:
                    System.out.println("\nInvalid choice. Returning to Fan Menu...");
                    break;
            }
        } else if (response.equals("NO_INTERACTION_HISTORY_FOUND")) {
            System.out.println("\nNo interaction history found.");
        } else {
            System.out.println("\nUnexpected response from server.");
        }
    }

    private static void handleMeetupActions(BufferedWriter writer, BufferedReader reader, Scanner scanner, int meetupID, int fanID) throws IOException, SQLException {
        // Send request to the server to retrieve details of the specified meetup
        writer.write("GET_MEETUP_DETAILS," + meetupID + "," + fanID + "\n");
        writer.flush();

        // Receive meetup details from the server
        String response = reader.readLine();
        if (response.equals("MEETUP_DETAILS_FOUND")) {
            String meetupDetailsData = reader.readLine();
            String[] meetupDetails = meetupDetailsData.split("\\|");
            String idolName = meetupDetails[0];
            String status = meetupDetails[1];

            System.out.println("\nMeetup Details:");
            System.out.println("-------------------------------");
            System.out.println("Meetup ID: " + meetupID);
            System.out.println("Idol Name: " + idolName);
            System.out.println("Status: " + status);
            System.out.println("-------------------------------");

            // Handle meetup actions based on status
            if (status.equals("Pending")) {
                System.out.println("\nMeetup is pending. Available actions:");
                System.out.println("1. Meet Now");
                System.out.println("2. Return");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        meetNow(writer, reader, scanner, meetupID, fanID);
                        break;
                    case 2:
                        System.out.println("\nReturning...");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Returning to Interaction History...");
                        break;
                }
            } else if (status.equals("Finished")) {
                System.out.println("\nMeetup is finished. Available actions:");
                System.out.println("1. Report");
                System.out.println("2. Return");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reportIdol(writer, reader, scanner, meetupID, fanID);
                        break;
                    case 2:
                        System.out.println("\nReturning...");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Returning to Interaction History...");
                        break;
                }
            } else {
                System.out.println("\nInvalid meetup status. Returning to Interaction History...");
            }
        } else if (response.equals("MEETUP_NOT_FOUND_FOR_USER")) {
            System.out.println("\nMeetup does not exist. Returning...");
        } else if (response.equals("MEETUP_NOT_FOUND")) {
            System.out.println("\nMeetup does not exist. Returning...");
        } else {
            System.out.println("\nUnexpected response from server.");
        }
    }

    private static void meetNow(BufferedWriter writer, BufferedReader reader, Scanner scanner, int meetupID, int fanID) throws IOException {
        // Implement the logic to initiate the meetup
        // This could involve confirming the meetup with the server and updating its status
        // For simplicity, let's assume it's handled by the server
        // Send a request to the server to initiate the meetup
        writer.write("MEET_NOW," + meetupID + "," + fanID + "\n");
        writer.flush();

        // Receive response from the server
        String response = reader.readLine();
        if (response.equals("MEETUP_INITIATED")) {
            System.out.println("\nMeetup initiated successfully.");
        } else {
            System.out.println("\nFailed to initiate meetup.");
        }
    }

    private static void reportIdol(BufferedWriter writer, BufferedReader reader, Scanner scanner, int meetupID, int fanID) throws SQLException, IOException {
        // Prompt the fan for report type and description
        System.out.println("\nReport Type:");
        System.out.println("1. Inappropriate Behavior");
        System.out.println("2. Harassment");
        System.out.println("3. Verbal Abuse");
        System.out.print("Enter report type: ");
        int reportTypeChoice = scanner.nextInt();
        String reportType;
        switch (reportTypeChoice) {
            case 1:
                reportType = "Inappropriate Behavior";
                break;
            case 2:
                reportType = "Harassment";
                break;
            case 3:
                reportType = "Verbal Abuse";
                break;
            default:
                System.out.println("Invalid report type choice. Defaulting to 'Inappropriate Behavior'.");
                reportType = "Inappropriate Behavior";
                break;
        }

        scanner.nextLine(); // Consume newline character

        System.out.print("Enter report description: ");
        String reportDescription = scanner.nextLine();

        // Implement the logic to report the idol
        // This could involve sending a report request to the server
        // Send a request to the server to report the idol
        writer.write("REPORT_IDOL," + meetupID + "," + fanID + "," + reportType + "," + reportDescription + "\n");
        writer.flush();

        // Receive response from the server
        String response = reader.readLine();
        if (response.equals("IDOL_REPORTED")) {
            System.out.println("\nIdol reported successfully.");
        } else {
            System.out.println("\nFailed to report idol.");
        }
    }

    private static void reserveMeetup(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        System.out.println("\nReserve Meetup Menu:");

        // Ask for the alias of the idol
        System.out.print("Enter the alias of the idol you want to reserve a meetup with: ");
        String idolAlias = scanner.nextLine().trim();

        if (!idolAlias.isEmpty()) {
            // Send request to the server to get IdolID corresponding to the alias
            writer.write("GET_IDOL_ID," + idolAlias + "\n");
            writer.flush();
            // Handle the response from the server
            String idResponse = reader.readLine();
            if (idResponse.equals("IDOL_ID_FOUND")) {
                String idolID = reader.readLine();
                // Ask for meetup details
                System.out.print("Enter duration in minutes: ");
                int durationInMinutes = scanner.nextInt();
                System.out.print("Enter scheduled date (YYYY-MM-DD): ");
                scanner.nextLine(); // Consume newline character
                String scheduledDate = scanner.nextLine().trim();
                System.out.print("Enter scheduled time (HH:MM): ");
                String scheduledTime = scanner.nextLine().trim();
                String status = "To Pay";

                // Reserve the meetup with the idol
                writer.write("RESERVE_MEETUP," + fanID + "," + idolID + "," + durationInMinutes + "," + scheduledDate + "," + scheduledTime + "," + status + "\n");
                writer.flush();
                // Handle the response from the server
                String reserveResponse = reader.readLine();
                if (reserveResponse.equals("MEETUP_RESERVED")) {
                    System.out.println("\nMeetup successfully reserved!");
                } else {
                    System.out.println("\nFailed to reserve meetup. Please try again later.");
                }
            } else {
                System.out.println("\nIdol not found.");
            }
        } else {
            System.out.println("\nInvalid alias. Please enter a valid alias.");
        }
    }


}