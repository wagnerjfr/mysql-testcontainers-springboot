package com.example.mysqlwithtestcontainers.util;

import com.example.mysqlwithtestcontainers.ConnectionPool;
import com.example.mysqlwithtestcontainers.User;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestUtils {
    private final ConnectionPool connectionPool;

    public TestUtils(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void setUpData() throws SQLException {
        String createUserSql = "create table if not exists user (id int auto_increment primary key, name varchar(20) null);";
        String insertUserSql = "insert into user(name) values('Wagner'), ('Jose'), ('Franchin')";
        try (Connection connection = this.connectionPool.getConnection()) {
            Statement createStatement = connection.createStatement();
            Statement insertStatement = connection.createStatement();
            createStatement.execute(createUserSql);
            insertStatement.execute(insertUserSql);
         }
    }

    public User getRandomUser() throws SQLException {
        String query = "SELECT id, name FROM user ORDER by rand() limit 1";
        try (Connection connection = this.connectionPool.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            resultSet.next();
            return new User(resultSet.getInt("id"), resultSet.getString("name"));
        }
    }

    public User getUserById( int id) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            String query = "SELECT id, name FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new User(rs.getInt("id"), rs.getString("name"));
        }
    }

    public String getRandomString(int length){
        return RandomStringUtils.randomAlphabetic(length);
    }
}
