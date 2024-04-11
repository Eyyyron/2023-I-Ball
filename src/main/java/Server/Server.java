package Server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    private static final int PORT = 12345;
    private static final String URL = "jdbc:mysql://localhost:3306/teamsea";
    private static final String USER = "root";
    private static final String PASSWORD = "ComSci_CS221";

    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Get the system's IP address dynamically
            String ipAddress = InetAddress.getLocalHost().getHostAddress();

            ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ipAddress));
            System.out.println("Server started on IP address: " + ipAddress + ", port: " + PORT);
            System.out.println("\nWaiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                //System.out.println("\nClient connected: " + clientSocket.getInetAddress());

                // Handle client requests in a separate thread
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Connection connection;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String request;
                while ((request = reader.readLine()) != null) {
                    String[] requestData = request.split(",");
                    String requestType = requestData[0];

                    if (requestType.equals("LOGOUT")) {
                        // Handle logout
                        System.out.println("\nClient logged out: " + clientSocket.getInetAddress());
                        break; // Exit the loop to end the client thread
                    }

                    if (requestType.equals("REGISTER_FAN")) {
                        // Handle fan registration
                        registerFan(requestData, writer);
                    } else if (requestType.equals("REGISTER_IDOL")) {
                        // Handle idol registration
                        registerIdol(requestData, writer);
                    } else if (requestType.equals("LOGIN")) {
                        // Handle login
                        login(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_NAME")) {
                        // Handle idol name edit
                        editIdolFullName(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_ALIAS")) {
                        // Handle idol alias edit
                        editIdolAlias(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_EMAIL")) {
                        // Handle idol email edit
                        editIdolEmail(requestData,writer);
                    } else if (requestType.equals("EDIT_IDOL_PASSWORD")) {
                        // Handle idol password edit
                        editIdolPassword(requestData,writer);
                    } else if (requestType.equals("EDIT_IDOL_TYPE")) {
                        // Handle idol type edit
                        editIdolType(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_BIO")) {
                        // Handle idol bio edit
                        editIdolBio(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_QBIT_RATE")) {
                        // Handle idol QBit Rate edit
                        editIdolQBitRate(requestData, writer);
                    } else if (requestType.equals("SET_AVAILABILITY")) {
                        // Handle setting availability of idol
                        setAvailability(requestData, writer);
                    } else if (requestType.equals("VIEW_SCHEDULES")) {
                        // Handle viewing schedules of idols
                        viewSchedules(requestData, writer);
                    } else if (requestType.equals("VIEW_TOTAL_EARNINGS")) {
                        // Handle viewing total earnings of idol
                        viewTotalEarnings(requestData, writer);
                    } else if (requestType.equals("VIEW_FEEDBACKS")) {
                        // Handle viewing feedbacks of idols
                        viewFeedbacks(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_FULLNAME")) {
                        // Handle fan full name edit
                        editFanFullName(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_USERNAME")) {
                        // Handle fan username edit
                        editFanUsername(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_EMAIL")) {
                        // Handle fan email edit
                        editFanEmail(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_PASSWORD")) {
                        // Handle fan password edit
                        editFanPassword(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_GENDER")) {
                        // Handle fan gender edit
                        editFanGender(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_BIRTHDATE")) {
                        // Handle fan birthdate edit
                        editFanBirthdate(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_BIO")) {
                        // Handle fan bio edit
                        editFanBio(requestData, writer);
                    } else if (requestType.equals("BROWSE_IDOL")) {
                        String alias = requestData[1];
                        browseIdols(alias, writer);
                    } else if (requestType.equals("VIEW_INTERACTION_HISTORY")) {
                        int fanID = Integer.parseInt(requestData[1]);
                        // Handle viewing interaction history of the logged-in user
                        viewInteractionHistory(fanID, writer);
                    } else if (requestType.equals("GET_MEETUP_DETAILS")) {
                        int meetupID = Integer.parseInt(requestData[1]);
                        int fanID = Integer.parseInt(requestData[2]);
                        // Handle getting details of a specific meetup for the specified fanID
                        getMeetupDetails(meetupID, fanID, writer);
                    } else if (requestType.equals("MEET_NOW")) {
                        int meetupID = Integer.parseInt(requestData[1]);
                        int fanID = Integer.parseInt(requestData[2]);
                        // Handle initiating the meetup
                        meetNow(meetupID, fanID, writer);
                    } else if (requestType.equals("REPORT_IDOL")) {
                        int meetupID = Integer.parseInt(requestData[1]);
                        int fanID = Integer.parseInt(requestData[2]);
                        String reportType = requestData[3]; // Extract report type
                        String reportDescription = requestData[4]; // Extract report description
                        // Handle reporting the idol
                        reportIdol(meetupID, fanID, reportType, reportDescription, writer);
                    }

                    else {
                        writer.write("Invalid request\n");
                        writer.flush();
                    }
                }
                writer.close();
                reader.close();
                clientSocket.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void registerFan(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan details from data array
            String fanFullName = data[1];
            String username = data[2];
            String fanEmail = data[3];
            String fanPassword = data[4];
            String gender = data[5];
            String birthdate = data[6];
            String fanBio = data[7];

            // Get the highest fanID from the FAN table
            String query = "SELECT MAX(FanID) FROM FAN";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int fanID = 1; // Initialize fanID to 1
            if (resultSet.next()) {
                fanID = resultSet.getInt(1) + 1; // Increase the fanID by 1
            }

            // Perform fan registration
            query = "INSERT INTO FAN (FanID, FanFullName, Username, FanEmail, FanPassword, Gender, Birthdate, FanBio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, fanID);
            preparedStatement.setString(2, fanFullName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, fanEmail);
            preparedStatement.setString(5, fanPassword);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, birthdate);
            preparedStatement.setString(8, fanBio);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Successfully Registered\n");
            } else {
                writer.write("Fan Registration Failed\n");
            }
            writer.flush();
        }

        private void registerIdol(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol details from data array
            String idolFullName = data[1];
            String alias = data[2];
            String idolEmail = data[3];
            String idolPassword = data[4];
            String idolType = data[5];
            String idolBio = data[6];
            String qbitRatePer10Mins = data[7];

            // Get the highest fanID from the IDOL table
            String query = "SELECT MAX(IdolID) FROM IDOL";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int idolID = 1; // Initialize fanID to 1
            if (resultSet.next()) {
                idolID = resultSet.getInt(1) + 1; // Increase the fanID by 1
            }

            // Perform idol registration
            query = "INSERT INTO IDOL (IdolID, IdolFullName, Alias, IdolEmail, IdolPassword, IdolType, IdolBio, QbitRatePer10Mins) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idolID);
            preparedStatement.setString(2, idolFullName);
            preparedStatement.setString(3, alias);
            preparedStatement.setString(4, idolEmail);
            preparedStatement.setString(5, idolPassword);
            preparedStatement.setString(6, idolType);
            preparedStatement.setString(7, idolBio);
            preparedStatement.setString(8, qbitRatePer10Mins);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Successfully Registered\n");
            } else {
                writer.write("Idol Registration Failed\n");
            }
            writer.flush();
        }

        private void login(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract login credentials from data array
            String email = data[1];
            String password = data[2];

            // Check if the email exists in the FAN table
            String fanQuery = "SELECT * FROM FAN WHERE FanEmail = ? AND FanPassword = ?";
            PreparedStatement fanStatement = connection.prepareStatement(fanQuery);
            fanStatement.setString(1, email);
            fanStatement.setString(2, password);
            ResultSet fanResult = fanStatement.executeQuery();

            // Check if the email exists in the IDOL table
            String idolQuery = "SELECT * FROM IDOL WHERE IdolEmail = ? AND IdolPassword = ?";
            PreparedStatement idolStatement = connection.prepareStatement(idolQuery);
            idolStatement.setString(1, email);
            idolStatement.setString(2, password);
            ResultSet idolResult = idolStatement.executeQuery();

            // Send response to client based on whether email exists in either table
            if (fanResult.next()) {
                String fanID = fanResult.getString("fanID");
                writer.write("FAN_LOGIN_SUCCESS," + fanID + "\n");
                System.out.println("\nClient logged in: " + clientSocket.getInetAddress());
            } else if (idolResult.next()) {
                String idolID = idolResult.getString("IdolID");
                writer.write("IDOL_LOGIN_SUCCESS," + idolID + "\n");
                System.out.println("\nClient logged in: " + clientSocket.getInetAddress());
            } else {
                writer.write("LOGIN_FAILED\n");
            }
            writer.flush();
        }

        private void editIdolFullName(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new name from data array
            int idolID = Integer.parseInt(data[1]);
            String newName = data[2];

            // Update the idol's name in the database
            String query = "UPDATE IDOL SET IdolFullName=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Name Updated Successfully...\n");
            } else {
                writer.write("Idol Name Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolAlias(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new alias from data array
            int idolID = Integer.parseInt(data[1]);
            String newAlias = data[2];

            // Update the idol's alias in the database
            String query = "UPDATE IDOL SET Alias=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newAlias);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Alias Updated Successfully...\n");
            } else {
                writer.write("Idol Alias Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolEmail(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new email from data array
            int idolID = Integer.parseInt(data[1]);
            String newEmail = data[2];

            // Update the fan's email in the database
            String query = "UPDATE IDOL SET IdolEmail=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newEmail);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Email Updated Successfully...\n");
            } else {
                writer.write("Idol Email Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolPassword(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new password from data array
            int idolID = Integer.parseInt(data[1]);
            String newPassword = data[2];

            // Update the fan's password in the database
            String query = "UPDATE IDOL SET IdolPassword=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Password Updated Successfully...\n");
            } else {
                writer.write("Idol Password Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolType(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new idol type from data array
            int idolID = Integer.parseInt(data[1]);
            String newIdolType = data[2];

            // Update the idol's type in the database
            String query = "UPDATE IDOL SET IdolType=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newIdolType);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Type Updated Successfully...\n");
            } else {
                writer.write("Idol Type Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolBio(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new bio from data array
            int idolID = Integer.parseInt(data[1]);
            String newBio = data[2];

            // Update the idol's bio in the database
            String query = "UPDATE IDOL SET IdolBio=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newBio);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol Bio Updated Successfully...\n");
            } else {
                writer.write("Idol Bio Update Failed...\n");
            }
            writer.flush();
        }

        private void editIdolQBitRate(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract idol ID and new QBit Rate from data array
            int idolID = Integer.parseInt(data[1]);
            String newQBitRate = data[2];

            // Update the idol's QBit Rate in the database
            String query = "UPDATE IDOL SET QbitRatePer10Mins=? WHERE IdolID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newQBitRate);
            preparedStatement.setInt(2, idolID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Idol QBit Rate Updated Successfully...\n");
            } else {
                writer.write("Idol QBit Rate Update Failed...\n");
            }
            writer.flush();
        }

        private void setAvailability(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract availability details from data array
            String idolID = data[1];
            String availableDay = data[2];
            String startTime = data[3];
            String endTime = data[4];

            // Check if the idol already has an availability for the given day
            String checkQuery = "SELECT COUNT(*) FROM AVAILABILITY WHERE IdolID =? AND AvailableDay =?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, idolID);
            checkStatement.setString(2, availableDay);
            ResultSet checkResult = checkStatement.executeQuery();
            checkResult.next();
            int availabilityCount = checkResult.getInt(1);

            if (availabilityCount > 0) {
                // The idol already has an availability for the given day, so update it
                String updateQuery = "UPDATE AVAILABILITY SET StartTime =?, EndTime =? WHERE IdolID =? AND AvailableDay =?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, startTime);
                updateStatement.setString(2, endTime);
                updateStatement.setString(3, idolID);
                updateStatement.setString(4, availableDay);
                updateStatement.executeUpdate();

                // Send the response to the client
                writer.write("Availability already exists and has been updated.\n");
            } else {
                // The idol doesn't have an availability for the given day, so insert it
                String insertQuery = "INSERT INTO AVAILABILITY (IdolID, AvailableDay, StartTime, EndTime) VALUES (?,?,?,?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, idolID);
                insertStatement.setString(2, availableDay);
                insertStatement.setString(3, startTime);
                insertStatement.setString(4, endTime);
                insertStatement.executeUpdate();

                // Send the response to the client
                writer.write("Availability Schedule Successfully Set\n");
            }
            writer.flush();
        }

        private void viewSchedules(String[] data, BufferedWriter writer) throws SQLException, IOException {
            int choice = Integer.parseInt(data[1]);
            String searchTerm = data[2];

            String query = "";
            if (choice == 1) {
                // Query the database to get the schedules for the selected idol alias
                query = "SELECT IDOL.Alias, AVAILABILITY.AvailableDay, AVAILABILITY.StartTime, AVAILABILITY.EndTime FROM IDOL JOIN AVAILABILITY ON IDOL.IdolID = AVAILABILITY.IdolID WHERE IDOL.Alias LIKE ?";
            } else if (choice == 2) {
                // Query the database to get the schedules for the selected available day
                query = "SELECT IDOL.Alias, AVAILABILITY.AvailableDay, AVAILABILITY.StartTime, AVAILABILITY.EndTime FROM IDOL JOIN AVAILABILITY ON IDOL.IdolID = AVAILABILITY.IdolID WHERE AVAILABILITY.AvailableDay LIKE ?";
            } else {
                writer.write("INVALID_CHOICE\n");
                writer.flush();
                return;
            }

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            // Build the schedule string to send to the client
            StringBuilder scheduleString = new StringBuilder();
            boolean hasSchedules = false;
            while (resultSet.next()) {
                if (hasSchedules) {
                    scheduleString.append(",");
                } else {
                    hasSchedules = true;
                }

                String idolFullName = resultSet.getString("Alias");
                String availableDayResult = resultSet.getString("AvailableDay");
                String startTime = resultSet.getString("StartTime");
                String endTime = resultSet.getString("EndTime");
                scheduleString.append(idolFullName).append("|").append(availableDayResult).append("|").append(startTime).append("|").append(endTime);
            }

            // Send the response to the client
            if (hasSchedules) {
                writer.write("SCHEDULES_FOUND\n");
            } else {
                writer.write("NO_SCHEDULES_FOUND\n");
            }
            writer.write(scheduleString.toString() + "\n");
            writer.flush();
        }

        private void viewTotalEarnings(String[] data, BufferedWriter writer) throws IOException, SQLException {
            // Get the idolID from the request
            String idolID = data[1];

            // Retrieve the earnings data from the database
            String query = "SELECT Year, TotalInDollars, TotalInQbits FROM IDOLEARNINGS WHERE IdolID =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, idolID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare the earnings string to send to the client
            StringBuilder earningsString = new StringBuilder();
            boolean hasEarnings = false;
            while (resultSet.next()) {
                if (hasEarnings) {
                    earningsString.append(",");
                } else {
                    hasEarnings = true;
                }

                String year = resultSet.getString("Year");
                String totalInDollars = resultSet.getString("TotalInDollars");
                String totalInQbits = resultSet.getString("TotalInQbits");

                earningsString.append(year).append("|").append(totalInDollars).append("|").append(totalInQbits);
            }

            // Send the earnings data to the client
            if (hasEarnings) {
                writer.write("EARNINGS_FOUND\n");
            } else {
                writer.write("NO_EARNINGS_FOUND\n");
            }
            writer.write(earningsString.toString() + "\n");
            writer.flush();
        }

        private void viewFeedbacks(String[] data, BufferedWriter writer) throws SQLException, IOException {
            int idolID = Integer.parseInt(data[1]);

            // SQL query to retrieve feedbacks for the given IdolID
            String query = "SELECT FAN.Username, FEEDBACK.Rating, FEEDBACK.Comment " +
                    "FROM MEETUP " +
                    "INNER JOIN FEEDBACK ON MEETUP.MeetupID = FEEDBACK.MeetupID " +
                    "INNER JOIN FAN ON MEETUP.FanID = FAN.FanID " +
                    "WHERE MEETUP.IdolID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idolID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare feedbacks string to send to the client
            StringBuilder feedbacksString = new StringBuilder();
            boolean hasFeedbacks = false;
            while (resultSet.next()) {
                if (hasFeedbacks) {
                    feedbacksString.append(",");
                } else {
                    hasFeedbacks = true;
                }

                String username = resultSet.getString("Username");
                int rating = resultSet.getInt("Rating");
                String comment = resultSet.getString("Comment");

                feedbacksString.append(username).append("|").append(rating).append("|").append(comment);
            }

            // Send the response to the client
            if (hasFeedbacks) {
                writer.write("FEEDBACKS_FOUND\n");
            } else {
                writer.write("NO_FEEDBACKS_FOUND\n");
            }
            writer.write(feedbacksString.toString() + "\n");
            writer.flush();
        }

        private void editFanFullName(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new full name from data array
            int fanID = Integer.parseInt(data[1]);
            String newFullName = data[2];

            // Update the fan's full name in the database
            String query = "UPDATE FAN SET FanFullName=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newFullName);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Full Name Updated Successfully...\n");
            } else {
                writer.write("Fan Full Name Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanUsername(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new username from data array
            int fanID = Integer.parseInt(data[1]);
            String newUsername = data[2];

            // Update the fan's username in the database
            String query = "UPDATE FAN SET Username=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newUsername);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Username Updated Successfully...\n");
            } else {
                writer.write("Fan Username Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanEmail(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new email from data array
            int fanID = Integer.parseInt(data[1]);
            String newEmail = data[2];

            // Update the fan's email in the database
            String query = "UPDATE FAN SET FanEmail=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newEmail);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Email Updated Successfully...\n");
            } else {
                writer.write("Fan Email Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanPassword(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new password from data array
            int fanID = Integer.parseInt(data[1]);
            String newPassword = data[2];

            // Update the fan's password in the database
            String query = "UPDATE FAN SET FanPassword=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Password Updated Successfully...\n");
            } else {
                writer.write("Fan Password Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanGender(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new gender from data array
            int fanID = Integer.parseInt(data[1]);
            String newGender = data[2];

            // Update the fan's gender in the database
            String query = "UPDATE FAN SET Gender=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newGender);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Gender Updated Successfully...\n");
            } else {
                writer.write("Fan Gender Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanBirthdate(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new birthdate from data array
            int fanID = Integer.parseInt(data[1]);
            String newBirthdate = data[2];

            // Update the fan's birthdate in the database
            String query = "UPDATE FAN SET Birthdate=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newBirthdate);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Birthdate Updated Successfully...\n");
            } else {
                writer.write("Fan Birthdate Update Failed...\n");
            }
            writer.flush();
        }

        private void editFanBio(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract fan ID and new bio from data array
            int fanID = Integer.parseInt(data[1]);
            String newBio = data[2];

            // Update the fan's bio in the database
            String query = "UPDATE FAN SET FanBio=? WHERE FanID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newBio);
            preparedStatement.setInt(2, fanID);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Fan Bio Updated Successfully...\n");
            } else {
                writer.write("Fan Bio Update Failed...\n");
            }
            writer.flush();
        }

        private void browseIdols(String alias, BufferedWriter writer) throws SQLException, IOException {
            // SQL query to search for a specific idol by alias
            String query = "SELECT Alias, IdolType, IdolBio, QbitRatePer10Mins FROM IDOL WHERE Alias = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, alias);
            ResultSet resultSet = statement.executeQuery();

            // Prepare the search result to send to the client
            if (resultSet.next()) {
                String idolAlias = resultSet.getString("Alias");
                String idolType = resultSet.getString("IdolType");
                String idolBio = resultSet.getString("IdolBio");
                double qbitRatePer10Mins = resultSet.getDouble("QbitRatePer10Mins");

                // Send the idol information to the client
                writer.write("IDOL_FOUND\n");
                writer.write(idolAlias + "|" + idolType + "|" + idolBio + "|" + qbitRatePer10Mins + "\n");
            } else {
                // Send a message indicating idol not found
                writer.write("IDOL_NOT_FOUND\n");
            }
            writer.flush();
        }

        private void viewInteractionHistory(int fanID, BufferedWriter writer) throws SQLException, IOException {
            // SQL query to retrieve interaction history for the given fanID
            String query = "SELECT MEETUP.MeetupID, IDOL.Alias, MEETUP.Status " +
                    "FROM MEETUP " +
                    "INNER JOIN IDOL ON MEETUP.IdolID = IDOL.IdolID " +
                    "WHERE MEETUP.FanID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, fanID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare interaction history string to send to the client
            StringBuilder interactionHistoryString = new StringBuilder();
            boolean hasInteractions = false;
            while (resultSet.next()) {
                if (hasInteractions) {
                    interactionHistoryString.append(",");
                } else {
                    hasInteractions = true;
                }

                String meetupID = resultSet.getString("MeetupID");
                String alias = resultSet.getString("Alias");
                String status = resultSet.getString("Status");

                interactionHistoryString.append(meetupID).append("|").append(alias).append("|").append(status);
            }

            // Send the response to the client
            if (hasInteractions) {
                writer.write("INTERACTION_HISTORY_FOUND\n");
            } else {
                writer.write("NO_INTERACTION_HISTORY_FOUND\n");
            }
            writer.write(interactionHistoryString.toString() + "\n");
            writer.flush();
        }

        private void getMeetupDetails(int meetupID, int fanID, BufferedWriter writer) throws SQLException, IOException {
            // SQL query to check if the meetup exists for the given fanID
            String queryCheck = "SELECT MeetupID FROM MEETUP WHERE MeetupID = ? AND FanID = ?";
            PreparedStatement statementCheck = connection.prepareStatement(queryCheck);
            statementCheck.setInt(1, meetupID);
            statementCheck.setInt(2, fanID);
            ResultSet resultSetCheck = statementCheck.executeQuery();
            if (resultSetCheck.next()) {
                // SQL query to retrieve details of the specified meetup
                String query = "SELECT IDOL.Alias, MEETUP.Status " +
                        "FROM MEETUP " +
                        "INNER JOIN IDOL ON MEETUP.IdolID = IDOL.IdolID " +
                        "WHERE MEETUP.MeetupID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, meetupID);
                ResultSet resultSet = statement.executeQuery();

                // Check if the meetup exists
                if (resultSet.next()) {
                    // Extract meetup details
                    String alias = resultSet.getString("Alias");
                    String status = resultSet.getString("Status");
                    // Send the response to the client
                    writer.write("MEETUP_DETAILS_FOUND\n");
                    writer.write(alias + "|" + status + "\n");
                } else {
                    // Send the response to the client if meetup not found
                    writer.write("MEETUP_NOT_FOUND\n");
                }
            } else {
                // Send the response to the client if meetup does not exist for the user
                writer.write("MEETUP_NOT_FOUND_FOR_USER\n");
            }
            writer.flush();
        }

        private void meetNow(int meetupID, int fanID, BufferedWriter writer) throws SQLException, IOException {
            // Implement the logic to initiate the meetup
            // This could involve updating the meetup status to "Finished"
            // For simplicity, let's assume it's handled by updating the status in the database
            String updateQuery = "UPDATE MEETUP SET Status = 'Finished' WHERE MeetupID = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, meetupID);
            int rowsAffected = updateStatement.executeUpdate();

            // Send the response to the client
            if (rowsAffected > 0) {
                writer.write("MEETUP_INITIATED\n");
            } else {
                writer.write("MEETUP_INITIATION_FAILED\n");
            }
            writer.flush();
        }

        private void reportIdol(int meetupID, int fanID, String reportType, String reportDescription, BufferedWriter writer) throws SQLException, IOException {
            // Implement the logic to report the idol
            // Retrieve the MeetupID from the database using a join
            String insertQuery = "INSERT INTO FANREPORT (FanID, IdolID, FanReportType, FanReportDescription) " +
                    "SELECT M.FanID, M.IdolID, ?, ? " +
                    "FROM MEETUP M " +
                    "WHERE M.MeetupID = ?";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, reportType);
            insertStatement.setString(2, reportDescription);
            insertStatement.setInt(3, meetupID);
            int rowsAffected = insertStatement.executeUpdate();

            // Send the response to the client
            if (rowsAffected > 0) {
                writer.write("IDOL_REPORTED\n");
            } else {
                writer.write("IDOL_REPORT_FAILED\n");
            }
            writer.flush();
        }



    }
}