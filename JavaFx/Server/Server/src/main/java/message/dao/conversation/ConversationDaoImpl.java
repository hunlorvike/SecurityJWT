package message.dao.conversation;
import message.model.ConversationModel;
import java.sql.*;

public class ConversationDaoImpl implements IConversationDao {
    private Connection connection;

    public ConversationDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createConversation(ConversationModel conversationModel) {
        String query = "INSERT INTO conversations (name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, conversationModel.getConversationName());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
           e.printStackTrace();
        }
    }

    @Override
    public ConversationModel getConversationById(int conversationId) {
        String query = "SELECT * FROM conversations WHERE conversation_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, conversationId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer id = resultSet.getInt("conversation_id");
                    String name = resultSet.getString("name");
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    ConversationModel conversation = new ConversationModel(id, name, createdAt);
                    return conversation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public boolean doesConversationExistBetweenUsers(int userAId, int userBId) {
        String query = "SELECT COUNT(*) " +
                "FROM participants participants1 " +
                "INNER JOIN participants participants2 " +
                "ON participants1.conversation_id = participants2.conversation_id " +
                "WHERE participants1.user_id = ? " +
                "AND participants2.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userAId);
            preparedStatement.setInt(2, userBId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


}
