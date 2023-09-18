package uk.ac.soton.ecs.runwayredeclarationtool.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;
import uk.ac.soton.ecs.runwayredeclarationtool.RunwayRedeclarationApp;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.*;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.AircraftRepository;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.AirportRepository;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.CalculationCase;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.FinalCalculation;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.PartialCalculation;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.views.AdminView;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.views.ExportView;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.views.ImportView;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class HomeController implements Initializable {

  //  public int width = 521; prefHeight="438.0" prefWidth="882.0"
  //  public int height = 312;

  protected boolean obstacle = false;
  protected double width;
  protected double windowWidth = 941.0;
  protected double height;
  protected double windowHeight = 470.0;
  @FXML
  protected Canvas canvas;

  @FXML
  protected Pane displayPane;

  // holds current user
  protected static User currentUser;

  protected static AircraftRepository aircraftRepository = new AircraftRepository();

  // for import/export window
  public int impExpWidth = 320;
  public int impExpHeight = 240;

  // s for size, instead of writing width * 3f/16, can just choose a size with index: width * s[3]
  private final float[] s = {
      0, 1f / 16, 2f / 16, 3f / 16, 4f / 16, 5f / 16, 6f / 16, 7f / 16, 8f / 16, 9f / 16, 10f / 16,
      11f / 16, 12f / 16, 13f / 16, 14f / 16, 15f / 16
  };
  @FXML // fx:id = "airp"
  protected Label airp;
  @FXML // fx:id = "runw"
  protected Label runw;

  @FXML // fx:id = "AirportSelectionBox"
  protected ComboBox<String> airportSelectionBox;

  @FXML // fx:id = "RunwaySelectionBox;
  protected ComboBox<String> runwaySelectionBox;

  @FXML
  protected ComboBox<String> caseComboBox;

  @FXML
  protected Label selectObstacleLabel;

  @FXML
  protected ComboBox<String> predefObstComboBox;

  @FXML
  protected Label runwayLabel;

  // Parameters
  @FXML
  protected Spinner<Integer> obstWidthSpinner;
  @FXML
  protected Spinner<Integer> obstHeightSpinner;
  @FXML
  protected Spinner<Integer> obstDistSpinner;

  // Results
  @FXML
  protected TextField toraBox;
  @FXML
  protected TextField todaBox;
  @FXML
  protected TextField asdaBox;
  @FXML
  protected TextField ldaBox;

  // Buttons
  @FXML
  protected Button rotateButton;
  @FXML
  protected Button calculateButton;
  @FXML
  protected Button switchViewButton;
  @FXML
  protected Button breakdownButton;
  @FXML
  protected Button resetButton;

  @FXML
  protected Button protan;
  @FXML
  protected Button tritan;
  // @FXML
  // protected Button deuteran;

//  @FXML
//  protected Button saveButton;

//  @FXML
//  protected Button printButton;

  protected Button dummyButton = new Button();

  protected String baseStyle = dummyButton.getStyle();

  // Texts

  // 1st, 2nd and 3rd vertical arrow measurements ordered by arrow length
  @FXML
  protected Text firstVert;
  @FXML
  protected Text secondVert;
  @FXML
  protected Text thirdVert;

  // far left and far right horizontal measurements
  @FXML
  protected Text rightHorizontal;
  @FXML
  protected Text leftHorizontal;

  // remaining two horizontal measurements bottom left
  @FXML
  protected Text largeHorizontal;
  @FXML
  protected Text smallHorizontal;

  // true = a side view is showing, false = topDown
  protected Boolean side = false;

  // login details
  private static String uname = "";
  private static String pword = "";
  // normal
  public Color backgroundColour = Color.MEDIUMPURPLE;

  // protan/deuteran
  // public Color backgroundColour = Color.web("#FE0000");

  // tritan
  // public Color backgroundColour = Color.web("#5C0063");
  @FXML
  protected Text leftDir;
  @FXML
  protected Text rightDir;

  // Notifications box
  @FXML
  protected ScrollPane notificationsPane;
  @FXML
  protected VBox notificationsContent;

  /**
   * Assigns uname + pword after being retrieved from login screen
   */
  public void setLoginCred(Pair<String, String> loginCred) {
    uname = loginCred.getKey();
    pword = loginCred.getValue();
    System.out.println(String.format("Logged in as %s (pword: %s)", uname, pword));
  }

  @FXML
  protected ComboBox<String> aircraftTypeComboBox;

  private FinalCalculation calc = null;

  public Text toraTodaAsda;
  public Text buffer;
  public Text minResa;
  public Text towardHeight;
  public Text heightTimesFifty;

  private final String xmlFileRootDir = "/logs/";
  // as much as i would like to neatly put the logs into a folder literally anywhere, java seems to
  // be allergic to
  // specifying any subdirectories and i want to keep my hair so this is unused for now

  public Text awayHeight;
  public Text awayLda;
  public Text tora;
  public Text toda;
  public Text asda;

  public Text towardLda;
  public Text towardtora;
  public Text towardtoda;
  public Text towardasda;

  protected Runway runway = null;
  protected Aircraft aircraftType = null;

  protected boolean rotated = false;

  // toward = side toward, away = side away
  protected String sideState = "toward";

  // protan/deuteran
  // public Color backgroundColour = Color.web("#FE0000");

  // tritan
  // public Color backgroundColour = Color.web("#5C0063");

  // Slider
  @FXML
  private Slider textScaleSlider;

  // Labels for parameters and results sections
  @FXML
  protected Label selectRunwayLabel;
  @FXML
  protected Label aircraftTypeLabel;
  @FXML
  protected Label obstacleWidthLabel;
  @FXML
  protected Label obstacleHeightLabel;
  @FXML
  protected Label distanceFromThresholdLabel;
  @FXML
  protected Label toraLabel;
  @FXML
  protected Label todaLabel;
  @FXML
  protected Label asdaLabel;
  @FXML
  protected Label ldaLabel;
  @FXML
  protected Label textScalingLabel;

  /**
   * Changes the size of the text depending on the value on the slider
   *
   * @param newValue
   */
  private void onSliderChanged(double oldValue, double newValue) {
    List<Text> runwayTexts =
        Arrays.asList(
            firstVert,
            secondVert,
            thirdVert,
            rightHorizontal,
            leftHorizontal,
            largeHorizontal,
            smallHorizontal,
            leftDir,
            rightDir,
            toraTodaAsda,
            buffer,
            minResa,
            towardHeight,
            heightTimesFifty,
            towardLda,
            towardtora,
            towardtoda,
            towardasda,
            awayHeight,
            awayLda,
            tora,
            toda,
            asda); // list of all the text

    List<Label> labelTexts =
        Arrays.asList(
            selectRunwayLabel,
            aircraftTypeLabel,
            obstacleWidthLabel,
            obstacleHeightLabel,
            distanceFromThresholdLabel,
            toraLabel,
            todaLabel,
            asdaLabel,
            ldaLabel,
            textScalingLabel,
            airp,
            runw,
            selectObstacleLabel);

    //        List<Button> buttonTexts = Arrays.asList(rotateButton, breakdownButton,
    // calculateButton, switchViewButton);

    double scaleValue = 1;

    // scale the runway text
    for (Text text : runwayTexts) {
      if (newValue < oldValue) {
        text.setFont(Font.font(text.getFont().getSize() - scaleValue));
      } else {
        text.setFont(Font.font(text.getFont().getSize() + scaleValue));
      }
    }

    // scale the labels on the sides
    for (Label label : labelTexts) {
      if (newValue < oldValue) {
        label.setFont(Font.font(label.getFont().getSize() - scaleValue));
      } else {
        label.setFont(Font.font(label.getFont().getSize() + scaleValue));
      }
    }

    //        //scale the button texts
    //        double value;
    //        for (Button button : buttonTexts) {
    //            if (newValue < oldValue) {
    //                value = button.getFont().getSize() - scaleValue;
    //            } else {
    //                value = button.getFont().getSize() + scaleValue;
    //            }
    //            button.setStyle("-fx-font-size: " + value + ";");
    //        }
  }

  /**
   * Rotates the canvas and the text
   *
   * @param gc    Graphics Context
   * @param angle Angle to rotate to
   */
  private void rotateRunway(GraphicsContext gc, double angle) {
    gc.clearRect(0, 0, width, height); // clears the canvas
    gc.setFill(backgroundColour); // background colour
    gc.fillRect(0, 0, width, height); // paints background
    gc.save(); // saves the contents of the graphics

    Rotate rotate, rotate2, rotate3;
    if (rotated) {
      rotate =
          new Rotate(
              angle, canvas.getWidth() / 2, canvas.getHeight() / 2); // rotate canvas graphics
    } else {
      rotate =
          new Rotate(
              0,
              canvas.getWidth() / 2,
              canvas.getHeight() / 2); // rotate back to the original position of the canvas
    }
    gc.setTransform(
        rotate.getMxx(),
        rotate.getMyx(),
        rotate.getMxy(),
        rotate.getMyy(),
        rotate.getTx(),
        rotate.getTy()); // transforms the canvas graphics

    //gc.setFill(Color.MEDIUMPURPLE); // paints the new background (fills up the new white space)
    //gc.fillRect(0, 0, width, height);

    List<Text> texts =
        Arrays.asList(
            firstVert,
            secondVert,
            thirdVert,
            rightHorizontal,
            leftHorizontal,
            largeHorizontal,
            smallHorizontal,
            leftDir,
            rightDir,
            towardLda,
            towardtora,
            towardtoda,
            towardasda,
            awayLda,
            tora,
            toda,
            asda); // list of all the text
    double xDistanceToCentre, yDistanceToCentre;
    for (Text text : texts) {
      // work out distances to center of canvas
      xDistanceToCentre = (canvas.getWidth() / 2 - (text.getLayoutX() - canvas.getLayoutX()));
      yDistanceToCentre = (canvas.getLayoutY() + canvas.getHeight() / 2) - (text.getLayoutY());
      if (rotated) { // CHANGING TO THE TRUE BEARING
        rotate2 = new Rotate(angle, xDistanceToCentre, yDistanceToCentre);
        text.getTransforms().add(rotate2);
        rotate3 =
            new Rotate(
                -angle,
                text.getBoundsInLocal().getWidth() / 2,
                text.getBoundsInLocal().getHeight() / 2);
        text.getTransforms().add(rotate3);
      } else { // CHANGING BACK TO ORIGINAL
        rotate3 =
            new Rotate(
                angle,
                text.getBoundsInLocal().getWidth() / 2,
                text.getBoundsInLocal().getHeight() / 2); // rotate the text so it is not straight
        text.getTransforms().add(rotate3);
        rotate2 = new Rotate(-angle, xDistanceToCentre, yDistanceToCentre);
        text.getTransforms().add(rotate2);
      }
    }

    // resize
    if (rotated) {
      gc.scale(0.5, 0.5);
      gc.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

      for (Text text : texts) {
        if (text != leftDir && text != rightDir) {
          text.setVisible(false);
        }
      }
    } else {
      gc.scale(1, 1);
      gc.translate(0, 0);

      for (Text text : texts) {
        if (text != leftDir && text != rightDir) {
          text.setVisible(true);
        }
      }
    }

    topDown(); // paint the runway
    if (obstacle) {
      updateLabels();
    }
  }

  public void paintObstacle(int value) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(Color.RED);

    //gc.fillRect(width * 4.5 / 16, height * 8.5 / 16, width * 1 / 16, height * 0.5 / 16);
    //topdown
    //gc.fillRect(width * 4.5 / 16, height * 8.5 / 16, width * 1 / 16, height * 0.5 / 16);

    //side
    //gc.fillRect(width * 1 / 16, height * 8.5 / 16, width * 3.5 / 16, height * 0.5 / 16);
    if (obstacle == true) {
      if (side == false) {
        gc.fillRect(width * value / 16, height * 7.5 / 16, width * 0.5 / 16, height * 1 / 16);
      } else {
        gc.fillRect(width * value / 16, height * 8.5 / 16, width * 0.5 / 16, height * 0.5 / 16);
      }
    }
    gc.setFill(Color.WHITE);
    // towards = (Take Off Towards, Landing Towards), away = (Take off Away, Landing Over)
//        // ^ use calculation class when ready
//        if (circumstance.equals("away")) {
//            if (side == false) {
//                gc.fillRect(width * 4.5 / 16, height * 8.5 / 16, width * 1 / 16, height * 0.5 / 16);
//            } else if (sideState.equals("away")) {
//                gc.fillRect(width * 1 / 16, height * 8.5 / 16, width * 3.5 / 16, height * 0.5 / 16);
//            } else {
//                // gc.fillRect(width * 1 / 16, height * 8.5 / 16, width * 3.5 / 16, height * 0.5 / 16);
//            }
//        } else {
//            // change to the other side on other view at some point
//            if (side == false) {
//                gc.fillRect(width * 4.5 / 16, height * 8.5 / 16, width * 1 / 16, height * 0.5 / 16);
//            } else if (sideState.equals("away")) {
//                gc.fillRect(width * 1 / 16, height * 8.5 / 16, width * 3.5 / 16, height * 0.5 / 16);
//            } else {
//                // gc.fillRect(width * 1 / 16, height * 8.5 / 16, width * 3.5 / 16, height * 0.5 / 16);
//            }
//        }
  }

  public void topDown() {
    side = false;
    resetDistances();
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, width, height);
    // background
    gc.setFill(backgroundColour);
    gc.fillRect(0, 0, width, height);
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(1);

    // top & bottom trapeziums (octagon)
    double[] xP = {
        width * 1 / 4,
        width * 3 / 16,
        width * 3 / 16,
        width * 1 / 4,
        width * 3 / 4,
        width * 13 / 16,
        width * 13 / 16,
        width * 3 / 4
    };
    double[] yP = {
        height * 3 / 16,
        height * 5 / 16,
        height * 11 / 16,
        height * 13 / 16,
        height * 13 / 16,
        height * 11 / 16,
        height * 5 / 16,
        height * 3 / 16
    };
    int nP = 8;
    gc.setFill(Color.BLUE);
    gc.fillPolygon(xP, yP, nP);
    gc.strokePolygon(xP, yP, nP);

    // innerstrip
    gc.setFill(Color.BLUE);
    gc.fillRect(0, height * 5 / 16, 521, height * 6 / 16);
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, height * 5 / 16, 521, height * 6 / 16);

    // reapply octagon to get rid of rectangle strokes (black outline)
    gc.fillPolygon(xP, yP, nP);

    // runway rectangle
    gc.setFill(Color.GRAY);
    gc.fillRect(width * 1 / 16, height * 7 / 16, width * 14 / 16, height * 2 / 16);
    gc.strokeRect(width * 1 / 16, height * 7 / 16, width * 14 / 16, height * 2 / 16);

    // runway middle line
    gc.setStroke(Color.GHOSTWHITE);
    gc.setLineWidth(2);
    gc.setLineDashes(10);
    gc.strokeLine(width * 1 / 4, height * 1 / 2, width * 3 / 4, height * 1 / 2);

    // White Lines (not arrows)
    gc.setLineDashes(1);

    //        //topleft
    //        gc.strokeLine(width * 3 / 16, height * 5 / 16, width * 4 / 16, height * 5 / 16);
    //        //far right
    //        gc.strokeLine(width * 15 / 16, height * 9 / 16, width * 15 / 16, height * 10 / 16);
    //        //far left
    //        gc.strokeLine(width * 1 / 16, height * 9 / 16, width * 1 / 16, height * 14 / 16);
    //        //bottom smallest
    //        gc.strokeLine(width * 4 / 16, height * 13 / 16, width * 4 / 16, height * 14 / 16);
    //        //bottom second smallest
    //        gc.strokeLine(width * 3 / 16, height * 11 / 16, width * 3 / 16, height * 13 / 16);

    //        //show parameter of runway
    //        //far right
    //        drawArrow(gc, width * 15 / 16, height * 10 / 16, width, height * 10 / 16);
    //        drawArrow(gc, width * 15 / 16, height * 10 / 16, width, height * 10 / 16);
    //        //the longest
    //        drawArrow(gc, width * 11.5 / 16, height * 0, width * 11.5 / 16, height * 8 / 16);
    //        //The short one in the upper vertical line
    //        drawArrow(gc, width * 4 / 16, height * 5 / 16, width * 4 / 16, height * 8 / 16);
    // Bottom horizontal line
    //        drawArrow(gc, width * 1 / 16, height * 14 / 16, width * 4 / 16, height * 14 / 16);
    //        drawArrow(gc, width * 0, height * 10 / 16, width * 1 / 16, height * 10 / 16);
    //        drawArrow(gc, width * 1 / 16, height * 12 / 16, width * 3 / 16, height * 12 / 16);
    // drawArrow(gc, width * 6 / 16, height * 8 / 16, width * 6 / 16, height * 13 / 16);

    if (sideState.equals("away") && isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 8 / 16, height * 5 / 16, width * 15 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 6 / 16, height * 11 / 16, width * 15 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 6 / 16, height * 13 / 16, width * 15.5 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 6 / 16, height * 15 / 16, width * 16 / 16, height * 15 / 16);
      if (obstacle) {
        paintObstacle(5);
      }
    } else if (sideState.equals("away") && !isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 1 / 16, height * 5 / 16, width * 8 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 1 / 16, height * 11 / 16, width * 10 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 0.5 / 16, height * 13 / 16, width * 10 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 0 / 16, height * 15 / 16, width * 10 / 16, height * 15 / 16);
      if (obstacle) {
        paintObstacle(11);
      }
    } else if (sideState.equals("toward") && !isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 8 / 16, height * 5 / 16, width * 15 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 6 / 16, height * 11 / 16, width * 15 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 6 / 16, height * 13 / 16, width * 15 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 6 / 16, height * 15 / 16, width * 15 / 16, height * 15 / 16);
      if (obstacle) {
        paintObstacle(5);
      }
    } else if (sideState.equals("toward") && isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 1 / 16, height * 5 / 16, width * 8 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 1 / 16, height * 11 / 16, width * 10 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 1 / 16, height * 13 / 16, width * 10 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 1 / 16, height * 15 / 16, width * 10 / 16, height * 15 / 16);
      if (obstacle) {
        paintObstacle(11);
      }
    }

    applyBarcode();
    applyDirection();
    // paintObstacle();
    // updateLabels();
  }

  public void applyBarcode() {
    double leftWidth = width * 6 / 64;
    double rightWidth = width * 53 / 64;
    GraphicsContext gc = canvas.getGraphicsContext2D();

    gc.fillRect(leftWidth, height * 140 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 137 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 134 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 131 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 128 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 125 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 122 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 119 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(leftWidth, height * 116 / 256, width * 5 / 64, height * 2 / 256);

    gc.fillRect(rightWidth, height * 140 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 137 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 134 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 131 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 128 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 125 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 122 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 119 / 256, width * 5 / 64, height * 2 / 256);
    gc.fillRect(rightWidth, height * 116 / 256, width * 5 / 64, height * 2 / 256);
  }

  public void selectAircraft(Aircraft aircraft) {
    this.aircraftType = aircraft;

    if (!this.aircraftTypeComboBox.getItems().contains("OTHR (Ad-Hoc)")) {
      this.aircraftTypeComboBox.getItems().remove("OTHR (Ad-Hoc)");
    }

    if (aircraft == null) {
      this.aircraftTypeComboBox.setValue("Select Type...");
      return;
    }

    this.aircraftTypeComboBox.setValue(
        String.format("%s (BD=%dm)", aircraft.getModel(), aircraft.getBlastDist()));
  }

  public void logout(Event event) {
    try {
      // confirmation dialog
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Logout");
      alert.setHeaderText("You are about to logout.");
      alert.setContentText("Click OK to logout.");

      System.err.println(
          String.format(
              "User : %s, %s, %s, %s",
              currentUser.getUsername(),
              currentUser.getFirstName(),
              currentUser.getLastName(),
              currentUser.getEmail()));

      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == ButtonType.OK) {
        Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
        System.out.println("Logging out");
        FXMLLoader fxmlLoader =
            new FXMLLoader(RunwayRedeclarationApp.class.getResource("ui/login.fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), windowWidth, windowHeight);
        String css = RunwayRedeclarationApp.class.getResource("ui/runway.css").toExternalForm();
        newScene.getStylesheets().add(css);
        stage.setScene(newScene);
        stage.show();
      }
    } catch (IOException e) {
      System.err.println("Could not log out.");
    }
  }

  public void openAdminPanel(Event event) {
    try {
      if (currentUser.getAdminAccess()) {
        AdminView adminView = new AdminView();
        adminView.createAdminScene(event, this);
      } else {
        Alert alertNotAdmin = new Alert(Alert.AlertType.WARNING);
        alertNotAdmin.setTitle("Cannot access Admin Panel");
        alertNotAdmin.setHeaderText("You must be Admin to access the Admin Panel.");
        alertNotAdmin.setContentText("Click OK to return.");
        alertNotAdmin.show();
      }
    } catch (IOException ioe) {
      System.err.println("Could not load admin scene");
      ioe.printStackTrace();
    }
  }

  public static void setUser(User user) {
    currentUser = user;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void applyDirection() {
    leftDir.setRotate(90);
    rightDir.setRotate(270);

    if (runway == null) {
      leftDir.setText("");
      rightDir.setText("");
      return;
    }

    if (runway.getBearing() <= 180) {
      leftDir.setText(runway.getRunwayDesignator());
      rightDir.setText(runway.getOpposite());
    } else {
      leftDir.setText(runway.getOpposite());
      rightDir.setText(runway.getRunwayDesignator());
    }
  }

  /*
    public void applyLandingDistances() {
         landDist.setText("500m");
         sideMiddle.setText("60m");
         minResa.setText("RESA");
     }
  */
  public void applyTowardDistances() {
    toraTodaAsda.setText("500m");
    buffer.setText("60m");
    minResa.setText("RESA");
    towardHeight.setText("h");
    heightTimesFifty.setText("hx50");

    towardLda.setText("560m");
    towardtora.setText("23m");
    towardtoda.setText("23m");
    towardasda.setText("23m");
  }

  public void applyAwayDistances() {
    awayHeight.setText("h");
    awayLda.setText("awayLda");
    tora.setText("tora");
    asda.setText("asda");
    toda.setText("toda");
  }

  public void resetDistances() {
    // numbers
    leftDir.setText("");
    rightDir.setText("");

    // topDown
    firstVert.setText("");
    secondVert.setText("");
    thirdVert.setText("");
    rightHorizontal.setText("");
    leftHorizontal.setText("");
    largeHorizontal.setText("");
    smallHorizontal.setText("");

    // toward
    toraTodaAsda.setText("");
    buffer.setText("");
    minResa.setText("");
    towardHeight.setText("");
    heightTimesFifty.setText("");

    towardLda.setText("");
    towardtora.setText("");
    towardtoda.setText("");
    towardasda.setText("");

    // away
    awayHeight.setText("");
    awayLda.setText("");
    tora.setText("");
    toda.setText("");
    asda.setText("");
  }

  public void sideCanvasRunway() {
    resetDistances();
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setLineWidth(2);
    gc.clearRect(0, 0, width, height);

    // background
    gc.setFill(backgroundColour);
    gc.fillRect(0, 0, width, height);

    // runway rectangle
    gc.setFill(Color.GRAY);
    gc.fillRect(width * 1 / 16, height * 9 / 16, width * 14 / 16, height * 1 / 16);

    // runway outline
    gc.setStroke(Color.BLACK);
    gc.strokeRect(width * 1 / 16, height * 9 / 16, width * 14 / 16, height * 1 / 16);
  }

  public void awaySideView() {
    side = true;
    sideState = "away";
    sideCanvasRunway();
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(Color.WHITE);

    if (sideState.equals("away") && isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 8 / 16, height * 5 / 16, width * 15 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 6 / 16, height * 11 / 16, width * 15 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 6 / 16, height * 13 / 16, width * 15.5 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 6 / 16, height * 15 / 16, width * 16 / 16, height * 15 / 16);

      // bottom diagonal line
      gc.setLineDashes(10);
      gc.strokeLine(width * 1 / 16, height * 6 / 16, width * 6 / 16, height * 9 / 16);
      gc.setLineDashes(1);

      // upper diagonal line
      gc.strokeLine(width * 8 / 16, height * 9 / 16, width * 1 / 16, height * 5.5 / 16);
      if (obstacle) {
        paintObstacle(4);
      }
    } else if (sideState.equals("away") && !isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 1 / 16, height * 5 / 16, width * 8 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 1 / 16, height * 11 / 16, width * 10 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 0.5 / 16, height * 13 / 16, width * 10 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 0 / 16, height * 15 / 16, width * 10 / 16, height * 15 / 16);

      // bottom diagonal line
      gc.setLineDashes(10);
      gc.strokeLine(width * 10 / 16, height * 9 / 16, width * 15 / 16, height * 6 / 16);
      gc.setLineDashes(1);

      // upper diagonal line
      gc.strokeLine(width * 8 / 16, height * 9 / 16, width * 15 / 16, height * 5.5 / 16);
      if (obstacle) {
        paintObstacle(12);
      }
    }

//        // bottom vertical dotted line
//        gc.setLineDashes(4);
//        gc.strokeLine(width * 7 / 16, height * 10 / 16, width * 7 / 16, height * 15 / 16);
//        gc.setLineDashes(1);
//
//        // top vertical dotted line
//        gc.setLineDashes(4);
//        gc.strokeLine(width * 6 / 16, height * 9 / 16, width * 6 / 16, height * 5 / 16);
//        gc.setLineDashes(1);

//        // lda arrow
//        drawArrow(gc, width * 6 / 16, height * 5 / 16, width * 15 / 16, height * 5 / 16);
//
//        // tora arrow
//        drawArrow(gc, width * 7 / 16, height * 11 / 16, width * 15 / 16, height * 11 / 16);
//
//        // toda arrow
//        drawArrow(gc, width * 7 / 16, height * 13 / 16, width * 15.5 / 16, height * 13 / 16);
//
//        // asda arrow
//        drawArrow(gc, width * 7 / 16, height * 15 / 16, width * 16 / 16, height * 15 / 16);

//        // upper right arrow (height)
//        drawArrow(gc, width * 1 / 16, height * 6 / 16, width * 1 / 16, height * 9 / 16);
//
//        // upper and bottom horizontal lines
//        gc.strokeLine(width * 0.5 / 16, height * 6 / 16, width * 1 / 16, height * 6 / 16);
//        gc.strokeLine(width * 0.5 / 16, height * 9 / 16, width * 1 / 16, height * 9 / 16);

//        // far right vertical line
//        gc.strokeLine(width * 15 / 16, height * 10 / 16, width * 15 / 16, height * 11 / 16);

//        // bottom diagonal line
//        gc.setLineDashes(10);
//        gc.strokeLine(width * 1 / 16, height * 6 / 16, width * 5 / 16, height * 9 / 16);
//        gc.setLineDashes(1);

    // applyAwayDistances();
    // paintObstacle();

    // resetDistances();
    //updateLabels();
  }

  public void towardSideView() {
    side = true;
    sideState = "toward";
    sideCanvasRunway();
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(Color.WHITE);

    if (sideState.equals("toward") && !isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 8 / 16, height * 5 / 16, width * 15 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 6 / 16, height * 11 / 16, width * 15 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 6 / 16, height * 13 / 16, width * 15 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 6 / 16, height * 15 / 16, width * 15 / 16, height * 15 / 16);

      // bottom diagonal line
      gc.setLineDashes(10);
      gc.strokeLine(width * 1 / 16, height * 6 / 16, width * 6 / 16, height * 9 / 16);
      gc.setLineDashes(1);

      // upper diagonal line
      gc.strokeLine(width * 8 / 16, height * 9 / 16, width * 1 / 16, height * 5.5 / 16);
      if (obstacle) {
        paintObstacle(4);
      }
    } else if (sideState.equals("toward") && isFromLeft()) {
      // lda arrow
      drawArrow(gc, width * 1 / 16, height * 5 / 16, width * 8 / 16, height * 5 / 16);

      // tora arrow
      drawArrow(gc, width * 1 / 16, height * 11 / 16, width * 10 / 16, height * 11 / 16);

      // toda arrow
      drawArrow(gc, width * 1 / 16, height * 13 / 16, width * 10 / 16, height * 13 / 16);

      // asda arrow
      drawArrow(gc, width * 1 / 16, height * 15 / 16, width * 10 / 16, height * 15 / 16);

      // bottom diagonal line
      gc.setLineDashes(10);
      gc.strokeLine(width * 10 / 16, height * 9 / 16, width * 15 / 16, height * 6 / 16);
      gc.setLineDashes(1);

      // upper diagonal line
      gc.strokeLine(width * 8 / 16, height * 9 / 16, width * 15 / 16, height * 5.5 / 16);
      if (obstacle) {
        paintObstacle(12);
      }
    }

//        // left vertical line down
//        gc.setStroke(Color.WHITE);
//        gc.strokeLine(width * 1 / 16, height * 10 / 16, width * 1 / 16, height * 11 / 16);
//
//        // left dotted vertical line down
//        gc.setLineDashes(4);
//        gc.strokeLine(width * 1 / 16, height * 11 / 16, width * 1 / 16, height * 13 / 16);
//        gc.setLineDashes(1);
//
//        // middle two vertical lines down
//        gc.strokeLine(width * 9 / 16, height * 10 / 16, width * 9 / 16, height * 11 / 16);
//
//        gc.strokeLine(width * 10 / 16, height * 10 / 16, width * 10 / 16, height * 11 / 16);
//        gc.setLineDashes(4);
//        gc.strokeLine(width * 10 / 16, height * 11 / 16, width * 10 / 16, height * 13 / 16);
//        gc.setLineDashes(1);
//
//        // right vertical line down
//        gc.strokeLine(width * 12 / 16, height * 10 / 16, width * 12 / 16, height * 11 / 16);
//
//        // toraTodaAsda arrow
//        drawArrow(gc, width * 1 / 16, height * 11 / 16, width * 9 / 16, height * 11 / 16);
//
//        // buffer arrowed line
//        drawArrow(gc, width * 9 / 16, height * 11 / 16, width * 10 / 16, height * 11 / 16);
//
//        // RESA arrow
//        drawArrow(gc, width * 10 / 16, height * 11 / 16, width * 12 / 16, height * 11 / 16);
//
//        // upper right arrow (height)
//        drawArrow(gc, width * 15 / 16, height * 6 / 16, width * 15 / 16, height * 9 / 16);
//
//        // upper and bottom horizontal lines
//        gc.strokeLine(width * 15 / 16, height * 6 / 16, width * 15.5 / 16, height * 6 / 16);
//        gc.strokeLine(width * 15 / 16, height * 9 / 16, width * 15.5 / 16, height * 9 / 16);
//
//        // add longer far right line
//        gc.strokeLine(width * 15 / 16, height * 10 / 16, width * 15 / 16, height * 13 / 16);
//
//        // far right horizontal arrow
//        drawArrow(gc, width * 10 / 16, height * 13 / 16, width * 15 / 16, height * 13 / 16);
//
//        // far right horizontal arrow
//        drawArrow(gc, width * 1 / 16, height * 13 / 16, width * 10 / 16, height * 13 / 16);
//
//        // bottom diagonal line
//        gc.setLineDashes(10);
//        gc.strokeLine(width * 10 / 16, height * 9 / 16, width * 15 / 16, height * 6 / 16);
//        gc.setLineDashes(1);
//
//        // upper diagonal line
//        gc.strokeLine(width * 9 / 16, height * 9 / 16, width * 15 / 16, height * 5.5 / 16);

    // applyTowardDistances();
    // paintObstacle();
    //updateLabels();
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
    gc.setFill(Color.WHITE);
    if (x1 == x2) {
      double[] a = {x1, x1 - (width * 1 / 64), x1 + (width * 1 / 64)};
      double[] b = {y1, y1 + (height * 2 / 64), y1 + (height * 2 / 64)};
      gc.fillPolygon(a, b, 3);

      gc.strokeLine(x1, y1 + width * 1 / 128, x2, y2 - width * 1 / 128);

      double[] c = {x2, x2 - (width * 1 / 64), x2 + (width * 1 / 64)};
      double[] d = {y2, y2 - (height * 2 / 64), y2 - (height * 2 / 64)};
      gc.fillPolygon(c, d, 3);
    } else if (y1 == y2) {
      double[] a = {x1, x1 + (width * 1 / 64), x1 + (width * 1 / 64)};
      double[] b = {y1, y1 - (height * 2 / 64), y1 + (height * 2 / 64)};
      gc.fillPolygon(a, b, 3);

      gc.strokeLine(x1, y1, x2, y2);

      double[] c = {x2, x2 - (width * 1 / 64), x2 - (width * 1 / 64)};
      double[] d = {y2, y2 - (height * 2 / 64), y2 + (height * 2 / 64)};
      gc.fillPolygon(c, d, 3);
    }
  }

  public void selectRunway() {
    // sort out runway list
    if (airportSelectionBox.getValue() != null) {
      AirportRepository apr = new AirportRepository();
      List<Runway> rws = apr.getRunwaysFromIcao(airportSelectionBox.getValue());
      ObservableList<String> newRunwayDesignators = FXCollections.observableArrayList();
      // add all runways from db with that ICAO to a list, so that we can attach it to the dropdown)
      for (Runway rw : rws) {
        newRunwayDesignators.add(rw.getRunwayDesignator());
      }
      // connect runway list to runway combo box
      runwaySelectionBox.setItems(newRunwayDesignators);
    }
  }

  public void selectAirport() {
    AirportRepository apr = new AirportRepository();
    // if(airportSelectionBox.getValue() == null) {
    // sort out airport list
    List<Runway> aps = apr.getAirports();
    ObservableList<String> newAirports = FXCollections.observableArrayList();
    // add airports to airport list only if it's unique to the list (so no duplicated in drop down)
    for (Runway ap : aps) {
      if (!newAirports.contains(ap.getAirport())) {
        newAirports.add(ap.getAirport());
      }
    }
    airportSelectionBox.setItems(newAirports);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    width = canvas.getWidth();
    height = canvas.getHeight();

    // Rotate button should be disabled to start with because the app starts on side view
    // changed (top down start)
    rotateButton.setDisable(false);

    // Reset the values of the obstacle parameters
    resetButton.setOnAction(
        e -> {
          resetDistances();
          obstWidthSpinner.getValueFactory().setValue(0);
          obstHeightSpinner.getValueFactory().setValue(0);
          obstDistSpinner.getValueFactory().setValue(0);
          obstacle = false;
          if (side == false) {
            topDown();
          } else if (sideState == "toward") {
            towardSideView();
          } else {
            awaySideView();
          }
        });

    // Scale text listener to listen for when the value of the slider changes
    textScaleSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (Math.floor(oldValue.doubleValue())
                  != Math.floor(newValue.doubleValue())) { // Check if the value is a whole number
                onSliderChanged(oldValue.doubleValue(), newValue.doubleValue());
              }
            });

    caseComboBox.setItems(
        FXCollections.observableArrayList("Towards Obstacle", "Away from Obstacle"));
    caseComboBox.setValue("Towards Obstacle");

    runwaySelectionBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              if (airportSelectionBox.getValue() != null && runwaySelectionBox.getValue() != null) {
                AirportRepository apr = new AirportRepository();
                runway =
                    apr.getCurrentRunway(
                            airportSelectionBox.getValue(), runwaySelectionBox.getValue())
                        .get(0);
              }
              if (side == false) {
                topDown();
              } else if (sideState == "toward") {
                towardSideView();
              } else {
                awaySideView();
              }
              if (obstacle) {
                updateLabels();
              }
            });
    var adHoc = "Ad-hoc Aircraft Type";
    //aircraftTypeComboBox.setItems(FXCollections.observableArrayList(adHoc));

    aircraftTypeComboBox.setOnMouseClicked(e -> {
      List<Aircraft> aircraftList = aircraftRepository.getAllAircrafts();
      ObservableList<String> foundAircrafts = FXCollections.observableArrayList();
      //add airports to airport list only if it's unique to the list (so no duplicated in drop down)
      for (Aircraft aircraft : aircraftList) {
        if (!foundAircrafts.contains(aircraft.getModel())) {
          foundAircrafts.add(
              String.format("%s (BD=%dm)", aircraft.getModel(), aircraft.getBlastDist()));
        }
      }
      aircraftTypeComboBox.setItems(foundAircrafts);
    });

    aircraftTypeComboBox.setOnAction(
        e -> {
          if (!aircraftTypeComboBox.getValue().equals(adHoc)) {
            System.out.println(aircraftTypeComboBox.getValue());
            selectAircraft(aircraftRepository.getAircraftFromModel(aircraftTypeComboBox.getValue()
                .substring(0, aircraftTypeComboBox.getValue().indexOf(" (BD="))));
          }
        });

    runwaySelectionBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              if (airportSelectionBox.getValue() != null && runwaySelectionBox.getValue() != null) {
                AirportRepository apr = new AirportRepository();
                runway =
                    apr.getCurrentRunway(
                            airportSelectionBox.getValue(), runwaySelectionBox.getValue())
                        .get(0);

                selectRunwayLabel.setText(
                    String.format("%s / %s", runway.getAirport(), runway.getRunwayDesignator()));
                calculateButton.setDisable(false);
              } else {
                selectRunwayLabel.setText("Select runway!");
                calculateButton.setDisable(true);
              }
              if (side == false) {
                applyDirection();
                if (obstacle) {
                  topDown();
                  updateLabels();
                }
              }
            });

    // Rotates the top-down view
    rotateButton.setOnAction(
        e -> {
          if (airportSelectionBox.getValue() != null && runwaySelectionBox.getValue() != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            double angle = runway.getBearing() - 90; // GET ANGLE FROM DATABASE
            if (!rotated) {
              System.out.println("Rotated to true bearing");
              rotated = true;
            } else {
              System.out.println("Rotated to horizontal view");
              rotated = false;
            }
            rotateRunway(gc, angle);
          }
        });

    switchViewButton.setOnAction(
        e -> {
          if (rotated) {
            System.out.println("Rotated to horizontal view");
            rotated = false;
            GraphicsContext gc = canvas.getGraphicsContext2D();
            double angle = runway.getBearing() - 90;
            rotateRunway(gc, angle);
          }
          if (side) {
            side = false;
            if (caseComboBox.getValue().equals("Towards Obstacle")) {
              sideState = "toward";
            } else {
              sideState = "away";
            }
            rotateButton.setDisable(false);
            topDown();
          } else {
            side = true;
            rotateButton.setDisable(true);
            // detect what state the runway is set to in dropdown
            if (caseComboBox.getValue().equals("Towards Obstacle")) {
              sideState = "toward";
              towardSideView();
            } else {
              sideState = "away";
              awaySideView();
            }
          }
          resetDistances();
          if (obstacle) {
            updateLabels();
          }
          if (!side) {
            applyDirection();
          }
        });

    protan.setOnAction(
        e -> {
          if (!backgroundColour.equals(Color.web("#FE0000"))) {
            backgroundColour = Color.web("#FE0000");
            protan.setStyle("-fx-background-color: orange;");
            tritan.setStyle(baseStyle);
            addNotification("Enabled protan colourblind mode", Color.YELLOW);
          } else {
            protan.setStyle(baseStyle);
            backgroundColour = Color.MEDIUMPURPLE;
            addNotification("Disabled protan colourblind mode", Color.YELLOW);
          }
          if (side == false) {
            if (rotated) {
              GraphicsContext gc = canvas.getGraphicsContext2D();
              double angle = runway.getBearing() - 90;
              rotated = false;
              rotateRunway(gc, angle);
              rotated = true;
              rotateRunway(gc, angle);
            } else {
              topDown();
            }
          } else if (sideState.equals("away")) {
            awaySideView();
          } else {
            towardSideView();
          }
          if (obstacle) {
            updateLabels();
          }
        });

    tritan.setOnAction(
        e -> {
          if (!backgroundColour.equals(Color.web("#5C0063"))) {
            backgroundColour = Color.web("#5C0063");
            protan.setStyle(baseStyle);
            tritan.setStyle("-fx-background-color: orange;");
            addNotification("Enabled tritan colourblind mode", Color.YELLOW);
          } else {
            backgroundColour = Color.MEDIUMPURPLE;
            tritan.setStyle(baseStyle);
            addNotification("Disabled tritan colourblind mode", Color.YELLOW);
          }
          if (side == false) {
            if (rotated) {
              GraphicsContext gc = canvas.getGraphicsContext2D();
              double angle = runway.getBearing() - 90;
              rotated = false;
              rotateRunway(gc, angle);
              rotated = true;
              rotateRunway(gc, angle);
            } else {
              topDown();
            }
          } else if (sideState.equals("away")) {
            awaySideView();
          } else {
            towardSideView();
          }
          if (obstacle) {
            updateLabels();
          }
        });

    calculateButton.setOnAction(
        e -> {
          var obst = new Obstacle(obstHeightSpinner.getValue(), obstWidthSpinner.getValue());

          CalculationCase calcCase;
          obstacle = true;
          if (caseComboBox.getValue().equals("Towards Obstacle")) {
            calcCase = CalculationCase.TOWARDS_OBSTACLE;
            sideState = "toward";
            if (side == true) {
              towardSideView();
            } else {
              topDown();
            }
          } else {
            calcCase = CalculationCase.AWAY_FROM_OBSTACLE;
            sideState = "away";
            if (side == true) {
              awaySideView();
            } else {
              topDown();
            }
          }

          var partialCalc =
              new PartialCalculation(
                  runway, aircraftType, calcCase, obst, obstDistSpinner.getValue());

          calc = partialCalc.calculate();

          toraBox.setText(calc.getResult().tora() + "m");
          todaBox.setText(calc.getResult().toda() + "m");
          asdaBox.setText(calc.getResult().asda() + "m");
          ldaBox.setText(calc.getResult().lda() + "m");

          Color spinnerColour = Color.GREEN;

          if (calc.getResult().tora() < 0.4 * runway.getTora()
              || calc.getResult().toda() < 0.4 * runway.getToda()
              || calc.getResult().asda() < 0.4 * runway.getAsda()
              || calc.getResult().lda() < 0.4 * runway.getLda()) {
            addNotification(
                "Runway unusually short, check obstacle dimensions and runway direction",
                Color.RED);
            spinnerColour = Color.RED;
          }

          Color finalColor = spinnerColour;

          PseudoClass animateSpinner = PseudoClass.getPseudoClass("animate");
          obstHeightSpinner.pseudoClassStateChanged(animateSpinner, true);

          Animation animation =
              new Transition() {
                {
                  setCycleDuration(Duration.millis(500));
                  setInterpolator(Interpolator.EASE_OUT);
                }

                @Override
                protected void interpolate(double v) {
                  Color colourTransition =
                      new Color(
                          finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), 1 - v);
                  // Color colourTransition = finalColour.interpolate(finalColour, 1 - v);
                  obstWidthSpinner.setBackground(new Background(new BackgroundFill(
                      new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xBBBBBB", 1.0)),
                          new Stop(1, Color.web("0xCFCFCF", 1.0))), new CornerRadii(3),
                      new Insets(0, 0, 0, 0)), new BackgroundFill(
                      new LinearGradient(0, 0, 0, 5, false, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xE8E8E8", 1.0)),
                          new Stop(1, Color.web("0xFFFFFF", 1))),
                      new CornerRadii(2, 0, 0, 2, false), new Insets(1, 0, 1, 1)),
                      new BackgroundFill(colourTransition, new CornerRadii(2, 0, 0, 2, false),
                          new Insets(1, 24, 1, 1))));
                  obstHeightSpinner.setBackground(new Background(new BackgroundFill(
                      new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xBBBBBB", 1.0)),
                          new Stop(1, Color.web("0xCFCFCF", 1.0))), new CornerRadii(3),
                      new Insets(0, 0, 0, 0)), new BackgroundFill(
                      new LinearGradient(0, 0, 0, 5, false, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xE8E8E8", 1.0)),
                          new Stop(1, Color.web("0xFFFFFF", 1))),
                      new CornerRadii(2, 0, 0, 2, false), new Insets(1, 0, 1, 1)),
                      new BackgroundFill(colourTransition, new CornerRadii(2, 0, 0, 2, false),
                          new Insets(1, 24, 1, 1))));
                  obstDistSpinner.setBackground(new Background(new BackgroundFill(
                      new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xBBBBBB", 1.0)),
                          new Stop(1, Color.web("0xCFCFCF", 1.0))), new CornerRadii(3),
                      new Insets(0, 0, 0, 0)), new BackgroundFill(
                      new LinearGradient(0, 0, 0, 5, false, CycleMethod.NO_CYCLE,
                          new Stop(0, Color.web("0xE8E8E8", 1.0)),
                          new Stop(1, Color.web("0xFFFFFF", 1))),
                      new CornerRadii(2, 0, 0, 2, false), new Insets(1, 0, 1, 1)),
                      new BackgroundFill(colourTransition, new CornerRadii(2, 0, 0, 2, false),
                          new Insets(1, 24, 1, 1))));
                }
              };
          animation.play();

          breakdownButton.setDisable(false);
          updateLabels();
        });

    ObservableList<String> obstacles = FXCollections.observableArrayList("Custom Obstacle");
    Arrays.stream(Obstacles.values()).forEach(o -> obstacles.add(o.toString()));
    predefObstComboBox.setItems(obstacles);
    predefObstComboBox.setValue("Custom Obstacle");

    predefObstComboBox.valueProperty().addListener((observableValue, s, t1) -> {
      updateObstacle(t1);
      addNotification("Obstacle updated: " + t1, Color.YELLOW);
    });

    caseComboBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              addNotification("Runway direction updated: " + t1, Color.YELLOW);
              if (side == false) {
                topDown();
              } else if (sideState == "toward") {
                towardSideView();
              } else {
                awaySideView();
              }
              if (obstacle) {
                updateLabels();
              }
            });
    aircraftTypeComboBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              addNotification("Aircraft type updated: " + t1, Color.YELLOW);
            });
    obstWidthSpinner
        .valueProperty()
        .addListener(
            (observableValue, integer, t1) -> {
              addNotification("Obstacle width updated: " + t1.toString() + "m", Color.YELLOW);
            });
    obstHeightSpinner
        .valueProperty()
        .addListener(
            (observableValue, integer, t1) -> {
              addNotification("Obstacle height updated: " + t1.toString() + "m", Color.YELLOW);
            });
    obstDistSpinner
        .valueProperty()
        .addListener(
            (observableValue, integer, t1) -> {
              addNotification("Obstacle distance updated: " + t1.toString() + "m", Color.YELLOW);
            });

    PseudoClass minValue = PseudoClass.getPseudoClass("minValue");
    PseudoClass maxValue = PseudoClass.getPseudoClass("maxValue");
    PseudoClass disabled = PseudoClass.getPseudoClass("disabled");

    obstHeightSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 100));
    obstWidthSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 100));

    obstHeightSpinner.pseudoClassStateChanged(minValue, true);
    obstWidthSpinner.pseudoClassStateChanged(minValue, true);
    obstDistSpinner.pseudoClassStateChanged(minValue, true);

    obstHeightSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
      obstHeightSpinner.pseudoClassStateChanged(minValue,
          t1 == ((IntegerSpinnerValueFactory) obstHeightSpinner.getValueFactory()).getMin());
      obstHeightSpinner.pseudoClassStateChanged(maxValue,
          t1 == ((IntegerSpinnerValueFactory) obstHeightSpinner.getValueFactory()).getMax());
      if (predefObstComboBox.getValue() != "Custom Obstacle"
          && obstHeightSpinner.getValue() != Obstacles.valueOf(predefObstComboBox.getValue())
          .getHeight()) {
        predefObstComboBox.setValue("Custom Obstacle");
      }
