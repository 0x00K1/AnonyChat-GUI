import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String DATABASE_URL = ""; // Use SSL
    private static final String DATABASE_USER = "";
    private static final String DATABASE_PASS = "";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);

            // Create a statement
            stmt = conn.createStatement();

            // SQL statement for creating the ROOT table
            String sqlRoot = "CREATE TABLE ROOT ( " +
                             "RootName VARCHAR(20) PRIMARY KEY," +
                             "NumOfRooms INT NOT NULL CHECK (NumOfRooms < 4)," +
                             "PASSWORDHASH VARCHAR(255) NOT NULL," +
                             "SALT VARCHAR(255) NOT NULL);";

            // Execute the statement
            stmt.execute(sqlRoot);

            // SQL statement for creating the CHATROOMS table
            String sqlChatrooms = "CREATE TABLE CHATROOMS ( " +
                                  "CRoomIP VARCHAR(32), " +
                                  "CRoomPORT INT, " +
                                  "RootName VARCHAR(20), " +
                                  "CRoomName VARCHAR(20) NOT NULL, " +
                                  "Capacity INT NOT NULL, " +
                                  "isPrivate BOOLEAN NOT NULL, " +
                                  "OnlineUsers INT NOT NULL, " +
                                  "PASSWORDHASH VARCHAR(255), " +
                                  "SALT VARCHAR(255), " +
                                  "isRunning BOOLEAN DEFAULT FALSE, " +
                                  "PRIMARY KEY (CRoomIP, CRoomPORT, RootName), " +
                                  "FOREIGN KEY (RootName) REFERENCES ROOT(RootName));";

            // Execute the statement
            stmt.execute(sqlChatrooms);

            // SQL statement for creating the USERS table
            String sqlUsers = "CREATE TABLE USERS ( " +
                              "SessionID CHAR(36), " +
                              "CRoomIP VARCHAR(32), " +
                              "CRoomPORT INT, " +
                              "RootName VARCHAR(20), " +
                              "UserIP VARCHAR(32), " +
                              "UserName VARCHAR(20), " +
                              "isRoot BOOLEAN NOT NULL, " +
                              "isAnonymous BOOLEAN NOT NULL, " +
                              "isOnline BOOLEAN NOT NULL, " +
                              "PRIMARY KEY (SessionID), " +
                              "FOREIGN KEY (CRoomIP, CRoomPORT, RootName) REFERENCES CHATROOMS(CRoomIP, CRoomPORT, RootName));";

            // Execute the statement
            stmt.execute(sqlUsers);

            System.out.println("Tables created successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
