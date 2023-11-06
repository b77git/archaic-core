package me.b77.archaiccore.database;

import java.io.File;
import java.sql.*;

public class Database {
    public Connection connect() {
        File database_file = new File("jdbc:sqlite:" + me.b77.archaiccore.ArchaicCore.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "archaiccore.db");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(String.valueOf(database_file));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewDatabase() {
        File database_file = new File("jdbc:sqlite:" + me.b77.archaiccore.ArchaicCore.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "archaiccore.db");

        try (Connection conn = DriverManager.getConnection(String.valueOf(database_file))) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
