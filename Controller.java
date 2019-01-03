
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;


import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.net.URL;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.print.DocFlavor;


public class Controller implements Initializable {


    List<ToSqlObj> objList2 = new ArrayList<>();

    class ToSqlObj {

        String kod, miasto, teryt, woj, gmi, pow;

        public ToSqlObj(String miasto, String woj, String gmi, String pow, String teryt, String kod) {
            this.teryt = teryt;
            this.kod = kod;
            this.miasto = miasto;
            this.gmi = gmi;
            this.woj = woj;
            this.pow = pow;

        }

        public String getKod() {
            return this.kod;
        }

        public String getMiasto() {
            return this.miasto;
        }

        public String getTeryt() {
            return this.teryt;
        }

        public void setKod(String kod) {
            this.kod = kod;
        }

        public void setMiasto(String miasto) {
            this.miasto = miasto;
        }

        public void setTeryt(String teryt) {
            this.teryt = teryt;
        }

        public void setWoj(String woj) {
            this.woj = woj;
        }

        public void setGmi(String gmi) {
            this.gmi = gmi;
        }

        public void setPow(String pow) {
            this.pow = pow;
        }

        public String getWoj() {
            return woj;
        }

        public String getGmi() {
            return gmi;
        }

        public String getPow() {
            return pow;
        }
    }


    @FXML

    private Button btn;

    @FXML

    private ListView list;

    @FXML

    private Button deleteBtn;

    @FXML

    private Button deleteAll;

    @FXML

    private Button importt;

    @FXML

    private Label label;

    @FXML
    private ListView citiesList;


    String fileName;
    public static ResultSet rs;

    public static File DATABASE;


    public static List<File> selectedFile = new ArrayList<>();

    public void importing() {

        if (selectedFile != null) {


            for (File file : selectedFile) {

                fileName = file.getName();

                //czy dane imienia i nazwiska lek. kier. zaczynają się od imienia
                boolean firstNameFirst = false;

                try {
                    String path = file.getCanonicalPath();
                    CsvFileReader.readFile(path, firstNameFirst, fileName);
                } catch (IOException e) {

                }


            }
            list.getItems().clear();
            citiesList.getItems().clear();
            selectedFile = new ArrayList<>();
            label.setText("Importowanie zakończone");

        }

    }

    public static List<String> lista;
    boolean brek = false;


