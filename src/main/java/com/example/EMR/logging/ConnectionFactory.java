package com.example.EMR.logging;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnection;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionFactory {
    private static interface Singleton {
        final ConnectionFactory INSTANCE = new ConnectionFactory();
    }

    private final DataSource dataSource;

    private ConnectionFactory() {
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "123456"); // or get properties from some configuration file

        String url = "jdbc:mysql://localhost:3306/";

        try (Connection connection = DriverManager.getConnection(url, properties)) {
            // Create the database if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS logs_db");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in your application
        }

        // Now, proceed with creating the connection pool and logs table as before
        GenericObjectPool<PoolableConnection> pool = new GenericObjectPool<>();
        DriverManagerConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                "jdbc:mysql://localhost:3306/logs_db", properties
        );
        new PoolableConnectionFactory(
                connectionFactory, pool, null, "SELECT 1", 3, false, false, Connection.TRANSACTION_READ_COMMITTED
        );

        this.dataSource = new PoolingDataSource(pool);
        createLogsTable();
    }

    private void createLogsTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // SQL script to create the logs table
            String sql = "CREATE TABLE IF NOT EXISTS logs ( EVENT_DATE TIMESTAMP, LEVEL VARCHAR(255), LOGGER VARCHAR(255), MSG TEXT, THROWABLE TEXT, ACTOR_ID VARCHAR(36), USER_ID VARCHAR(36))";
            // Execute the SQL statement
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in your application
        }
    }

    public static Connection getDatabaseConnection() throws SQLException {
        return Singleton.INSTANCE.dataSource.getConnection();
    }
}
