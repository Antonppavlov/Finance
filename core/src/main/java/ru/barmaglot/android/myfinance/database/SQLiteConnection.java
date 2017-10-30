package ru.barmaglot.android.myfinance.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLiteConnection {

    private static Connection connection;
    private static String driverClassName;
    private static String urlConnection;


    public static void init(String driverClassName, String urlConnection) {
        SQLiteConnection.driverClassName = driverClassName;
        SQLiteConnection.urlConnection = urlConnection;

        createConnection();
    }

    private static void createConnection() {
        try {
            if (driverClassName == null) {
                driverClassName = "org.sqlite.JDBC";
            }
            if (urlConnection == null) {
                urlConnection="jdbc:sqlite:/Users/antonpavlov/StudioProjects/Finance/app/src/main/assets/money.db";
            }

            Class.forName(driverClassName).newInstance();

            if (connection == null) {
                connection = DriverManager.getConnection(urlConnection);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                connection.createStatement().execute("PRAGMA encoding = \"UTF-8\"");
            }

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static Connection getConnection() {
        if (connection == null) {
            createConnection();
        }
        return connection;
    }


}