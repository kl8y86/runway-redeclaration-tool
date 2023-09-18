package uk.ac.soton.ecs.runwayredeclarationtool.ui;

import javafx.geometry.Insets;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer.MarginType;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.CalculationCase;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.FinalCalculation;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.HomeController;

public class BreakdownController extends BorderPane {

  private final Stage stage;
  private FinalCalculation model;

  private HomeController homeController;

  private static final int WIDTH = 400;
  private static final int HEIGHT = 450;

  private VBox takeoffVbox;

  private VBox landingVbox;

  private VBox inputsVbox;

  private Button closeButton;

  private Button printButton;

  public BreakdownController(FinalCalculation calculation, HomeController homeController) {
    this.model = calculation;
    this.homeController = homeController;

    inputsVbox = new VBox();
    inputsVbox.setPadding(new Insets(5, 5, 5, 5));
    inputsVbox.setSpacing(5);

    takeoffVbox = new VBox();
    takeoffVbox.setPadding(new Insets(5, 5, 5, 5));
    takeoffVbox.setSpacing(5);

    landingVbox = new VBox();
    landingVbox.setPadding(new Insets(5, 5, 5, 5));
    landingVbox.setSpacing(5);

    var closeVbox = new VBox(30);
    closeVbox.setPadding(new Insets(5, 5, 5, 5));

    printButton = new Button("Print");
    printButton.setOnAction(e -> {
      print();
      homeController.addNotification("Printed redeclared values and calculations", Color.YELLOW);
    });
    closeVbox.getChildren().add(printButton);

    closeButton = new Button("Close");
    closeButton.setOnAction(e -> this.onCloseButtonAction());
    closeVbox.getChildren().add(closeButton);
    VBox.setVgrow(closeVbox, Priority.ALWAYS);

    var parent = new VBox(inputsVbox, takeoffVbox, landingVbox, closeVbox);
    parent.setSpacing(10);

    this.setCenter(parent);

    addInputs();
    addTakeoffText();
    addLandingText();

    var scene = new Scene(this, WIDTH, HEIGHT);
    stage = new Stage();
    stage.setTitle("Calculation Breakdown");
    stage.setScene(scene);

    stage.show();
  }

  private void addInputs() {
    var title = new Label("Your Inputs");
    title.setStyle("-fx-font-weight: bold");
    inputsVbox.getChildren().add(title);

    inputsVbox.getChildren().addAll(
        new Label(
            model.getCalculationCase() == CalculationCase.TOWARDS_OBSTACLE ? "Towards Obstacle"
                : "Away from Obstacle"),
        new Label("Runway: " + model.getRunway().getAirport() + "/" + model.getRunway()
            .getRunwayDesignator()),
        new Label("Aircraft: " + model.getAircraftType().getModel() + " (Blast Dist. "
            + model.getAircraftType().getBlastDist() + ")"),
        new Label("Obstacle: " + model.getObstacle().getHeight() + "m x " + model.getObstacle()
            .getLength() + "m, " + model.getObstDistanceFromThreshold() + "m from threshold")
    );
  }

  private void addLandingText() {
    var title = new Label("Landing Calculations (LDA)");
    title.setStyle("-fx-font-weight: bold");

    landingVbox.getChildren().add(title);

    if (model.getCalculationCase() == CalculationCase.TOWARDS_OBSTACLE) {
      landingVbox.getChildren().addAll(
          new Label("RESA = 240m"),
          new Label(String.format("LDA = %d - RESA - 60 = %dm", model.getRunway().getLda(),
              model.getResult().lda()))
      );
    } else {
      landingVbox.getChildren().addAll(
          new Label(String.format("ALS dist = %d * 50 = %dm", model.getObstacle().getLength(),
              model.getObstacle().getLength() * 50)),
          model.getObstacle().getLength() * 50 >= 240 + model.getObstacle().getLength() ?
              new Label("RESA = ALS dist") : new Label(
              String.format("RESA = 240 + %d = %dm", model.getObstacle().getLength(),
                  model.getObstacle().getLength() + 240)),
          new Label(String.format("LDA = %d - %d - RESA - 60 = %dm", model.getRunway().getLda(),
              model.getResult().lda(), model.getResult().lda()))
      );
    }
  }

  private void addTakeoffText() {
    var title = new Label("Take-off Calculations (TORA, TODA, ASDA)");
    title.setStyle("-fx-font-weight: bold");

    takeoffVbox.getChildren().add(title);

    if (model.getCalculationCase() == CalculationCase.TOWARDS_OBSTACLE) {
      takeoffVbox.getChildren().addAll(
          new Label(String.format("TORA = %d + %d - %d - 60 = %dm",
              Math.max(model.getObstDistanceFromThreshold(),
                  model.getRunway().getDisplacedThreshold()), model.getObstacle().getHeight() * 50,
              240 + model.getObstacle().getLength(), model.getResult().tora())),
          new Label("TORA = TODA = ASDA")
      );
    } else {
      takeoffVbox.getChildren().addAll(
          new Label(String.format("d = %d + %d + %d (%s) = %dm", model.getObstacle().getLength(),
              model.getObstDistanceFromThreshold(), model.getAircraftType().getBlastDist(),
              model.getAircraftType().getModel(),
              model.getObstacle().getLength() + model.getObstDistanceFromThreshold()
                  + model.getAircraftType().getBlastDist())),
          new Label(String.format("TORA = %d - d = %dm", model.getRunway().getTora(),
              model.getResult().tora())),
          new Label(String.format("TODA = %d - d = %dm", model.getRunway().getToda(),
              model.getResult().toda())),
          new Label(String.format("ASDA = %d - d = %dm", model.getRunway().getAsda(),
              model.getResult().asda()))
      );
    }
  }

  private void print() {
    var job = PrinterJob.createPrinterJob();
    if (job != null && job.showPrintDialog(getScene().getWindow())) {
      if (job.printPage(this)) {
        job.endJob();
      }
    }
  }

  private void onCloseButtonAction() {
    stage.close();
  }
}
