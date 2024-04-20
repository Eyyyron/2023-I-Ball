package Server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Server {
    private static final int PORT = 12345;
    private static final String URL = "jdbc:mysql://localhost:3306/eyeball";
    private static final String USER = "root";
    private static final String PASSWORD = null;

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
                        registerFan(requestData, writer);
                    } else if (requestType.equals("REGISTER_IDOL")) {
                        registerIdol(requestData, writer);
                    } else if (requestType.equals("LOGIN")) {
                        login(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_NAME")) {
                        editIdolFullName(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_ALIAS")) {
                        editIdolAlias(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_EMAIL")) {
                        editIdolEmail(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_PASSWORD")) {
                        editIdolPassword(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_TYPE")) {
                        editIdolType(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_BIO")) {
                        editIdolBio(requestData, writer);
                    } else if (requestType.equals("EDIT_IDOL_QBIT_RATE")) {
                        editIdolQBitRate(requestData, writer);
                    } else if (requestType.equals("SET_AVAILABILITY")) {
                        setAvailability(requestData, writer);
                    } else if (requestType.equals("VIEW_SCHEDULES")) {
                        viewSchedules(requestData, writer);
                    } else if (requestType.equals("VIEW_TOTAL_EARNINGS")) {
                        viewTotalEarnings(requestData, writer);
                    } else if (requestType.equals("VIEW_FEEDBACKS")) {
                        handleViewFeedbacksRequest(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_FULLNAME")) {
                        editFanFullName(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_USERNAME")) {
                        editFanUsername(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_EMAIL")) {
                        editFanEmail(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_PASSWORD")) {
                        editFanPassword(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_GENDER")) {
                        editFanGender(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_BIRTHDATE")) {
                        editFanBirthdate(requestData, writer);
                    } else if (requestType.equals("EDIT_FAN_BIO")) {
                        editFanBio(requestData, writer);
                    } else if (requestType.equals("BROWSE_IDOL")) {
                        String alias = requestData[1];
                        browseIdols(alias, writer);
                    } else if (requestType.equals("VIEW_INTERACTION_HISTORY")) {
                        fanViewInteractionHistory(requestData, writer);
                    } else if (requestType.equals("VIEW_INTERACTION_HISTORY_1")) {
                        idolViewInteractionHistory(requestData, writer);
                    } else if (requestType.equals("RESERVE_MEETUP")) {
                        reserveMeetup(requestData, writer);
                    } else if (requestType.equals("MAKE_PAYMENT")) {
                        String paymentMethod = requestData[1];
                        makePayment(paymentMethod, writer, reader);
                    } else if (requestType.equals("ACCESS_MEETUP")) {
                        handleFanMeetupRequest(requestData, writer);
                    } else if (requestType.equals("ACCESS_MEETUP_1")) {
                        handleIdolMeetupRequest(requestData, writer);
                    } else if (requestType.equals("WRITE_FEEDBACK")){
                        handleFeedbackRequest(requestData, writer);
                    } else if (requestType.equals("REPORT_IDOL")) {
                        handleReportIdolRequest(requestData, writer);
                    } else if (requestType.equals("REPORT_FAN")) {
                        handleReportFanRequest(requestData, writer);
                    } else {
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

        private void handleViewFeedbacksRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String idolID = data[1];

            // Query the database to get the feedbacks related to the logged-in idol along with the meetup IDs and ratings
            String query = "SELECT F.FeedbackID, F.MeetupID, F.Rating FROM FEEDBACK F JOIN MEETUP M ON F.MeetupID = M.MeetupID WHERE M.IdolID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, idolID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare the feedbacks string to send to the client
            StringBuilder feedbacksString = new StringBuilder();
            boolean hasFeedbacks = false;
            while (resultSet.next()) {
                if (hasFeedbacks) {
                    feedbacksString.append(",");
                } else {
                    hasFeedbacks = true;
                }

                String feedbackID = resultSet.getString("FeedbackID");
                String meetupID = resultSet.getString("MeetupID");
                String rating = resultSet.getString("Rating");

                feedbacksString.append(feedbackID).append("|").append(meetupID).append("|").append(rating);
            }

            // Send the feedbacks data to the client
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

        private void fanViewInteractionHistory(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String fanID = data[1];

            // Query the database to get the interaction history for the logged-in fan
            String query = "SELECT MEETUP.MeetupID, MEETUP.DurationInMinutes, MEETUP.ScheduledDate, MEETUP.ScheduledTime, IDOL.Alias, FAN.FanFullName " +
                    "FROM MEETUP " +
                    "INNER JOIN FAN ON MEETUP.FanID = FAN.FanID " +
                    "INNER JOIN IDOL ON MEETUP.IdolID = IDOL.IdolID " +
                    "WHERE MEETUP.FanID =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, fanID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare the interaction history string to send to the client
            StringBuilder interactionHistoryString = new StringBuilder();
            boolean hasInteractions = false;
            while (resultSet.next()) {
                if (hasInteractions) {
                    interactionHistoryString.append(",");
                } else {
                    hasInteractions = true;
                }

                String meetupID = resultSet.getString("MeetupID");
                int durationInMinutes = resultSet.getInt("DurationInMinutes");
                String scheduledDate = resultSet.getString("ScheduledDate");
                String scheduledTime = resultSet.getString("ScheduledTime");
                String idolAlias = resultSet.getString("Alias");
                String fanFullName = resultSet.getString("FanFullName");

                interactionHistoryString.append(meetupID).append("|").append(durationInMinutes).append("|").append(scheduledDate).append("|").append(scheduledTime).append("|").append(idolAlias).append("|").append(fanFullName);
            }

            // Send the interaction history data to the client
            if (hasInteractions) {
                writer.write("INTERACTION_HISTORY_FOUND\n");
            } else {
                writer.write("NO_INTERACTION_HISTORY_FOUND\n");
            }
            writer.write(interactionHistoryString.toString() + "\n");
            writer.flush();
        }

        private void idolViewInteractionHistory(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String idolID = data[1];

            // Query the database to get the interaction history for the logged-in idol
            String query = "SELECT MEETUP.MeetupID, MEETUP.DurationInMinutes, MEETUP.ScheduledDate, MEETUP.ScheduledTime, IDOL.Alias, FAN.FanFullName " +
                    "FROM MEETUP " +
                    "INNER JOIN FAN ON MEETUP.FanID = FAN.FanID " +
                    "INNER JOIN IDOL ON MEETUP.IdolID = IDOL.IdolID " +
                    "WHERE MEETUP.IdolID =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, idolID);
            ResultSet resultSet = statement.executeQuery();

            // Prepare the interaction history string to send to the client
            StringBuilder interactionHistoryString = new StringBuilder();
            boolean hasInteractions = false;
            while (resultSet.next()) {
                if (hasInteractions) {
                    interactionHistoryString.append(",");
                } else {
                    hasInteractions = true;
                }

                String meetupID = resultSet.getString("MeetupID");
                int durationInMinutes = resultSet.getInt("DurationInMinutes");
                String scheduledDate = resultSet.getString("ScheduledDate");
                String scheduledTime = resultSet.getString("ScheduledTime");
                String idolAlias = resultSet.getString("Alias");
                String fanFullName = resultSet.getString("FanFullName");

                interactionHistoryString.append(meetupID).append("|").append(durationInMinutes).append("|").append(scheduledDate).append("|").append(scheduledTime).append("|").append(idolAlias).append("|").append(fanFullName);
            }

            // Send the interaction history data to the client
            if (hasInteractions) {
                writer.write("INTERACTION_HISTORY_FOUND_1\n");
            } else {
                writer.write("NO_INTERACTION_HISTORY_FOUND_1\n");
            }
            writer.write(interactionHistoryString.toString() + "\n");
            writer.flush();
        }

        private void reserveMeetup(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String fanID = data[1];
            String idolAlias = data[2];
            int durationInMinutes = Integer.parseInt(data[3]);
            String scheduledDate = data[4];
            String scheduledTime = data[5];

            // Check if the idol exists
            String checkIdolQuery = "SELECT IdolID FROM IDOL WHERE Alias =?";
            PreparedStatement checkIdolStatement = connection.prepareStatement(checkIdolQuery);
            checkIdolStatement.setString(1, idolAlias);
            ResultSet checkIdolResult = checkIdolStatement.executeQuery();

            if (checkIdolResult.next()) {
                int idolID = checkIdolResult.getInt("IdolID");

                // Check if the fan and idol have a meetup scheduled on the same date and time
                String checkMeetupQuery = "SELECT * FROM MEETUP WHERE FanID =? AND IdolID =? AND ScheduledDate =? AND ScheduledTime =?";
                PreparedStatement checkMeetupStatement = connection.prepareStatement(checkMeetupQuery);
                checkMeetupStatement.setInt(1, Integer.parseInt(fanID));
                checkMeetupStatement.setInt(2, idolID);
                checkMeetupStatement.setString(3, scheduledDate);
                checkMeetupStatement.setString(4, scheduledTime);
                ResultSet checkMeetupResult = checkMeetupStatement.executeQuery();

                if (!checkMeetupResult.next()) {
                    // Check if the scheduled time is within the idol's availability
                    String checkAvailabilityQuery = "SELECT * FROM AVAILABILITY WHERE IdolID =? AND AvailableDay =? AND StartTime <=? AND EndTime >?";
                    PreparedStatement checkAvailabilityStatement = connection.prepareStatement(checkAvailabilityQuery);
                    checkAvailabilityStatement.setInt(1, idolID);
                    checkAvailabilityStatement.setString(2, getDayOfWeek(scheduledDate));
                    checkAvailabilityStatement.setTime(3, Time.valueOf(scheduledTime));
                    checkAvailabilityStatement.setTime(4, Time.valueOf(LocalTime.parse(scheduledTime).plusMinutes(durationInMinutes)));
                    ResultSet checkAvailabilityResult = checkAvailabilityStatement.executeQuery();

                    if (checkAvailabilityResult.next()) {
                        // Insert the new meetup into the MEETUP table
                        String insertMeetupQuery = "INSERT INTO MEETUP (FanID, IdolID, DurationInMinutes, ScheduledDate, ScheduledTime, Status) VALUES (?,?,?,?,?, 'To Pay')";
                        PreparedStatement insertMeetupStatement = connection.prepareStatement(insertMeetupQuery);
                        insertMeetupStatement.setInt(1, Integer.parseInt(fanID));
                        insertMeetupStatement.setInt(2, idolID);
                        insertMeetupStatement.setInt(3, durationInMinutes);
                        insertMeetupStatement.setString(4, scheduledDate);
                        insertMeetupStatement.setString(5, scheduledTime);
                        int rowsAffected = insertMeetupStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            writer.write("MEETUP_RESERVED\n");
                        } else {
                            writer.write("ERROR_RESERVING_MEETUP\n");
                        }
                    } else {
                        writer.write("MEETUP_OUTSIDE_IDOL_AVAILABILITY\n");
                    }
                } else {
                    writer.write("MEETUP_ALREADY_SCHEDULED\n");
                }
            } else {
                writer.write("IDOL_NOT_FOUND\n");
            }
            writer.flush();
        }

        private String getDayOfWeek(String date) {
            LocalDate localDate = LocalDate.parse(date);
            return localDate.getDayOfWeek().toString();
        }

        private void makePayment(String paymentMethod, BufferedWriter writer, BufferedReader reader) throws IOException, SQLException {
            // Update the status of the meetup to "Pending" in the database
            String query = "UPDATE MEETUP SET Status = 'Pending' WHERE Status = 'To Pay'";
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0) {
                // Get the meetup ID and IdolID of the meetup that was just updated to "Pending"
                query = "SELECT MeetupID, IdolID FROM MEETUP WHERE Status = 'Pending' ORDER BY MeetupID DESC LIMIT 1";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                int meetupID = 0;
                int idolID = 0;
                if (resultSet.next()) {
                    meetupID = resultSet.getInt("MeetupID");
                    idolID = resultSet.getInt("IdolID");
                }

                // Get the duration of the meetup
                query = "SELECT DurationInMinutes FROM MEETUP WHERE MeetupID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, meetupID);
                resultSet = preparedStatement.executeQuery();
                int durationInMinutes = 0;
                if (resultSet.next()) {
                    durationInMinutes = resultSet.getInt("DurationInMinutes");
                }

                // Get the Qbit rate per 10 minutes of the idol in the meetup
                query = "SELECT QbitRatePer10Mins FROM IDOL WHERE IdolID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, idolID);
                resultSet = preparedStatement.executeQuery();
                double qbitRatePer10Mins = 0;
                if (resultSet.next()) {
                    qbitRatePer10Mins = resultSet.getDouble("QbitRatePer10Mins");
                }

                // Calculate the amount in Qbits and dollars
                double amountInQbits = (durationInMinutes / 10.0) * qbitRatePer10Mins;
                double amountInDollars = amountInQbits / 80.0;

                // Insert a new row into the PAYMENT table
                query = "INSERT INTO PAYMENT (MeetupID, AmountInDollars, AmountInQbits, PaymentDate, PaymentTime, PaymentMode) VALUES (?, ?, ?, CURDATE(), CURTIME(), ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, meetupID);
                preparedStatement.setDouble(2, amountInDollars);
                preparedStatement.setDouble(3, amountInQbits);
                preparedStatement.setString(4, paymentMethod);
                rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Update TotalInQbits and TotalInDollars in IDOLEARNINGS table
                    query = "UPDATE IDOLEARNINGS SET TotalInQbits = TotalInQbits + ?, TotalInDollars = TotalInDollars + ? WHERE IdolID = ? AND Year = YEAR(CURDATE())";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setDouble(1, amountInQbits);
                    preparedStatement.setDouble(2, amountInDollars);
                    preparedStatement.setInt(3, idolID);
                    preparedStatement.executeUpdate();

                    // Send a response to the client indicating that the payment was successful
                    writer.write("PAYMENT_SUCCESS\n");
                    writer.flush();
                } else {
                    // Send a response to the client indicating that the payment failed
                    writer.write("PAYMENT_FAILED\n");
                    writer.flush();
                }
            } else {
                // Send a response to the client indicating that the payment failed
                writer.write("PAYMENT_FAILED\n");
                writer.flush();
            }
        }

        public void handleFanMeetupRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String meetupID = data[1];
            String status = ""; // Initialize status variable

            // Retrieve meetup details from the database with a join to fetch IdolAlias
            String getMeetupQuery = "SELECT M.Status, I.Alias AS IdolAlias, M.DurationInMinutes, M.ScheduledDate, M.ScheduledTime " +
                    "FROM MEETUP M " +
                    "JOIN IDOL I ON M.IdolID = I.IdolID " +
                    "WHERE M.MeetupID =?";
            PreparedStatement getMeetupStatement = connection.prepareStatement(getMeetupQuery);
            getMeetupStatement.setString(1, meetupID);
            ResultSet meetupResult = getMeetupStatement.executeQuery();

            if (meetupResult.next()) {
                status = meetupResult.getString("Status"); // Assign value to status variable
                String idolAlias = meetupResult.getString("IdolAlias");
                int duration = meetupResult.getInt("DurationInMinutes");
                String scheduledDate = meetupResult.getString("ScheduledDate");
                String scheduledTime = meetupResult.getString("ScheduledTime");

                switch (status) {
                    case "Pending":
                        writer.write("MEETUP_PENDING," + idolAlias + "," + duration + "," + scheduledDate + "," + scheduledTime + "\n");
                        break;
                    case "Finished":
                        writer.write("MEETUP_FINISHED\n");
                        break;
                    case "To Pay":
                        writer.write("MEETUP_TO_PAY\n");
                        break;
                    default:
                        writer.write("INVALID_MEETUP_STATUS\n");
                        break;
                }
            } else {
                writer.write("MEETUP_NOT_FOUND\n");
            }
            writer.flush();
        }

        public void handleIdolMeetupRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String meetupID = data[1];
            String status = ""; // Initialize status variable

            // Retrieve meetup details from the database with a join to fetch FanUsername
            String getMeetupQuery = "SELECT M.Status, F.Username AS FanUsername, M.DurationInMinutes, M.ScheduledDate, M.ScheduledTime " +
                    "FROM MEETUP M " +
                    "JOIN FAN F ON M.FanID = F.FanID " +
                    "WHERE M.MeetupID =?";
            PreparedStatement getMeetupStatement = connection.prepareStatement(getMeetupQuery);
            getMeetupStatement.setString(1, meetupID);
            ResultSet meetupResult = getMeetupStatement.executeQuery();

            if (meetupResult.next()) {
                status = meetupResult.getString("Status"); // Assign value to status variable
                String fanUsername = meetupResult.getString("FanUsername");
                int duration = meetupResult.getInt("DurationInMinutes");
                String scheduledDate = meetupResult.getString("ScheduledDate");
                String scheduledTime = meetupResult.getString("ScheduledTime");

                switch (status) {
                    case "Pending":
                        writer.write("MEETUP_PENDING," + fanUsername + "," + duration + "," + scheduledDate + "," + scheduledTime + "\n");
                        break;
                    case "Finished":
                        writer.write("MEETUP_FINISHED\n");
                        break;
                    case "To Pay":
                        writer.write("MEETUP_TO_PAY\n");
                        break;
                    default:
                        writer.write("INVALID_MEETUP_STATUS\n");
                        break;
                }
            } else {
                writer.write("MEETUP_NOT_FOUND\n");
            }
            writer.flush();
        }

        public void handleFeedbackRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String meetupID = data[1];
            String rating = data[2];
            String comment = data[3];

            // Insert feedback into the database
            String insertFeedbackQuery = "INSERT INTO FEEDBACK (MeetupID, Rating, Comment) VALUES (?, ?, ?)";
            PreparedStatement insertFeedbackStatement = connection.prepareStatement(insertFeedbackQuery);
            insertFeedbackStatement.setString(1, meetupID);
            insertFeedbackStatement.setString(2, rating);
            insertFeedbackStatement.setString(3, comment);
            int rowsAffected = insertFeedbackStatement.executeUpdate();

            if (rowsAffected > 0) {
                writer.write("\nFEEDBACK_SUBMITTED\n");
            } else {
                writer.write("\nERROR_SUBMITTING_FEEDBACK\n");
            }
            writer.flush();
        }

        public void handleReportFanRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String meetupID = data[1];
            String reportType = data[2];
            String reportDescription = data[3];

            // Get FanID and IdolID related to the meetup
            String getFanIdQuery = "SELECT FanID, IdolID FROM MEETUP WHERE MeetupID = ?";
            PreparedStatement getFanIdStatement = connection.prepareStatement(getFanIdQuery);
            getFanIdStatement.setString(1, meetupID);
            ResultSet fanIdResult = getFanIdStatement.executeQuery();

            if (fanIdResult.next()) {
                String fanID = fanIdResult.getString("FanID");
                String idolID = fanIdResult.getString("IdolID");

                // Insert report into the database
                String insertReportQuery = "INSERT INTO IDOLREPORT (IdolID, FanID, IdolReportType, IdolReportDescription) VALUES (?, ?, ?, ?)";
                PreparedStatement insertReportStatement = connection.prepareStatement(insertReportQuery);
                insertReportStatement.setString(1, idolID);
                insertReportStatement.setString(2, fanID);
                insertReportStatement.setString(3, reportType);
                insertReportStatement.setString(4, reportDescription);
                int rowsAffected = insertReportStatement.executeUpdate();

                if (rowsAffected > 0) {
                    writer.write("\nREPORT_SUBMITTED\n");
                } else {
                    writer.write("\nERROR_SUBMITTING_REPORT\n");
                }
            } else {
                writer.write("\nMEETUP_NOT_FOUND\n");
            }
            writer.flush();
        }

        public void handleReportIdolRequest(String[] data, BufferedWriter writer) throws SQLException, IOException {
            String meetupID = data[1];
            String reportType = data[2];
            String reportDescription = data[3];

            // Get FanID and IdolID related to the meetup
            String getFanIdolQuery = "SELECT FanID, IdolID FROM MEETUP WHERE MeetupID = ?";
            PreparedStatement getFanIdolStatement = connection.prepareStatement(getFanIdolQuery);
            getFanIdolStatement.setString(1, meetupID);
            ResultSet fanIdolResult = getFanIdolStatement.executeQuery();

            if (fanIdolResult.next()) {
                String fanID = fanIdolResult.getString("FanID");
                String idolID = fanIdolResult.getString("IdolID");

                // Insert report into the database
                String insertReportQuery = "INSERT INTO FANREPORT (FanID, IdolID, FanReportType, FanReportDescription) VALUES (?, ?, ?, ?)";
                PreparedStatement insertReportStatement = connection.prepareStatement(insertReportQuery);
                insertReportStatement.setString(1, fanID);
                insertReportStatement.setString(2, idolID);
                insertReportStatement.setString(3, reportType);
                insertReportStatement.setString(4, reportDescription);
                int rowsAffected = insertReportStatement.executeUpdate();

                if (rowsAffected > 0) {
                    writer.write("\nREPORT_SUBMITTED\n");
                } else {
                    writer.write("\nERROR_SUBMITTING_REPORT\n");
                }
            } else {
                writer.write("\nMEETUP_NOT_FOUND\n");
            }
            writer.flush();
        }
    }
}