//      obstHeightSpinner.pseudoClassStateChanged(disabled,
//          ((IntegerSpinnerValueFactory) obstHeightSpinner.getValueFactory()).getMax()
//              == ((IntegerSpinnerValueFactory) obstHeightSpinner.getValueFactory()).getMin());
    });

    obstWidthSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
      obstWidthSpinner.pseudoClassStateChanged(minValue,
          t1 == ((IntegerSpinnerValueFactory) obstWidthSpinner.getValueFactory()).getMin());
      obstWidthSpinner.pseudoClassStateChanged(maxValue,
          t1 == ((IntegerSpinnerValueFactory) obstWidthSpinner.getValueFactory()).getMax());
      if (predefObstComboBox.getValue() != "Custom Obstacle"
          && obstWidthSpinner.getValue() != Obstacles.valueOf(predefObstComboBox.getValue())
          .getWidth()) {
        predefObstComboBox.setValue("Custom Obstacle");
      }
//      obstWidthSpinner.pseudoClassStateChanged(disabled,
//          ((IntegerSpinnerValueFactory) obstWidthSpinner.getValueFactory()).getMax()
//              == ((IntegerSpinnerValueFactory) obstWidthSpinner.getValueFactory()).getMin());
    });

    obstDistSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
      obstDistSpinner.pseudoClassStateChanged(minValue,
          t1 == ((IntegerSpinnerValueFactory) obstDistSpinner.getValueFactory()).getMin());
      obstDistSpinner.pseudoClassStateChanged(maxValue,
          t1 == ((IntegerSpinnerValueFactory) obstDistSpinner.getValueFactory()).getMax());
    });

    PseudoClass animateSpinner = PseudoClass.getPseudoClass("animate");

    obstHeightSpinner.backgroundProperty().addListener((observableValue, background, t1) -> {
      obstHeightSpinner.pseudoClassStateChanged(animateSpinner,
          !t1.getFills().get(0).getFill().equals(Color.web("0xFF0000", 0)));
    });

    obstWidthSpinner.backgroundProperty().addListener((observableValue, background, t1) -> {
      obstWidthSpinner.pseudoClassStateChanged(animateSpinner,
          !t1.getFills().get(0).getFill().equals(Color.web("0xFF0000", 0)));
    });

    obstDistSpinner.backgroundProperty().addListener((observableValue, background, t1) -> {
      obstDistSpinner.pseudoClassStateChanged(animateSpinner,
          !t1.getFills().get(0).getFill().equals(Color.web("0xFF0000", 0)));
    });

    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    UnaryOperator<TextFormatter.Change> filter = f -> {
      if (f.isContentChange()) {
        ParsePosition parsePosition = new ParsePosition(0);
        // NumberFormat evaluates the beginning of the text
        numberFormat.parse(f.getControlNewText(), parsePosition);
        if (parsePosition.getIndex() < f.getControlNewText().length()) {
          // reject parsing the complete text failed
          return null;
        } else if (parsePosition.getIndex() == 0) {
          f.setText("0");
          f.setCaretPosition(1);
        }
      }
      return f;
    };
    TextFormatter<Integer> heightFormatter = new TextFormatter<Integer>(
        new IntegerStringConverter(), 0, filter);
    obstHeightSpinner.setEditable(true);
    obstHeightSpinner.getEditor().setTextFormatter(heightFormatter);
    TextFormatter<Integer> widthFormatter = new TextFormatter<Integer>(new IntegerStringConverter(),
        0, filter);
    obstWidthSpinner.setEditable(true);
    obstWidthSpinner.getEditor().setTextFormatter(widthFormatter);
    TextFormatter<Integer> distFormatter = new TextFormatter<Integer>(new IntegerStringConverter(),
        0, filter);
    obstDistSpinner.setEditable(true);
    obstDistSpinner.getEditor().setTextFormatter(distFormatter);

    runwaySelectionBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              addNotification("New runway loaded: " + t1, Color.YELLOW);
            });

    airportSelectionBox
        .valueProperty()
        .addListener(
            (observableValue, s, t1) -> {
              addNotification("New airport loaded: " + t1, Color.YELLOW);
            });

