package uk.ac.soton.ecs.runwayredeclarationtool.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


public class ImportExportController implements Initializable {

  @FXML
  public TextField fileField;

  private String fileURL = "";


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    //fileURL = fileField.getText();

  }

  public String submitImport() {
    fileURL = fileField.getText();
    return fileURL;
  }

  public String submitExport() {
    fileURL = fileField.getText();
    return fileURL;
  }
}
