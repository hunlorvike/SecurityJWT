package message.dao.message;

import message.model.MessageModel;

public interface IMessageDao {
    void sendMessage(MessageModel messageModel);    // Gửi một tin nhắn mới trong một cuộc trò chuyện

}
