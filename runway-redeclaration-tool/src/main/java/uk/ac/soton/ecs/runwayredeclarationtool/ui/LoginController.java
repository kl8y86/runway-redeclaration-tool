package uk.ac.soton.ecs.runwayredeclarationtool.ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import uk.ac.soton.ecs.runwayredeclarationtool.RunwayRedeclarationApp;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.User;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.UserRepository;

import java.util.Objects;

public class LoginController {

  private Stage stage;
  private Scene scene;
  private Parent root;

  private final int width = 941;
  private final int height = 470;

  private User user;

  private boolean isLoggedIn = false;

  public TextField usernameText;
  @FXML
  public PasswordField passwordHide;
  @FXML
  public TextField passwordShow;

  public void changeScene(Event event) throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home.fxml")));
    stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
    scene = new Scene(root);

    System.out.println("Opening home");
    FXMLLoader fxmlLoader = new FXMLLoader(
        RunwayRedeclarationApp.class.getResource("ui/home.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), width, height);
    String css = RunwayRedeclarationApp.class.getResource("ui/runway.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
//
    HomeController myRunway = fxmlLoader.getController();
    HomeController.setUser(user);
    myRunway.topDown();
    isLoggedIn = true;
  }


  public void showPassword(MouseEvent mouseEvent) {
    passwordHide.setVisible(false);
    passwordShow.setVisible(true);
    passwordShow.setText(passwordHide.getText());
  }

  public void hidePassword(MouseEvent mouseEvent) {
    passwordHide.setVisible(true);
    passwordShow.setVisible(false);
  }

  public boolean getLoggedInStatus() {
    return isLoggedIn;
  }


  public void attemptLogin(Event mouseEvent) throws IOException {
    Pair<String, String> loginDetails = new Pair<>(usernameText.getText(), passwordHide.getText());
    System.out.println("login deets : " + loginDetails.getKey() + " , " + loginDetails.getValue());
    UserRepository userRepo = new UserRepository();
    User result = userRepo.authenticateUser(loginDetails);
    if (result != null) {
      System.err.println("logged in lel");
      user = result;
      changeScene(mouseEvent);
    } else {
      passwordHide.setText("");
      usernameText.setText("");
      Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect username or password.");
      alert.show();
    }
  }

  /**
   * runs attemptLogin if user presses enter
   *
   * @param event key press
   * @throws IOException tbh idk
   */
  public void enterAttemptLogin(KeyEvent event) throws IOException {
    if (event.getCode().equals(KeyCode.ENTER)) {
      attemptLogin(event);
    }
  }

  public void closeApplication() {
    Platform.exit();
  }
}
