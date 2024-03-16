package com.example.rapizz;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnect {

    public String url;
    public String user;
    public String password;
    DataBaseConnect(String url, String user, String password) {

        try (FileInputStream input = new FileInputStream("src/main/resources/config_db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.url = prop.getProperty("database.url");
            this.user = prop.getProperty("database.user");
            this.password = prop.getProperty("database.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        /*try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties n'a pas été trouvé dans le classpath");
            }
            Properties prop = new Properties();
            prop.load(input);
            this.url = prop.getProperty("database.url");
            this.user = prop.getProperty("database.user");
            this.password = prop.getProperty("database.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/


    }



    public Connection connect() {

        try {
            // Assurez-vous que le pilote JDBC est chargé
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connecté à la base de données SQL Server avec succès.");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("Le pilote JDBC n'a pas été trouvé.");
        } catch (SQLException e) {
            System.out.println("Une erreur s'est produite lors de la connexion à la base de données. \n Error : \n"+e.getMessage());
        }
        return null;
    }
}
