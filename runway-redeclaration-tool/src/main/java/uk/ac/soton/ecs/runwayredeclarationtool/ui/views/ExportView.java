package uk.ac.soton.ecs.runwayredeclarationtool.ui.views;

import java.io.File;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Calculation;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.LogRepository;

public class ExportView {

  private final LogRepository logRepository = new LogRepository();

  public void createExportBox(Calculation calculation) {
    Dialog<String> exportDialog = new TextInputDialog();
    exportDialog.setTitle("Export XML file");
    exportDialog.setContentText("Enter the filename of the XML file you wish to export");
    exportDialog.setHeaderText("Export XML file");
    Optional<String> result = exportDialog.showAndWait();
    if (result.isPresent()) {
      if (!result.get().equals("")) {
        String resultString = result.get();
        System.out.printf("file provided: %s%n", result);
        if (resultString.contains(".xml")) {
          System.out.println(resultString.split(".xml")[0]);
          resultString = resultString.split(".xml")[0];
        }
        File checkFileExists = new File(resultString + ".xml");
        if (!checkFileExists.exists()) {
          logRepository.exportFile(calculation, resultString);
        } else {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
              String.format("Error: File already exists. Overwrite? (%s)",
                  resultString));
          alert.showAndWait();
          if (alert.getResult() == ButtonType.OK) {
            System.out.println("gaming");
            logRepository.exportFile(calculation, resultString);
          }
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
    } else {
      System.out.print("Export cancelled. Probably.%n");
    }
  }
}