    public void showCities() throws InterruptedException, IOException {

        brek = false;

        lista = new ArrayList<>();

        if (selectedFile != null) {


            List<String[]> records = null;
            CSVReader csvReader;
            CSVParser csvParserBuilder;
            FileReader fileReader = null;
            for (File file : Controller.selectedFile) {
                label.setText("CZEKAJ");

                csvParserBuilder = new CSVParserBuilder().withSeparator(';').build();
                try {
                    fileReader = new FileReader(new File(file.getCanonicalPath()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                csvReader = new CSVReaderBuilder(fileReader).withCSVParser(csvParserBuilder).withKeepCarriageReturn(false).withSkipLines(1).build();
                try {
                    records = csvReader.readAll();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                for (String[] string : records) {

                    int c = 0;
                    try {
                        Importer.stmt = Importer.conn.createStatement();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        Controller.rs = Importer.stmt.executeQuery("SELECT lokalizacje.naz_miejsc, gmi.naz_gmi, pow.naz_pow, woj.naz_woj FROM lokalizacje left join woj on lokalizacje.kod_woj=woj.kod_woj " +
                                "left join pow on pow.kod_pow=lokalizacje.kod_pow " +
                                "left join gmi on gmi.kod_gmi=lokalizacje.kod_gmi ;");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        label.setText("WYBRANO NIEPRAWIDŁOWĄ BAZĘ DANYCH / BŁĄD W BAZIE DANYCH");
                    }

                    String miejsc;
                    String woj;
                    String gmi;
                    String pow;
                    List<String[]> sameCities = new ArrayList<>();
                    int indor = 0;
                    String[] tab = new String[4];


                    while (true) {
                        try {
                            if (!Controller.rs.next()) break;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        try {
                            miejsc = Controller.rs.getString("naz_miejsc");
                        } catch (SQLException e) {
                            miejsc = null;
                        }
                        try {
                            woj = Controller.rs.getString("naz_woj");
                        } catch (SQLException e) {
                            woj = null;
                        }
                        try {
                            gmi = Controller.rs.getString("naz_gmi");
                        } catch (SQLException e) {
                            gmi = null;
                        }
                        try {
                            pow = Controller.rs.getString("naz_pow");
                        } catch (SQLException e) {
                            pow = null;
                        }

                        if (miejsc.equalsIgnoreCase(string[5])) {


//                            System.out.println("SQL: " + miejsc + " " + woj + " | gmina: " + gmi + " | powiat: " + pow);
                            tab[0] = miejsc;
                            tab[1] = woj;
                            tab[2] = gmi;
                            tab[3] = pow;
                            c++;
                        }

                        if (tab[0] != null && tab[1] != null && tab[2] != null && tab[3] != null) {
                            sameCities.add(tab);
                            tab = new String[4];
                        }


                    }
                    System.out.println(string[5] + ": " + c);

                    if (c > 1) {

                        for (String[] str : sameCities) {
                            System.out.println(str[0] + " " + str[1] + " | gmina: " + str[2] + " | powiat: " + str[3]);
                            lista.add(str[0] + " " + str[1] + " | gmina: " + str[2] + " | powiat: " + str[3]);
//                            objList.add(new ToSqlObj(string[4],string[5],"4324"));
                            objList2.add(new ToSqlObj(str[0], str[1], str[2], str[3], "3432423", string[4]));

                        }


                        //
                        // WIDOK OKNA MODALNEGO
                        //


                        Stage dialog = new Stage();
                        ListView<ToSqlObj> list = new ListView();
                        ObservableList<ToSqlObj> listaWidoku = FXCollections.observableList(objList2);
                        list.setItems(listaWidoku);
                        list.setCellFactory(new Callback<ListView<ToSqlObj>, ListCell<ToSqlObj>>() {
                            @Override
                            public ListCell<ToSqlObj> call(ListView<ToSqlObj> param) {
                                ListCell<ToSqlObj> cell = new ListCell<ToSqlObj>() {
                                    @Override
                                    protected void updateItem(ToSqlObj item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item != null) {
                                            setText(item.getMiasto() + " " + item.getWoj() + " | gmina: " + item.getGmi() + " | powiat:" + item.getPow());
                                        }
                                    }
                                };
                                return cell;
                            }
                        });
                        list.setStyle("-fx-border-color: #71bca0; -fx-border-width: 2px; -fx-background-radius: 4px; -fx-border-radius: 4px; ");

                        Button btn = new Button("Potwierdź");
                        Button abort = new Button("Abordaż");
                        btn.setMaxWidth(Double.MAX_VALUE);
                        abort.setMaxWidth(Double.MAX_VALUE);
                        btn.setMinHeight(100);
                        abort.setMinHeight(100);
                        Label lb = new Label("Pacjent: " + string[1] + " " + string[0]);
                        Label cf = new Label("Plik: " + file.getName());
                        Font f = new Font("Lato Light", 20);
                        btn.setFont(f);
                        abort.setFont(f);
                        lb.setFont(f);
                        lb.setTextFill(Paint.valueOf("#ffffff"));
                        lb.setStyle("-fx-background-color: #71bca0;");
                        cf.setFont(f);
                        cf.setTextFill(Paint.valueOf("#ffffff"));
                        cf.setStyle("-fx-background-color: #71bca0;");

                        btn.setOnMouseEntered(e -> {
                            btn.setTextFill(Paint.valueOf("#1ee8ff"));
                        });
                        btn.setOnMouseExited(e -> {
                            btn.setTextFill(Paint.valueOf("#000000"));
                        });
                        abort.setOnMouseEntered(e -> {
                            abort.setTextFill(Paint.valueOf("#ff0000"));
                        });
                        abort.setOnMouseExited(e -> {
                            abort.setTextFill(Paint.valueOf("#000000"));
                        });

                        VBox vb = new VBox();
                        vb.setStyle("-fx-background-color: #71bca0;");
                        vb.setMinWidth(128);
                        vb.getChildren().add(btn);
                        vb.getChildren().add(abort);

                        abort.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                brek = true;
                                dialog.close();
                            }
                        });

                        btn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                if (list.getSelectionModel().isEmpty()) {

                                } else {

                                    dialog.close();

                                    SqlQueries.insert(list.getSelectionModel().getSelectedItem().getKod(), list.getSelectionModel().getSelectedItem().getMiasto(), list.getSelectionModel().getSelectedItem().getTeryt());
//                                System.out.println("HALOO "+ list.getSelectionModel().getSelectedItem().getKod()+list.getSelectionModel().getSelectedItem().getMiasto()+list.getSelectionModel().getSelectedItem().getTeryt());
                                }
                            }
                        });

                        BorderPane rt = new BorderPane();
                        Scene sc = new Scene(rt);

                        rt.setStyle("-fx-background-color: #71bca0; -fx-border-width: 2px; -fx-border-color: #71bca0;");

                        rt.setCenter(list);
                        rt.setRight(vb);
                        rt.setTop(lb);
                        rt.setBottom(cf);

                        dialog.setTitle("Wybierz pacjenta");
                        dialog.setMinWidth(800);

                        dialog.setScene(sc);
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.showAndWait();

                        lista.clear();

                        if (brek == true) {

                            break;
                        }


                        //
                        // KONIEC WIDOKU OKNA MODALNEGO
                        //


                    }


                    try {
                        Controller.rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        Importer.stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


//                citiesList.getItems().add(string[5] + " " + string[4]);

                }
            }
            label.setText("GOTOWE");

        }
    }


    public void modWind(ActionEvent event) {

//        Stage dialog = new Stage();
//
//
//        dialog.initModality(Modality.APPLICATION_MODAL);
//        dialog.showAndWait();

        FileChooser fw = new FileChooser();

        DATABASE = fw.showOpenDialog(null);


        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;

        if (DATABASE != null) {
            try {
                output = new FileOutputStream("config.properties");

                prop.setProperty("database", "jdbc:sqlite:" + DATABASE);
                System.out.println("PROPERTY SET to " + DATABASE);

                try {
                    prop.store(output, null);
                    output.close();
                } catch (IOException f) {
                    f.printStackTrace();
                }

            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } finally {
                Importer.connectToDB();
            }
        }


    }


    public void btnAction(ActionEvent event) throws IOException {

        FileChooser fw = new FileChooser();
        //    fw.showOpenMultipleDialog(null);
        List<File> ll;
        ll = fw.showOpenMultipleDialog(null);

        if (ll != null) {

            selectedFile.addAll(ll);
        }
        if (selectedFile != null) {

//        Desktop desktop = Desktop.getDesktop();  OTWIERANIE PLIKU
//        desktop.open(selectedFile);
            list.getItems().clear();
            for (File fiel : selectedFile) {
                list.getItems().addAll(fiel.getName());
            }

            label.setText("");
        }
        label.setText("");
    }


    public void delete(ActionEvent event) throws ArrayIndexOutOfBoundsException {


        if (selectedFile != null && selectedFile.size() > 0) {
            Object o = list.getSelectionModel().getSelectedItem();
            int indyk = list.getSelectionModel().getSelectedIndex();
            list.getItems().remove(o);

            List<File> modifiable = new ArrayList<File>(selectedFile);

            modifiable.remove(indyk);
            selectedFile = modifiable;
            citiesList.getItems().clear();

        }

    }


    public void deleteAll(ActionEvent event) throws FileNotFoundException {

        if (selectedFile != null) {
            list.getItems().clear();

            selectedFile = new ArrayList<>();
            citiesList.getItems().clear();

        }
//        System.out.println(selectedFile.size());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
