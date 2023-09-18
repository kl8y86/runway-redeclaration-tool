package uk.ac.soton.ecs.runwayredeclarationtool.ui.views;

import java.io.File;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Calculation;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.LogRepository;

public class ImportView {

  private final LogRepository logRepository = new LogRepository();
  private Calculation calculation;


  public Calculation createImportBox() {
    Dialog<String> importDialog = new TextInputDialog();
    importDialog.setTitle("Import XML file");
    importDialog.setContentText("Enter the filename of the XML file you wish to import");
    importDialog.setHeaderText("Import XML file");
    Optional<String> result = importDialog.showAndWait();
    if (result.isPresent()) {
      if (!result.get().equals("")) {
        System.out.printf("file provided: %s%n", result);
        String resultStr = result.get();
        if (result.get().contains(".xml")) {
          resultStr = result.get().split(".xml")[0];
        } else {
          System.out.print("Import cancelled. Probably.\n");
        }
        File checkFileExists = new File(resultStr + ".xml");
        if (checkFileExists.exists()) {
          calculation = logRepository.importFile(resultStr);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR,
              String.format("Error: File not found (%s.xml)",
                  resultStr));
          alert.show();
          return null;
        }
      } else {
        var notFoundAlert = new Alert(Alert.AlertType.WARNING);
        notFoundAlert.setTitle("Input Not Recognized");
        notFoundAlert.setHeaderText(null);
        notFoundAlert.setContentText(
            String.format("Must provide valid file name. Provided: %s", result.get()));
        notFoundAlert.show();
        //importBox();
      }
      return calculation;
    } else {
      System.out.print("Import cancelled. Probably.%n");
    }
    return null;
  }
}
