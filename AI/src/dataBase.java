import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class dataBase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/my_database?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "Alice");
            preparedStatement.setString(2, "alice@example.com");
            preparedStatement.setInt(3, 25);

            int rows = preparedStatement.executeUpdate();
            System.out.println("Inserted " + rows + " row(s) into the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
