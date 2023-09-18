package uk.ac.soton.ecs.runwayredeclarationtool.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.ac.soton.ecs.runwayredeclarationtool.RunwayRedeclarationApp;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Runway;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.User;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.AircraftRepository;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.AirportRepository;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminPanelController {

  private static HomeController homeController;
  private static UserRepository userRepository;
  private static AircraftRepository aircraftRepository = new AircraftRepository();
  private static AirportRepository airportRepository = new AirportRepository();
  private static double width = 941.0;
  private static double height = 470.0;

  @FXML // fx:id = "userSelectionBox"
  protected ComboBox<String> userSelectionBox;
  @FXML // fx:id = "adminCheckBox"
  protected CheckBox adminCheckBox;
  @FXML // fx:id = "removeUserButton"
  protected Button removeUserButton;

  @FXML // fx:id = "aircraftSelectionBox"
  protected ComboBox<String> aircraftSelectionBox;
  @FXML // fx:id = "removeAircraftButton"
  protected Button removeAircraftButton;

  @FXML // fx:id = "runwaySelectionBox"
  protected ComboBox<String> runwaySelectionBox;
  @FXML // fx:id = "removeRunwayButton"
  protected Button removeRunwayButton;

  protected User selectedUser;
  protected Aircraft selectedAircraft;
  protected Runway selectedRunway;


  public void setHomeController(HomeController homeControl) {
    homeController = homeControl;
  }

  public void setUserRepo(UserRepository userRepo) {
    userRepository = userRepo;
  }

  public void exit(Event event) {
    try {
      Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();

      System.out.println("Leaving admin panel");
      FXMLLoader fxmlLoader = new FXMLLoader(
          RunwayRedeclarationApp.class.getResource("ui/home.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), width, height);
      stage.setScene(scene);
      stage.show();

      HomeController myRunway = fxmlLoader.getController();
      //HomeController.setUser(user); // I dont think we need this ? It seems to retain user ? :egsmile:
      myRunway.topDown();
    } catch (IOException ioe) {
      System.err.println(
          "(Failed to leave admin panel) YOUR INTERNET ACTIVITY HAS ATTRACTED OUR ATTENTION. " +
              "We Have been Track your Internet Activity and we do not like the Bhehaviour ! " +
              "You're son, daughter, And wife taken to Admin Panel Autonomous Zone where they will be Tracked and "
              +
              "Traced by His Royal Majesty Government. Your Halifax Fico remain constant, But -20 Nectar Points !");
      ioe.printStackTrace();
    }
  }

  public void showAllUsers() {
    List<User> userList = userRepository.getAllUsers();
    ObservableList<String> foundUsers = FXCollections.observableArrayList();
    //add airports to airport list only if it's unique to the list (so no duplicated in drop down)
    for (User user : userList) {
      if (!foundUsers.contains(user.getEmail())) {
        foundUsers.add(
            String.format("%s %s (%s)", user.getFirstName(), user.getLastName(), user.getEmail()));
      }
    }
    removeUserButton.setDisable(userList.size() == 0); //should probably do this
    userSelectionBox.setItems(foundUsers);
  }

  public void showAllAircraft() {
    List<Aircraft> aircraftList = aircraftRepository.getAllAircrafts();
    ObservableList<String> foundAircrafts = FXCollections.observableArrayList();
    //add airports to airport list only if it's unique to the list (so no duplicated in drop down)
    for (Aircraft aircraft : aircraftList) {
      if (!foundAircrafts.contains(aircraft.getModel())) {
        foundAircrafts.add(String.format("%s (BD=%dm)", aircraft.getModel(), aircraft.getBlastDist()));
      }
    }
    removeAircraftButton.setDisable(aircraftList.size() == 0); //should probably do this
    aircraftSelectionBox.setItems(foundAircrafts);
  }

  public void showAllRunways() {
    List<Runway> runwaysList = airportRepository.getAllRunways();
    ObservableList<String> foundRunways = FXCollections.observableArrayList();
    //add airports to airport list only if it's unique to the list (so no duplicated in drop down)
    for (Runway runway : runwaysList) {
      foundRunways.add(String.format("%s %s", runway.getAirport(), runway.getRunwayDesignator()));
    }
    removeRunwayButton.setDisable(runwaysList.size() == 0); //should probably do this
    runwaySelectionBox.setItems(foundRunways);
  }

  public void userSelectionMade(){
    try {
      System.err.println("user selection change");
      String selectedUserEmail = userSelectionBox.getValue();
      selectedUserEmail = selectedUserEmail.substring(selectedUserEmail.indexOf("(") + 1,
          selectedUserEmail.indexOf(")"));
      System.out.println("User email : " + selectedUserEmail);
      selectedUser = userRepository.getUserByEmail(selectedUserEmail);
      adminCheckBox.setSelected(selectedUser.getAdminAccess());
      //if selected user is the user, disable ability to change their own admin perms and delete themselves
      adminCheckBox.setDisable(selectedUser.getEmail().equals(homeController.getCurrentUser().getEmail()));
      removeUserButton.setDisable(selectedUser.getEmail().equals(homeController.getCurrentUser().getEmail()));
    } catch (NullPointerException npe) {
      System.out.println("This probably isnt actually an error. Well it is, but I have no idea why selectionMade() is" +
              "called several times when you go to change user. Something to do with whatever triggers onAction in " +
              "javafx. If this is actually an error... Oh no! Anyway, ");
    }
  }

  public void aircraftSelectionMade(){
    try {
      System.err.println("aircraft selection change");
      String selectedAircraftModel = aircraftSelectionBox.getValue();
      selectedAircraftModel = selectedAircraftModel.substring(0, selectedAircraftModel.indexOf(" (BD="));
      System.out.println("aircraft model : " + selectedAircraftModel);
      selectedAircraft = aircraftRepository.getAircraftFromModel(selectedAircraftModel);
    } catch (NullPointerException npe) {
      System.out.println(
          "This probably isnt actually an error. Well it is, but I have no idea why selectionMade() is"
              + "called several times when you go to change user. Something to do with whatever triggers onAction in "
              + "javafx. If this is actually an error... Oh no! Anyway, ");
    }
  }

  public void runwaySelectionMade(){
    try {
      System.err.println("runway selection change");
      String selectedrunway = runwaySelectionBox.getValue();
      System.out.println("runway : " + selectedRunway);
      selectedRunway = airportRepository.getCurrentRunway(selectedrunway.split(" ")[0],
              selectedrunway.split(" ")[1]).get(0);
    } catch (NullPointerException npe) {
      System.out.println(
              "This probably isnt actually an error. Well it is, but I have no idea why selectionMade() is"
                      + "called several times when you go to change user. Something to do with whatever triggers onAction in "
                      + "javafx. If this is actually an error... Oh no! Anyway, ");
    }
  }

  public void alterAdminAccess() {
    if (userSelectionBox.getValue() == null || userSelectionBox.getValue()
        .equals("Select a User ...")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid User");
      alert.setHeaderText("Please select a user");
      alert.setContentText("Press OK to continue");
      alert.show();
      adminCheckBox.setDisable(true);
      adminCheckBox.setSelected(false);
      return;
    }
    userRepository.updateUserAdminAccess(selectedUser, !selectedUser.getAdminAccess());
  }

  public void removeUser() {
    if(selectedUser.getEmail().equals(homeController.getCurrentUser().getEmail())) return;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete this User?");
    alert.setHeaderText("Are you sure you want to delete this User?");
    alert.setContentText("Click OK to delete this User.");
    Optional<ButtonType> response = alert.showAndWait();
    if(response.isPresent()){
      if(response.get() == ButtonType.OK){
        userRepository.removeUser(userRepository.getUserByEmail(selectedUser.getEmail()));
        showAllUsers();
        adminCheckBox.setDisable(true);
        adminCheckBox.setSelected(false);
      }
    }

  }

  public void removeAircraft() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete this Aircraft?");
    alert.setHeaderText("Are you sure you want to delete this Aircraft?");
    alert.setContentText("Click OK to delete this Aircraft.");
    Optional<ButtonType> response = alert.showAndWait();
    if(response.isPresent()){
      if(response.get() == ButtonType.OK){
        aircraftRepository.removeAircraftByModel(selectedAircraft);
        showAllAircraft();
        removeAircraftButton.setDisable(true);
      }
    }
  }

  public void removeRunway(){
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete this Runway?");
    alert.setHeaderText("Are you sure you want to delete this Runway?");
    alert.setContentText("Click OK to delete this Runway.");
    Optional<ButtonType> response = alert.showAndWait();
    if(response.isPresent()){
      if(response.get() == ButtonType.OK){
        airportRepository.removeRunway(selectedRunway);
        showAllRunways();
        removeRunwayButton.setDisable(true);
      }
    }
  }

  public void addUser() {
    Dialog<User> createUserDialog = new Dialog<>();
    createUserDialog.setTitle("Add User");
    createUserDialog.setHeaderText("Add a new user");
    DialogPane contentPane = createUserDialog.getDialogPane();
    contentPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    TextField usernameTF = new TextField();
    usernameTF.setPromptText("Enter Username ...");
    TextField firstNameTF = new TextField();
    firstNameTF.setPromptText("Enter First Name ...");
    TextField lastNameTF = new TextField();
    lastNameTF.setPromptText("Enter Last Name ...");
    TextField emailTF = new TextField();
    emailTF.setPromptText("Enter Email ...");
    PasswordField passwordTF = new PasswordField();
    passwordTF.setPromptText("Enter Password ...");
    PasswordField passwordConfTF = new PasswordField();
    passwordConfTF.setPromptText("Confirm Password ...");
    contentPane.setContent(
        new VBox(8, usernameTF, firstNameTF, lastNameTF, emailTF, passwordTF, passwordConfTF));
    createUserDialog.setResultConverter((ButtonType button) -> {
      if (button == ButtonType.OK) {
        if (!passwordTF.getText().equals(passwordConfTF.getText())) {
          //if passwords dont match, show warning alert
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("Passwords do not match");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
        if (!emailTF.getText().contains("@")) {
          //likely invalid email address
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("Email address does not contain an \"@\" symbol");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
        if (usernameTF.getText().equals("") || firstNameTF.getText().equals("")
            || lastNameTF.getText().equals("") ||
            emailTF.getText().equals("") || passwordTF.getText().equals("")) {
          //if details blank, alert
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("All fields must be completed to add a new user");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
        //show confirmation alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User registration successful");
        alert.setHeaderText("New user has been registered");
        alert.setContentText("Click OK to proceed");
        alert.show();
        return new User(usernameTF.getText(), firstNameTF.getText(), lastNameTF.getText(),
            passwordTF.getText(), emailTF.getText());
      }
      return null;
    });

    Optional<User> result = createUserDialog.showAndWait();
    result.ifPresent(user -> userRepository.addUser(user));
  }

  public void addAircraft() {
    Dialog<Aircraft> createAircraftDialog = new Dialog<>();
    createAircraftDialog.setTitle("Add Aircraft");
    createAircraftDialog.setHeaderText("Add a new aircraft");
    DialogPane contentPane = createAircraftDialog.getDialogPane();
    contentPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    TextField modelTF = new TextField();
    modelTF.setPromptText("Enter Model Name ...");
    TextField blastDistTF = new TextField();
    blastDistTF.setPromptText("Enter Blast Distance (m) ...");
    contentPane.setContent(new VBox(8, modelTF, blastDistTF));

    createAircraftDialog.setResultConverter((ButtonType button) -> {
      if(button == ButtonType.OK){
        if(modelTF.getText().equals("")){
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("Aircraft model must not be empty");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
        if(modelTF.getText().contains("(BD=")){
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("Model name should not contain \"(BD=\" ... why did you put that in ?");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
        try {
          int blastDistInt = Integer.parseInt(blastDistTF.getText().split("m")[0]);
          if(blastDistInt < 0 || blastDistInt > 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid details");
            alert.setHeaderText("Blast Distance should be between 0m and 100m");
            alert.setContentText("Click OK to try again");
            alert.show();
            return null;
          }
          //show confirmation alert
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Aircraft registration successful");
          alert.setHeaderText("New Aircraft has been registered");
          alert.setContentText("Click OK to proceed");
          alert.show();
          return new Aircraft(modelTF.getText(), blastDistInt);
        } catch (Exception e) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("Blast Distance must be an integer");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
      }
      return null;
    });

    Optional<Aircraft> result = createAircraftDialog.showAndWait();
    result.ifPresent(aircraft -> aircraftRepository.addAircraft(aircraft));
  }

  public void addRunway() {
    Dialog<Runway> createRunwayDialog = new Dialog<>();
    createRunwayDialog.setTitle("Add Runway");
    createRunwayDialog.setHeaderText("Add a new Runway");
    DialogPane contentPane = createRunwayDialog.getDialogPane();
    contentPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    TextField airportTF = new TextField();
    airportTF.setPromptText("Enter Airport ICAO ...");
    TextField runwayDesignatorTF = new TextField();
    runwayDesignatorTF.setPromptText("Enter Runway Designator ...");
    TextField toraTF = new TextField();
    toraTF.setPromptText("Enter TORA ...");
    TextField todaTF = new TextField();
    todaTF.setPromptText("Enter TODA ...");
    TextField asdaTF = new TextField();
    asdaTF.setPromptText("Enter ASDA ...");
    TextField ldaTF = new TextField();
    ldaTF.setPromptText("Enter LDA ...");
    TextField displacedThresholdTF = new TextField();
    displacedThresholdTF.setPromptText("Enter Displaced Threshold ...");
    contentPane.setContent(
            new VBox(8, airportTF, runwayDesignatorTF, toraTF, todaTF, asdaTF, ldaTF, displacedThresholdTF));
    createRunwayDialog.setResultConverter((ButtonType button) -> {
      if (button == ButtonType.OK) {
        //check ints
        try{
          int tora = Integer.parseInt(toraTF.getText());
          int toda = Integer.parseInt(todaTF.getText());
          int asda = Integer.parseInt(asdaTF.getText());
          int lda = Integer.parseInt(ldaTF.getText());
          int dispT = Integer.parseInt(displacedThresholdTF.getText());
          if(tora < 0 || tora > 9999 || toda < 0 || toda > 9999 || asda < 0 || asda > 9999 || lda < 0 || lda > 9999 ||
                  dispT < 0 || dispT > 9999){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid details");
            alert.setHeaderText("TORA, TODA, ASDA, LDA, Displaced Threshold must be between 0 and 9999");
            alert.setContentText("Click OK to try again");
            alert.show();
            return null;
          }
          //check empty strings
          if(airportTF.getText().equals("") || runwayDesignatorTF.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid details");
            alert.setHeaderText("Airport ICAO and Runway Designator must not be blank");
            alert.setContentText("Click OK to try again");
            alert.show();
            return null;
          }
          //show confirmation alert
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Runway registration successful");
          alert.setHeaderText("New Runway has been registered");
          alert.setContentText("Click OK to proceed");
          alert.show();
          return new Runway(airportTF.getText(), runwayDesignatorTF.getText(), tora, toda, asda, lda, dispT);
        } catch (Exception e) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Invalid details");
          alert.setHeaderText("TORA, TODA, ASDA, LDA, Displaced Threshold must be integers");
          alert.setContentText("Click OK to try again");
          alert.show();
          return null;
        }
      }
      return null;
    });

    Optional<Runway> result = createRunwayDialog.showAndWait();
    result.ifPresent(runway -> airportRepository.addRunway(runway));
  }
}
