import java.io.*;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


//TO DO DODAĆ MOŻLIWOŚĆ ZMIANY ŚCIEŻKI ZAPISYWANYCH PLIKÓW SQL


public class Importer extends Application {

    // SQL

    public static Connection conn;
    public static Statement stmt;


    static Properties prop = new Properties();
    static OutputStream output = null;
    static InputStream input = null;

    // SQL

    public static void main(String[] args) throws IOException {


        // PROPERTIES


        try {
            input = new FileInputStream("config.properties");

            try {
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("WORKING ON DATABASE: " + prop.getProperty("database"));

            input.close();

        } catch (FileNotFoundException e) {


            try {
                output = new FileOutputStream("config.properties");

                prop.setProperty("database", "jdbc/:sqlite/:C:/Users/Jakub/Desktop/Importy/importer/Baza.db");

                System.out.println("PROPERTY SET");

                try {
                    prop.store(output, null);
                    output.close();
                } catch (IOException f) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException f) {
                e.printStackTrace();
            }

        } finally {

        }


        // PROPERTIES


        // DATABASE

        conn = null;
        stmt = null;


        // ŁĄCZENIE Z BAZĄ DANYCH


        connectToDB();


        //DATABASE


        // JAVAFX

        launch(args);


        // JAVAFX


    }


    public static void connectToDB() {
        prop = new Properties();
        output = null;
        input = null;

        try {
            input = new FileInputStream("config.properties");

            try {
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("WORKING ON DATABASE: " + prop.getProperty("database"));

            try {
                // db parameters
                String url = prop.getProperty("database");
                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");
//            conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {

            }

            input.close();

        } catch (Exception e) {
        }
    }




    // JAVAFX FUNKCJA START, ŁADUJE PIERWSZY, PODSTAWOWY WIDOK TZW PRIMARY STAGE

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {


            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("widok.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Importer");
            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //FUNKCJA TWORZĄCA PUSTĄ BAZĘ DANYCH

    public static void createNewDatabase(String fileName) {

        String url = "jdbc/:sqlite/:C/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
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


//CREATE TABLE `lokalizacje` (
//	`sym`	TEXT NOT NULL,
//	`naz_gmi`	TEXT,
//	`naz_miejsc`	TEXT,
//	`nazwa_rm`	TEXT,
//	`sym_pod`	TEXT,
//	`typ`	TEXT,
//	`kod_gmi`	TEXT,
//	`kod_woj`	TEXT,
//	`kod_pow`	TEXT,
//	PRIMARY KEY(`sym`)
//);