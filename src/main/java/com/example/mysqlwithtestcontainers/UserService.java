package com.example.mysqlwithtestcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private final ConnectionPool connectionPool;

    public UserService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User getUser( int id) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            String query = "SELECT * FROM user WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return new User(rs.getInt("id"), rs.getString("name"));
        }
    }

    public User createUser(User user) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            String query = "INSERT INTO user (name) VALUES (?)";
            PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                user.setId(rs.getInt(1));
            } else {
                throw new SQLException("Failed to create user");
            }
        }
        return user;
    }
}
