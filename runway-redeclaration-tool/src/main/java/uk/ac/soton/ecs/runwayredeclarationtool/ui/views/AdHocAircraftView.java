package uk.ac.soton.ecs.runwayredeclarationtool.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.HomeController;

public class AdHocAircraftView extends BorderPane {

  Stage stage;
  TextField blastAreaBox;

  private final HomeController controller;

  public AdHocAircraftView(HomeController controller) {
    this.controller = controller;
    var vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(10.0d);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    this.setCenter(vbox);

    vbox.getChildren().add(new Label("Aircraft Blast Area (m):"));

    blastAreaBox = new TextField();
    blastAreaBox.setAlignment(Pos.CENTER);
    blastAreaBox.setPromptText("Blast Area (m)");
    setLimitInts(blastAreaBox, 4);

    var save = new Button("Save");
    save.setAlignment(Pos.CENTER);
    blastAreaBox.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        submit();
      }
    });

    save.setOnAction(e -> {
      submit();
    });

    vbox.getChildren().addAll(blastAreaBox, save);
  }

  public void submit() {
    if (!isValid()) {
      var err = new Alert(AlertType.ERROR);
      err.setHeaderText("Invalid input");
      err.setContentText("The blast area is required and must be an integer.");
      err.show();
      return;
    }

    controller.selectAircraft(new Aircraft("OTHR", Integer.parseInt(blastAreaBox.getText())));
    ((Stage) this.getScene().getWindow()).close();
  }

  public void show() {
    stage = new Stage();
    stage.setTitle("Ad-Hoc Aircraft Type");
    stage.setScene(new Scene(this, 400, 200));
    stage.show();

    stage.setOnCloseRequest(e -> {
      controller.selectAircraft(null);
    });
  }

  private void setLimitInts(TextField field, int limit) {
    field.setOnKeyTyped(e -> {
      if (field.getText().length() > limit) {
        field.setText(field.getText().substring(0, limit));
      }

      if (!field.getText().matches("[0-9]+")) {
        field.setText("");
      }
    });
  }

  private boolean isValid() {
    return blastAreaBox.getText().length() > 0 && blastAreaBox.getText().matches("[0-9]+");
  }
}
