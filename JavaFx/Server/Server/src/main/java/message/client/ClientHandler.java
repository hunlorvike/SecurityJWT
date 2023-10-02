package message.client;

import message.dao.message.MessageDaoImpl;
import message.model.MessageModel;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private MessageDaoImpl messageDao;

    public ClientHandler(Socket clientSocket, MessageDaoImpl messageDao) {
        this.clientSocket = clientSocket;
        this.messageDao = messageDao;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Đọc yêu cầu từ client (ví dụ: tin nhắn)
            String clientRequest = in.readLine();

            // Xử lý yêu cầu (ví dụ: gửi tin nhắn)
            if (clientRequest.startsWith("SEND_MESSAGE:")) {
                // Phân tích yêu cầu để lấy thông tin tin nhắn
                String[] parts = clientRequest.split(":");
                int conversationId = Integer.parseInt(parts[1]);
                int senderId = Integer.parseInt(parts[2]);
                String messageText = parts[3];

                // Tạo đối tượng Message
                MessageModel message = new MessageModel(conversationId, senderId, messageText);

                // Thêm tin nhắn vào cơ sở dữ liệu
                messageDao.sendMessage(message);

                // Gửi phản hồi cho client (nếu cần)
                out.println("MESSAGE_SENT");
            }

            // Đóng kết nối
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

