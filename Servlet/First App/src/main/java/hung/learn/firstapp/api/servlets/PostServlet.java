import java.io.IOException;
import java.sql.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import hung.learn.firstapp.api.config.ConnectionProvider;

@WebServlet(name = "postServlet", urlPatterns = {"/api/post/getAllPosts", "/api/post/getPostsByCategory"})
public class PostServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        switch (url) {
            case "/api/post/getAllPosts":
                getAllPosts(resp);
                break;
            case "/api/post/getPostsByCategory":
                getPostsByCategory(req, resp);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("URL không hợp lệ.");
                break;
        }
    }

    private void getAllPosts(HttpServletResponse resp) throws IOException {
        try (Connection connection = new ConnectionProvider().getConnection()) {
            if (connection != null) {
                String sql = "SELECT\n" +
                        "    p.post_id,\n" +
                        "    p.title,\n" +
                        "    p.content,\n" +
                        "    p.status,\n" +
                        "    p.view_count,\n" +
                        "    p.creator_id,\n" +
                        "    p.scheduled_datetime,\n" +
                        "    p.category,\n" +
                        "    u.fullname AS creator_name,\n" +
                        "    p.created_at,\n" +
                        "    p.updated_at\n" +
                        "FROM\n" +
                        "    post p\n" +
                        "LEFT JOIN\n" +
                        "    users u ON p.creator_id = u.user_id\n";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        JsonObject jsonResponse = new JsonObject();
                        JsonArray postsArray = new JsonArray();

                        while (resultSet.next()) {
                            int postId = resultSet.getInt("post_id");
                            String title = resultSet.getString("title");
                            String content = resultSet.getString("content");
                            String status = resultSet.getString("status");
                            int viewCount = resultSet.getInt("view_count");
                            int creatorId = resultSet.getInt("creator_id");
                            Timestamp scheduledDatetime = resultSet.getTimestamp("scheduled_datetime");
                            String category = resultSet.getString("category");
                            String creatorName = resultSet.getString("creator_name");
                            Timestamp createdAt = resultSet.getTimestamp("created_at");
                            Timestamp updatedAt = resultSet.getTimestamp("updated_at");

                            JsonObject postObject = new JsonObject();
                            postObject.addProperty("post_id", postId);
                            postObject.addProperty("title", title);
                            postObject.addProperty("content", content);
                            postObject.addProperty("status", status);
                            postObject.addProperty("view_count", viewCount);
                            postObject.addProperty("creator_id", creatorId);
                            postObject.addProperty("scheduled_datetime", scheduledDatetime != null ? scheduledDatetime.toString() : null);
                            postObject.addProperty("category", category);
                            postObject.addProperty("creator_name", creatorName);
                            postObject.addProperty("created_at", createdAt.toString());
                            postObject.addProperty("updated_at", updatedAt.toString());

                            // Lấy danh sách ảnh liên quan đến bài viết
                            JsonArray imagesArray = getImagesForPost(connection, postId);
                            postObject.add("images", imagesArray);

                            postsArray.add(postObject);
                        }

                        jsonResponse.add("posts", postsArray);

                        resp.setContentType("application/json");
                        resp.getWriter().write(jsonResponse.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Kết nối đến CSDL thất bại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JsonArray getImagesForPost(Connection connection, int postId) throws SQLException {
        JsonArray imagesArray = new JsonArray();
        String imagesQuery = "SELECT image_id, image_path FROM post_images WHERE post_id = ?";
        try (PreparedStatement imagesStatement = connection.prepareStatement(imagesQuery)) {
            imagesStatement.setInt(1, postId);
            try (ResultSet imagesResultSet = imagesStatement.executeQuery()) {
                while (imagesResultSet.next()) {
                    int imageId = imagesResultSet.getInt("image_id");
                    String imagePath = imagesResultSet.getString("image_path");

                    JsonObject imageObject = new JsonObject();
                    imageObject.addProperty("image_id", imageId);
                    imageObject.addProperty("image_path", imagePath);

                    imagesArray.add(imageObject);
                }
            }
        }
        return imagesArray;
    }


    private void getPostsByCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String categoryFilter = req.getParameter("category");

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            try (Connection connection = new ConnectionProvider().getConnection()) {
                if (connection != null) {
                    String sql = "SELECT\n" +
                            "    p.post_id,\n" +
                            "    p.title,\n" +
                            "    p.content,\n" +
                            "    p.status,\n" +
                            "    p.view_count,\n" +
                            "    p.creator_id,\n" +
                            "    p.scheduled_datetime,\n" +
                            "    p.category,\n" +
                            "    u.fullname AS creator_name,\n" +
                            "    p.created_at,\n" +
                            "    p.updated_at\n" +
                            "FROM\n" +
                            "    post p\n" +
                            "LEFT JOIN\n" +
                            "    users u ON p.creator_id = u.user_id\n" +
                            "WHERE\n" +
                            "    p.category = ?\n";

                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, categoryFilter);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            JsonArray jsonArray = new JsonArray();

                            while (resultSet.next()) {
                                int postId = resultSet.getInt("post_id");
                                String title = resultSet.getString("title");
                                String content = resultSet.getString("content");
                                String status = resultSet.getString("status");
                                int viewCount = resultSet.getInt("view_count");
                                int creatorId = resultSet.getInt("creator_id");
                                Timestamp scheduledDatetime = resultSet.getTimestamp("scheduled_datetime");
                                String category = resultSet.getString("category");
                                String creatorName = resultSet.getString("creator_name");
                                Timestamp createdAt = resultSet.getTimestamp("created_at");
                                Timestamp updatedAt = resultSet.getTimestamp("updated_at");

                                JsonObject postObject = new JsonObject();
                                postObject.addProperty("post_id", postId);
                                postObject.addProperty("title", title);
                                postObject.addProperty("content", content);
                                postObject.addProperty("status", status);
                                postObject.addProperty("view_count", viewCount);
                                postObject.addProperty("creator_id", creatorId);
                                postObject.addProperty("scheduled_datetime", scheduledDatetime != null ? scheduledDatetime.toString() : null);
                                postObject.addProperty("category", category);
                                postObject.addProperty("creator_name", creatorName);
                                postObject.addProperty("created_at", createdAt.toString());
                                postObject.addProperty("updated_at", updatedAt.toString());

                                // Lấy danh sách ảnh liên quan đến bài viết
                                JsonArray imagesArray = getImagesForPost(connection, postId);
                                postObject.add("images", imagesArray);

                                jsonArray.add(postObject);
                            }

                            resp.setContentType("application/json");
                            resp.getWriter().write(jsonArray.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("Kết nối đến CSDL thất bại.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Thiếu tham số 'category' trong yêu cầu.");
        }
    }

}
