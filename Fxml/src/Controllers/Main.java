package Controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main extends Application {

    public Stage mainStage;

    public WebView webView;

    public HTMLEditor editor;
    private boolean isDirty = false;

    public ScrollPane ListeElements;

    public ArrayList<Button> allButton = new ArrayList<>();

    public File repertoire = new File("C:\\Users\\stherene\\Documents\\NPEI Editor repertory");

    Map<File,String> listeDonnees = new HashMap<>();

    public File onUpdate;
    public Button onUpdateBtn;

    public Button CreateNewBtn;

    @Override
    public void start(final Stage primaryStage) throws IOException {
        mainStage = primaryStage;
        try {
            final URL url = getClass().getResource("../Views/interface.fxml");
            final FXMLLoader fxmlLoader = new FXMLLoader(url);
            final AnchorPane root = (AnchorPane) fxmlLoader.load();
            final Scene scene = new Scene(root, 960   , 600  );
            primaryStage.setScene(scene);
        } catch (IOException ex) {
            exceptionDialog(ex);
        }
        primaryStage.setTitle("NPEI Editor");
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public String getMeTheDateOfFile(File file){
        long lastModified = file.lastModified();

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Date lastModifiedDate = new Date( lastModified );

        return simpleDateFormat.format( lastModifiedDate );

    }

    public void Save() {
        try{
            PrintWriter writer = new PrintWriter(onUpdate);
            writer.println(editor.getHtmlText());
            writer.close();

            listeDonnees.put(onUpdate, editor.getHtmlText());
        }catch (Exception e){
            exceptionDialog(e);
        }
    }

    public void exceptionDialog(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Exception Dialog");
        alert.setContentText(e.toString());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public File selectDirectory(){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Selectionner le repertoire avec vos notes");

        if(repertoire != null){
            dirChooser.setInitialDirectory(repertoire);
        }

        File selectedFile = dirChooser.showDialog(mainStage);
        if (selectedFile != null) {
            return selectedFile;
        }else{
            return null;
        }
    }

    private void changeAllButton(){
        for(Button btn : allButton){
            btn.setId("btnPrimary");
            btn.setText("Mod.");
            btn.setTooltip(null);
        }
    }

    public void Open() throws IOException {
        repertoire = selectDirectory();
        if(repertoire != null) {
            String[] liste = repertoire.list();

            if (liste != null) {
                for (String s : liste) {
                    BufferedReader lecteurAvecBuffer = null;
                    String ligne;


                    try {
                        lecteurAvecBuffer = new BufferedReader(new FileReader(repertoire + "\\" + s));
                        StringBuilder result = new StringBuilder();
                        while ((ligne = lecteurAvecBuffer.readLine()) != null)
                            result.append(ligne);
                        lecteurAvecBuffer.close();
                        listeDonnees.put(new File(repertoire + "/" + s),result.toString());
                    } catch (FileNotFoundException exc) {
                        exceptionDialog(exc);
                    }
                }

                VBox root = new VBox();
                root.setId("Zone");
                root.setMaxWidth(183);
                root.setMinWidth(183);
                for (Map.Entry<File, String> entry : listeDonnees.entrySet()) {
                    AnchorPane anch = new AnchorPane();
                    anch.setId("CaseDeMemoire");


                    Label lbl = new Label(entry.getKey().getName().replace(".npei",""));
                    AnchorPane.setBottomAnchor(lbl, 0.0);
                    AnchorPane.setLeftAnchor(lbl, 10.0);
                    AnchorPane.setRightAnchor(lbl, 100.0);
                    AnchorPane.setTopAnchor(lbl, 0.0);
                    System.out.println(getMeTheDateOfFile(entry.getKey()));

                    Button btnEdit = new Button("\uD83D\uDD8D");
                    btnEdit.setFont(new Font("Arial Black", 10));
                    allButton.add(btnEdit);
                    btnEdit.setId("btnPrimary");
                    AnchorPane.setBottomAnchor(btnEdit, 0.0);
                    AnchorPane.setRightAnchor(btnEdit, 55.0);
                    AnchorPane.setTopAnchor(btnEdit, 0.0);

                    btnEdit.onActionProperty().setValue(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if(onUpdateBtn != btnEdit && onUpdate != entry.getKey()){
                                if(!refreshDirty()){
                                    editor.setVisible(true);
                                    editor.setHtmlText(entry.getValue());
                                    //webView.getEngine().loadContent(entry.getValue());
                                    changeAllButton();
                                    btnEdit.setId("btnSecondary");
                                    btnEdit.setText("Focus");
                                    btnEdit.setTooltip(new Tooltip("Vous regardez ce fichier."));
                                    btnEdit.setTooltip(new Tooltip("Vous regardez ce fichier."));
                                    onUpdate = entry.getKey();
                                    onUpdateBtn = btnEdit;
                                }
                            }
                        }
                    });

                    Button btnSuppr = new Button("\uD83D\uDDD1");
                    btnSuppr.setId("btnDanger");
                    AnchorPane.setBottomAnchor(btnSuppr, 0.0);
                    AnchorPane.setRightAnchor(btnSuppr, 0.0);
                    AnchorPane.setTopAnchor(btnSuppr, 0.0);

                    anch.getChildren().addAll(lbl, btnEdit, btnSuppr);
                    root.getChildren().add(anch);
                }
                root.setSpacing(10);
                ListeElements.setContent(root);
                //ListeElements.setPannable(true);
            } else {
                System.err.println("Nom de repertoire invalide");
            }
        }
    }

    public boolean refreshDirty(){
        if(!isDirty) return false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Données non enregistrées !");
        alert.setHeaderText("Attention !");
        alert.setContentText("Les données que vous avez modifiez ne seront pas enregistrées.");
        ImageView img = new ImageView(this.getClass().getResource("/Styles/warning.png").toString());
        img.setFitHeight(50);
        img.setFitWidth(50);
        alert.setGraphic(img);

        ButtonType buttonTypeOne = new ButtonType("Sauvegarder puis continuer");
        ButtonType buttonTypeTwo = new ButtonType("Continuer sans sauv");
        ButtonType buttonTypeCancel = new ButtonType("Annuler");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            Save();
            isDirty = false;
            return false;
        }

        if (result.get() == buttonTypeTwo){
            isDirty = false;
            return false;
        }

        return true;
    }

    public void textChange() {
        try{
            isDirty = true;
            onUpdateBtn.setId("btnDanger");
            onUpdateBtn.setText("Updated");
        }catch (Exception e){
            exceptionDialog(e);
        }
    }

    public void CreateNewFile(){
        if(repertoire == null){
            while ((repertoire = selectDirectory()) == null);
        }

        TextInputDialog inDialog = new TextInputDialog("New File");
        inDialog.setTitle("Create a new file");
        inDialog.setContentText("Name of file :");
        Optional<String> textIn;
        do{
            textIn = inDialog.showAndWait();
        }while (!textIn.isPresent());

        try {
            new PrintWriter(repertoire + "/" +textIn.get()).close();
        } catch (FileNotFoundException e) {
            exceptionDialog(e);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}