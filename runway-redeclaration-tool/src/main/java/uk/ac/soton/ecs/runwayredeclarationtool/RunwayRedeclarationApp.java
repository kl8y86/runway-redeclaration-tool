package uk.ac.soton.ecs.runwayredeclarationtool;

import java.security.MessageDigest;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.HomeController;

import uk.ac.soton.ecs.runwayredeclarationtool.ui.LoginController;


public class RunwayRedeclarationApp extends Application {

  private final int width = 941;
  private final int height = 470;

  //private static HelloApplication instance;
  private Stage stage;

  @Override
  public void start(Stage stage) throws IOException {
    //instance = this;
    this.stage = stage;

    //Open Menu
    Pair<String, String> loginCred = openMenu();
    //validate login to proceed to main screen
    //UserRepository userRepo = new UserRepository();
    //if(userRepo.authenticateUser(loginCred)){
    //openHome(loginCred);
    //}
  }

  public static void main(String[] args) {
    System.out.println("Starting client");
    //String v = Hashing.sha256().hashString("password2", StandardCharsets.UTF_8).toString()
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      System.err.println("About to launch");
    } catch (Exception e) {
      System.err.println("How on earth did this happen (invalid hashing algorithm)");
    }
    launch();
  }

  public Pair<String, String> openMenu() throws IOException {
    System.out.println("Opening Main Menu");
    FXMLLoader fxmlLoader = new FXMLLoader(
        RunwayRedeclarationApp.class.getResource("ui/login.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), width, height);
    String css = RunwayRedeclarationApp.class.getResource("ui/runway.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setTitle("Runway Redeclaration Tool");
    stage.setScene(scene);
    stage.show();
    LoginController loginController = fxmlLoader.getController();
    if (loginController.getLoggedInStatus()) {
      return new Pair<>("Hello", "gamers");
    }
    return new Pair<>("Hello", "ooie");
  }

  public void openHome(Pair<String, String> loginCred) throws IOException {
    System.out.println("Opening Home");
    FXMLLoader fxmlLoader = new FXMLLoader(
        RunwayRedeclarationApp.class.getResource("ui/home.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), width, height);
    String css = RunwayRedeclarationApp.class.getResource("ui/runway.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();

    HomeController myRunway = fxmlLoader.getController();
    myRunway.setLoginCred(loginCred);
    myRunway.topDown();
    //myRunway.towardSideView();
    //myRunway.awaySideView();
  }
}