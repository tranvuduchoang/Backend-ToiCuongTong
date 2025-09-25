import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDbConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/toicuongtong";
        String username = "postgres";
        String password = "0934534079";
        
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Schema: " + connection.getSchema());
            connection.close();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed:");
            System.out.println("Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
        }
    }
}