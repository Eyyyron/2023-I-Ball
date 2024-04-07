package Server;

import java.io.*;
import java.net.*;
import java.sql.*;

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
                    } else if (requestType.equals("CONNECT_TO_ROOM")) {
                        // Handle room connection request
                        connectToRoom(requestData, writer);
                    } else if (requestType.equals("SET_AVAILABILITY")) {
                        // Handle setting availability of idol
                    } else if (requestType.equals("CONDUCT_VIRTUAL_MEETUP")) {
                        // Handle conducting virtual meet-up
                        conductVirtualMeetup(requestData, writer);
                        setAvailability(requestData, writer);
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

        private void setAvailability(String[] data, BufferedWriter writer) throws SQLException, IOException {
            // Extract availability details from data array
            String idolID = data[1];
            String availableDay = data[2];
            String startTime = data[3];
            String endTime = data[4];

            // Perform scheduling of availability
            String query = "INSERT INTO AVAILABILITY (IdolID, AvailableDay, StartTime, EndTime) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, idolID);
            preparedStatement.setString(2, availableDay);
            preparedStatement.setString(3, startTime);
            preparedStatement.setString(4, endTime);
            int rowsAffected = preparedStatement.executeUpdate();

            // Send response to client
            if (rowsAffected > 0) {
                writer.write("Availability Schedule Successfully Set\n");
            } else {
                writer.write("Setting Availability Schedule Failed\n");
            }
            writer.flush();
        }
        private void connectToRoom(String[] data, BufferedWriter writer)throws SQLException, IOException{
            String meetupId = data[1];
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM meetup WHERE meetupID = ?");
            statement.setString(1, meetupId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                //send the content of the row corresponding the meetup ID back to the Client
                int meetupID = resultSet.getInt("MeetupID");
                int fanID = resultSet.getInt("FanID");
                int idolID = resultSet.getInt("IdolID");
                int duration = resultSet.getInt("DurationInMinutes");
                String scheduledDate = resultSet.getString("ScheduledDate");
                String scheduledTime = resultSet.getString("ScheduledTime");
                String status = resultSet.getString("Status");

                String meetUpInfo = String.format("MeetUpID: &d, FanID: &d, IdolID: &d, Duration: &d, minutes: &d, ScheduledDate: &s, scheduledTime: &s, Status: &s",
                        meetupID, fanID, idolID, duration, scheduledDate, scheduledTime, status);

                writer.write(meetUpInfo +"\n");
            }else{
                writer.write("Invalid meetup ID\n");
            }
            writer.flush();
        }
        private void conductVirtualMeetup(String[] data, BufferedWriter writer) throws SQLException, IOException {
          String meetupId = data[1];
            writer.write("Virtual meetup initiated for meetup ID: " + meetupId + "\n");
            writer.flush();
        }

    }
}
