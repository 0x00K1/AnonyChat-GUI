/**
 * @For_'App/LDBA/AnonyChat.exe'_users
 *
 * This Java program establishes a connection to a MySQL database and creates the necessary database schema for the AnonyChat application.
 * It is designed for initial setup and should be run only once to create three essential tables: ROOT, CHATROOMS, and USERS.
 *
 * Usage Instructions:
 * 1. Ensure you have a MySQL server running and accessible.
 * 2. Update the DATABASE_URL, DATABASE_USER, and DATABASE_PASS constants in this file with your actual database URL, username, and password.
 * 3. Run this program once to establish your database schema. It is intended only for initial setup; running it multiple times may lead to errors unless existing tables are handled or dropped.
 * 4. This setup is crucial for anyone aiming to establish a new database environment for the AnonyChat application.
 * 5. After successfully creating the tables, you must update your 'config.properties' file with the correct database credentials for the AnonyChat application to function correctly.
 *
 * Note:
 * - If you encounter SQL exceptions while running this program, please verify your connection details and ensure your MySQL user has the appropriate permissions.
 * - Modifications to the database schema should be reflected in the application’s database interaction logic to prevent runtime errors.
 *
 * IMPORTANT:
 * - After the tables are created, update your 'config.properties' file with the database connection details. This file should be located in the resources directory of your project.
 * - Ensure the following details are correct in the 'config.properties':
 *   - database.url=jdbc:mysql://<your-db-url> (e.g., jdbc:mysql://localhost:3306/anonychat)
 *   - database.user=<your-db-user> (e.g., root)
 *   - database.password=<your-db-password> (e.g., yoursecurepassword)
 * - This step is critical for the application's connectivity and functionality.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    // MySQL
    private static final String DATABASE_URL = ""; // Example: "jdbc:mysql://localhost:3306/myDatabase?useSSL=false"
    private static final String DATABASE_USER = ""; // Example: "root"
    private static final String DATABASE_PASS = ""; // Example: "password"

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

            // Instructions for updating config.properties
            System.out.println("IMPORTANT: Please update your 'config.properties' file with the correct database credentials:");
            System.out.println("   database.url=jdbc:mysql://<your-db-url>");
            System.out.println("   database.user=<your-db-user>");
            System.out.println("   database.password=<your-db-password>");
            System.out.println("This step is crucial for the proper functioning of your application. The file should be located in your project's repo directory.");
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