//    saveButton.setOnAction(e -> {
//      addNotification("Saved current configuration", Color.YELLOW);
//    });
//
//    printButton.setOnAction(e -> {
//      addNotification("Printed redeclared values and calculations", Color.YELLOW);
//    });

    notificationsContent
        .heightProperty()
        .addListener(
            (observableValue, number, t1) -> {
              notificationsPane.setVvalue(notificationsPane.getVmax());
            });
  }

  public void importBox() {
    ImportView importView = new ImportView();
    Calculation calculation = importView.createImportBox();
    if (calculation != null) {
      Obstacle obstacle = calculation.getObstacle();

      toraBox.setText(calculation.getTora());
      todaBox.setText(calculation.getToda());
      asdaBox.setText(calculation.getAsda());
      ldaBox.setText(calculation.getLda());

      obstDistSpinner.getValueFactory().setValue(obstacle.getDistanceThreshold());
      obstWidthSpinner.getValueFactory().setValue(obstacle.getLength());
      obstHeightSpinner.getValueFactory().setValue(obstacle.getHeight());

      airportSelectionBox.setValue(calculation.getAirport());
      runwaySelectionBox.setValue(calculation.getRunwayDesignator());
      caseComboBox.setValue(calculation.getTakeOffDirection());
      selectAircraft(new Aircraft(calculation.getAircraft().getModel(),
          calculation.getAircraft().getBlastDist()));
    }
  }

  public void exportBox() {
    ExportView exportView = new ExportView();
    try {
      Calculation calculation =
          new Calculation(
              airportSelectionBox.getValue(),
              runwaySelectionBox.getValue(),
              toraBox.getText(),
              todaBox.getText(),
              asdaBox.getText(),
              ldaBox.getText(),
              new Obstacle(
                  obstHeightSpinner.getValue(),
                  obstWidthSpinner.getValue(),
                  obstDistSpinner.getValue()),
              currentUser,
              caseComboBox.getValue(),
              aircraftType);
      exportView.createExportBox(calculation);
    } catch (Exception e) {
      System.err.println("No blast area probably");
    }
  }

  @FXML
  private void showBreakdown() {
    new BreakdownController(this.calc, this);
  }

  //    private void updateLabels() {
  //        if (side == true) {
  //            if (calc.getCalculationCase() == TOWARDS_OBSTACLE && sideState.equals("toward")) {
  //                //sideState = "toward";
  //                toraTodaAsda.setText(calc.getResult().tora() + "m");
  //                towardLda.setText(calc.getResult().lda() + "m");
  //                towardHeight.setText(calc.getObstacle().getHeight() + "m");
  //            } else if (calc.getCalculationCase() == AWAY_FROM_OBSTACLE &&
  // sideState.equals("away")) {
  //                //sideState = "away";
  //                tora.setText(calc.getResult().tora() + "m");
  //                toda.setText(calc.getResult().toda() + "m");
  //                asda.setText(calc.getResult().asda() + "m");
  //                awayLda.setText(calc.getResult().lda() + "m");
  //            }
  //        } else {
  //            //update topdown distances
  //            applyTopDownDistances();
  //        }
  //    }

  private void updateLabels() {
    if (sideState.equals("away") && isFromLeft()) {
      tora.setText(calc.getResult().tora() + "m");
      toda.setText(calc.getResult().toda() + "m");
      asda.setText(calc.getResult().asda() + "m");
      awayLda.setText(calc.getResult().lda() + "m");
    } else if (sideState.equals("away") && !isFromLeft()) {
      towardtora.setText(calc.getResult().tora() + "m");
      towardtoda.setText(calc.getResult().toda() + "m");
      towardasda.setText(calc.getResult().asda() + "m");
      towardLda.setText(calc.getResult().lda() + "m");
    } else if (sideState.equals("toward") && !isFromLeft()) {
      tora.setText(calc.getResult().tora() + "m");
      toda.setText(calc.getResult().toda() + "m");
      asda.setText(calc.getResult().asda() + "m");
      awayLda.setText(calc.getResult().lda() + "m");
    } else if (sideState.equals("toward") && isFromLeft()) {
      towardtora.setText(calc.getResult().tora() + "m");
      towardtoda.setText(calc.getResult().toda() + "m");
      towardasda.setText(calc.getResult().asda() + "m");
      towardLda.setText(calc.getResult().lda() + "m");
    }

    if (side == false) {
      applyDirection();
    }

//        if (side == true) {
//            if (calc.getCalculationCase() == TOWARDS_OBSTACLE && sideState.equals("toward")) {
//                // sideState = "toward";
//                toraTodaAsda.setText(calc.getResult().tora() + "m");
//                towardLda.setText(calc.getResult().lda() + "m");
//                towardHeight.setText(calc.getObstacle().getHeight() + "m");
//            } else if (calc.getCalculationCase() == AWAY_FROM_OBSTACLE && sideState.equals("away")) {
//                // sideState = "away";
//                tora.setText(calc.getResult().tora() + "m");
//                toda.setText(calc.getResult().toda() + "m");
//                asda.setText(calc.getResult().asda() + "m");
//                awayLda.setText(calc.getResult().lda() + "m");
//            }
  }

  // method to give top down slightly different arrows (not sure how)
