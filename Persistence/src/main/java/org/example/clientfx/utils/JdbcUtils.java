package org.example.clientfx.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class JdbcUtils {

    private Properties jdbcProps;
    private Connection instance = null;

    public JdbcUtils(Properties props) {

        jdbcProps = props;
    }

    private Connection getNewConnection() {
        String url = jdbcProps.getProperty("server.jdbc.url");
        String user = jdbcProps.getProperty("server.jdbc.user");
        String pass = jdbcProps.getProperty("server.jdbc.password");
        System.out.println("Connecting to "+user+" "+pass+" "+url);
        Connection con = null;
        try {

            if (user != null && pass != null)
                con = DriverManager.getConnection(url, user, pass);
            else
                con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error getting connection " + e);
        }
        return con;
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed())
                instance = getNewConnection();

        } catch (SQLException e) {
            System.out.println("Error at DB " + e);
        }
        return instance;
    }
}
