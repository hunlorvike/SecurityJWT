package hung.learn.firstapp.api.dao;

import hung.learn.firstapp.api.model.UserModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class IUserDao {
    private Connection connection;

    public IUserDao(Connection connection) {
        this.connection = connection;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                Timestamp created_at = resultSet.getTimestamp("created_at");
                Timestamp updated_at = resultSet.getTimestamp("updated_at");
                String avatar_path = resultSet.getString("avatar_path"); // Lấy trường avatar_path
                int followers_count = resultSet.getInt("followers_count"); // Lấy trường followers_count
                UserModel userModel = new UserModel(id, fullname, email, password, role, avatar_path, followers_count, created_at, updated_at);
                users.add(userModel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