//    private void applyTopDownDistances() {
//        //        firstVert.setText("150m");
//        //        secondVert.setText("105m");
//        //        thirdVert.setText("75m");
//        //        rightHorizontal.setText("60m");
//        //        leftHorizontal.setText("60m");
//        //        largeHorizontal.setText("300m");
//        //        smallHorizontal.setText("150m");
//        if (calc.getCalculationCase() == TOWARDS_OBSTACLE && sideState.equals("toward")) {
//            // sideState = "toward";
//            toraTodaAsda.setText(calc.getResult().tora() + "m");
//            towardLda.setText(calc.getResult().lda() + "m");
//            towardHeight.setText(calc.getObstacle().getHeight() + "m");
//        } else if (calc.getCalculationCase() == AWAY_FROM_OBSTACLE && sideState.equals("away")) {
//            // sideState = "away";
//            tora.setText(calc.getResult().tora() + "m");
//            toda.setText(calc.getResult().toda() + "m");
//            asda.setText(calc.getResult().asda() + "m");
//            awayLda.setText(calc.getResult().lda() + "m");
//        }
//        //paintObstacle();


  @SuppressWarnings("unchecked")
  public void addNotification(String notificationMessage, Color colour) {
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    Text notification = new Text(
        "[" + formatter.format(new Timestamp(System.currentTimeMillis())) + "] "
            + notificationMessage);
    notification.setWrappingWidth(notificationsContent.getWidth());
    notificationsContent
        .getChildren()
        .add(notification);

    Animation animation =
        new Transition() {
          {
            setCycleDuration(Duration.millis(500));
            setInterpolator(Interpolator.EASE_OUT);
          }

          @Override
          protected void interpolate(double v) {
            Color colourTransition =
                new Color(
                    colour.getRed(), colour.getGreen(), colour.getBlue(), 1 - v);
            // Color colourTransition = finalColour.interpolate(finalColour, 1 - v);
            notificationsContent.setBackground(
                new Background(
                    new BackgroundFill(colourTransition, CornerRadii.EMPTY, Insets.EMPTY)));
          }
        };
    animation.play();
  }

  // returns true if from the left, false otherwise
  private boolean isFromLeft() {
    if (runway == null) {
      return true;
    }

    return runway.getBearing() <= 180;
  }

  private void updateObstacle(String obstacle) {
    PseudoClass disabled = PseudoClass.getPseudoClass("disabled");
    if (obstacle != "Custom Obstacle") {
      //obstHeightSpinner.editableProperty().set(false);
      //obstWidthSpinner.editableProperty().set(false);
      obstHeightSpinner.setValueFactory(
          new IntegerSpinnerValueFactory(0, 100, Obstacles.valueOf(obstacle).getHeight()));
      obstWidthSpinner.setValueFactory(
          new IntegerSpinnerValueFactory(0, 100, Obstacles.valueOf(obstacle).getWidth()));
      //obstHeightSpinner.pseudoClassStateChanged(disabled, ((IntegerSpinnerValueFactory)obstHeightSpinner.getValueFactory()).getMax() == ((IntegerSpinnerValueFactory)obstHeightSpinner.getValueFactory()).getMin());
      //obstWidthSpinner.pseudoClassStateChanged(disabled, ((IntegerSpinnerValueFactory)obstWidthSpinner.getValueFactory()).getMax() == ((IntegerSpinnerValueFactory)obstWidthSpinner.getValueFactory()).getMin());
    } else {
      //obstHeightSpinner.editableProperty().set(true);
      //obstWidthSpinner.editableProperty().set(true);
      //obstHeightSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 100));
      //obstWidthSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 100));
    }
  }
}