package uk.ac.soton.ecs.runwayredeclarationtool.ui.views;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.soton.ecs.runwayredeclarationtool.RunwayRedeclarationApp;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.UserRepository;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.AdminPanelController;
import uk.ac.soton.ecs.runwayredeclarationtool.ui.HomeController;

import java.io.IOException;
import java.util.Objects;

public class AdminView {

  private Stage stage;
  private Scene scene;
  private Parent root;

  private double width = 941.0;
  private double height = 470.0;

  private final UserRepository userRepository = new UserRepository();
  private HomeController homeController;

  public void createAdminScene(Event event, HomeController homecontroller) throws IOException {
    homeController = homecontroller;
    root = FXMLLoader.load(
        Objects.requireNonNull(homeController.getClass().getResource("home.fxml")));
    stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
    scene = new Scene(root);

    System.out.println("Opening admin panel");
    FXMLLoader fxmlLoader = new FXMLLoader(
        RunwayRedeclarationApp.class.getResource("ui/adminPanel.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), width, height);
    stage.setScene(scene);
    stage.show();

    AdminPanelController adminPanel = fxmlLoader.getController();
    adminPanel.setHomeController(
        homeController); //required for changing scene back (probably not but this is how it is)
    adminPanel.setUserRepo(userRepository);
  }

}
