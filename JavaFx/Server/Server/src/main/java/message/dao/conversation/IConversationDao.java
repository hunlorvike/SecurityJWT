package message.dao.conversation;

import message.model.ConversationModel;


import java.sql.SQLException;

public interface IConversationDao {
    void createConversation(ConversationModel conversationModel); // Tạo một cuộc trò chuyện mới:
    ConversationModel getConversationById(int conversationId) throws SQLException; //Lấy thông tin cuộc trò chuyện dựa trên conversation_id:
    boolean doesConversationExistBetweenUsers(int userAId, int userBId); // Kiểm tra xem một cuộc trò chuyện đã tồn tại giữa hai người dùng hay chưa:
}
