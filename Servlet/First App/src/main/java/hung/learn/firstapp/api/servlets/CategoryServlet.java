package hung.learn.firstapp.api.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hung.learn.firstapp.api.config.ConnectionProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "categoryServlet", urlPatterns = {"/api/category/getAllCategorys"})

public class CategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy kết nối từ ConnectionProvider
        try (Connection connection = new ConnectionProvider().getConnection()) {
            // Kiểm tra xem kết nối có tồn tại hay không
            if (connection != null) {
                // Xây dựng câu lệnh SQL
                String sql = "SELECT * FROM categories";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        // Khởi tạo mảng JSON để lưu trữ dữ liệu
                        JsonArray jsonArray = new JsonArray();

                        while (resultSet.next()) {
                            // Lấy các giá trị từ kết quả truy vấn
                            int categoryId = resultSet.getInt("category_id");
                            String categoryName = resultSet.getString("name");
                            int creatorId = resultSet.getInt("creator_id");
                            Timestamp createdAt = resultSet.getTimestamp("created_at");
                            Timestamp updatedAt = resultSet.getTimestamp("updated_at");

                            // Tạo một đối tượng JSON cho dòng dữ liệu hiện tại
                            JsonObject categoryObject = new JsonObject();
                            categoryObject.addProperty("category_id", categoryId);
                            categoryObject.addProperty("category_name", categoryName);
                            categoryObject.addProperty("creator_id", creatorId);
                            categoryObject.addProperty("created_at", createdAt.toString());
                            categoryObject.addProperty("updated_at", updatedAt.toString());

                            // Thêm đối tượng JSON của dòng dữ liệu vào mảng JSON
                            jsonArray.add(categoryObject);
                        }
                        // Đặt kiểu nội dung là JSON trong phản hồi
                        resp.setContentType("application/json");
                        // Ghi mảng JSON vào phản hồi
                        resp.getWriter().write(jsonArray.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // Xử lý trường hợp kết nối không thành công
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Kết nối đến CSDL thất bại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
