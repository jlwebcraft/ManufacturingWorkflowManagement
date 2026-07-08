import java.sql.*;

public class DatabaseConnection
{

    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/manufacturing_workflow_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection connectServer() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD);
    }

    public static Connection connectDatabase() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }
}