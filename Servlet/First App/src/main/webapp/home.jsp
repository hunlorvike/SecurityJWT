<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
</head>
<body>
<h1>Welcome, <%= request.getAttribute("username") %>!</h1>
<h1><%= "Dữ liệu từ homeServlet: " + request.getAttribute("message") %></h1>
<a href="login">Login</a>
</body>
</html>
