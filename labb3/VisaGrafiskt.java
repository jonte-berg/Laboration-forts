/*
        Kurs: 1IK153
        Laboration: Labb3:2
        Kursdeltagare: Jonathan Berg
        Termin och datum: VT 2022 19/1
*/
package com.example.labb3;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class VisaGrafiskt extends Application  {


    @Override
    public void start(Stage stage) throws SQLException, ClassNotFoundException {


        BorderPane pane = new BorderPane();
        Label l1 = new Label();
        l1.setText("MusikDB");
        l1.setFont(new Font(26));
        pane.setTop(l1);


        ArrayList<String> dbTest = dbGrab();


        ObservableList<String> names = FXCollections.observableArrayList();

        for(int i =0;i<dbTest.size();i++)
            names.add(i,dbTest.get(i));


        ListView<String> listView = new ListView<>(names);
        listView.setMaxSize(200, 500);
        pane.setLeft(listView);




        ScrollPane beskrivning = new ScrollPane();
        beskrivning.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 2;" + "-fx-border-color: gray;");


        Text t1 = new Text();
        t1.setTextAlignment(TextAlignment.LEFT);
        t1.setFont(new Font(16));
        beskrivning.setContent(t1);
        beskrivning.setMaxWidth(500);
        pane.setCenter(beskrivning);

        Scene scene = new Scene(pane,730,500);
        stage.setTitle("MusikDB");
        stage.setScene(scene);
        stage.show();



        listView.setOnMouseClicked(e ->{

            String s;
          
            for (int i=0;i<dbTest.size();i++){
                    //ge S v??rdet av Artistnamnet
                    s= dbTest.get(i);
               //om du har klickat p?? artistnamnet
                if (s==listView.getSelectionModel().getSelectedItem()) {
                    //skapa ny lista ( f??r album)
                    ArrayList<String> albumLista = new ArrayList<>();

                    try {
                        //h??mta albumen ifr??n DB
                        albumLista =dbClick(s);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    //skapa en str??ng f??r att l??gga in i javaFX
                    String resultat="";
                    //iterera genom hela albumlistan
                    for (int y=0;y<albumLista.size();y++) {
                        //str??ngen som ska l??ggas in i Scrollpane panelen blir resultat, h??mta namnet ifr??n albumlistan som skapades, och g??r radbrytning
                        resultat+=albumLista.get(y)+"\n";

                    }
                    //alla album har nu lagts in i reusltat str??ngen, s??tt t1 till resultat (ligger i scrollpane)
                    t1.setText(resultat);
                    break;
                }
            }

        });

    }

    public static void main(String[] args)  {
        launch(args);

    }

    //h??mtar ifr??n artist DB
    public ArrayList<String> dbGrab() throws SQLException, ClassNotFoundException {
         final String url = "jdbc:mysql://localhost/musikdatabasen";
         final String user = "root";
         final String pass = "123456789";

        ArrayList<String> dbNamn = new ArrayList<>();
        Connection conn;

        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Connecting to selected database...");
        conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Connected to database successfully...");
        Statement statement = conn.createStatement();

        //want to grab from artist , i use this string to decide from which DBN to grab from and use in my query
        String s = "artist";

        //result = the query given, decided by String s
        ResultSet result = statement.executeQuery("Select * from "+ s);


        //as long as the resultset has values, i print the value of each row from each column
        while(result.next()){
            //OBS! jag H??MTAR IFR??N KOLLUMN 1 (NAMN) , kol 2=bildades,kol3=genre
            dbNamn.add(result.getString(1));
        }



        return  dbNamn;
    }

    //h??mtar ifr??n DB album, anv??nds vid actionevent onclick( parameter S= str??ngen som anv??nds f??r att filtrera i query)
    public ArrayList<String> dbClick(String s) throws SQLException, ClassNotFoundException {
        final String url = "jdbc:mysql://localhost/musikdatabasen";
        final String user = "root";
        final String pass = "123456789";

        ArrayList<String> dbAlbum = new ArrayList<>();
        Connection conn;

        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Connecting to selected database...");
        conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Connected to database successfully...");
        Statement statement = conn.createStatement();




       // notera query, h??mtar albumnamn d??r PK=FK och artist = S-parameterN ifr??n actionEventClick, order by utgivnings??r(enligt upgft)
        ResultSet result = statement.executeQuery("select album.Namn from album,artist where album.Artist=Artist.Namn AND album.Artist='"+s+"' order by Utgivningsar");


        //as long as the resultset has values, add to the arrayList that will be sent back from the method
        while(result.next()){
            //notera KOL1=ALBUMnamn, kol2=utgivnings??r, kol3=artist
            dbAlbum.add(result.getString(1));
        }



        return  dbAlbum;
    }

}