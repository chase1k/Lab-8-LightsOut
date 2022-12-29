package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LightsOutModel;
import model.Observer;
import model.Tile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class LightOutGUI extends Application implements Observer{

    private LightsOutModel model;
    private Label mV;
    private Label mG;
    private Button[][] lightBoard;

    /**
     * Makes LightsOut Model
     */
    public void LightsOutGUI() {
        model = new LightsOutModel();
        model.addObserver(this);
    }

    /**
     * Starts the initial representation of the GUI
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        LightsOutGUI();

        BorderPane borderPane = new BorderPane();
        GridPane lights = new GridPane();
        Button newGame = new Button("New Game");
        Button loadGame = new Button("Load Game");
        Button hint = new Button("Hint");

        newGame.setOnAction(event -> model.generateRandomBoard());
        lightBoard = new Button[model.getDimension()][model.getDimension()];

        hint.setOnAction(event -> model.getHint());

        loadGame.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/boards"));
            fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Text Files", "*.lob"));
            fileChooser.setTitle("Load a new game board");
            File selectedFile = fileChooser.showOpenDialog(stage);
            try {
                model.loadBoardFromFile(selectedFile);
            }catch(Exception e){
                mG.setText("Message: Load File Failure");
            }
        });

        mV = new Label("Moves: "+model.getMoves());
        mG = new Label("Message: Start a game first");

        HBox infoBox = new HBox(mV, mG);
        infoBox.setSpacing(80);
        infoBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < model.getDimension(); i++) {
            for (int j = 0; j < model.getDimension(); j++) {

                Button light = new Button();
                light.setPrefSize(125,130);
                int finalI = i;
                int finalJ = j;

                light.setOnAction((event -> {
                    model.toggleTile(finalI, finalJ);
                    mV.setText("Moves: "+model.getMoves());
                }));

                lights.add(light,j,i);
                lightBoard[i][j] = light;
            }
        }
        model.generateRandomBoard();
        lights.setAlignment(Pos.CENTER);

        HBox gameBox = new HBox(newGame, loadGame, hint);
        gameBox.setSpacing(80);
        gameBox.setAlignment(Pos.CENTER);

        borderPane.setTop(infoBox);
        borderPane.setCenter(lights);
        borderPane.setBottom(gameBox);
        borderPane.setPadding(new Insets( 5));

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Lights Out GUI");
        stage.show();
    }

    /**
     * No command line arguments to parse
     * @throws Exception
     */
    @Override
    public void init() throws Exception {System.out.println("init: Initialize and connect to the model!");}

    /**
     * Launches the application
     * @param args
     */
    public static void main(String[] args) {Application.launch(args);}

    /**
     * Updates the model for the viewer to view
     * @param o the object that wishes to inform this object
     *                about something that has happened.
     * @param o2 optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(Object o, Object o2) {

        for (int i = 0; i < model.getDimension(); i++) {
            for (int j = 0; j < model.getDimension(); j++) {
                model.Tile tile = model.getTile(i,j);

                if(tile.isOn()) {
                    lightBoard[i][j].setStyle("-fx-background-color: #ededed;");
                    lightBoard[i][j].setText("ONONONONON\nONONONONON\nONONONONON");
                }else{
                    lightBoard[i][j].setStyle("-fx-background-color: #d754ff;");
                    lightBoard[i][j].setText("OFF");}
            }
        }

        if(o2.toString().contains(",")){
            String x = o2.toString().substring(6,7);
            String y = o2.toString().substring(9,10);
            mG.setText("Message: "+o2);
            lightBoard[Integer.parseInt(x)][Integer.parseInt(y)].setText("!!!!HINT!!!!");
            lightBoard[Integer.parseInt(x)][Integer.parseInt(y)].setStyle("-fx-background-color: #f2ff00;");

        } else if (o2.toString().matches(".*\\d.*")){
            String x = o2.toString().substring(0,1);
            String y = o2.toString().substring(1);
            mG.setText("Message: Move: "+x+", "+y);
            model.getTile(Integer.parseInt(x),Integer.parseInt(x));
        }else if (o2.toString().equals("loaded")) {
            mG.setText("Message: Game Loaded");
            mV.setText("Moves: "+model.getMoves());
        } else{
            mG.setText("Message: My code is bad this error shouldn't happen");
        }

        int win = 0;
        for (int i = 0; i < model.getDimension(); i++) {
            for (int j = 0; j < model.getDimension(); j++) {
                if (model.getTile(i,j).isOn()){win++;}
            }
        }
        if (win==0){mG.setText("Message: Game Won");}
    }
}
