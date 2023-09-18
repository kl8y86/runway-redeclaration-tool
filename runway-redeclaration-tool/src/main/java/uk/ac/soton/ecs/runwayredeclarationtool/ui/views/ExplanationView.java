package uk.ac.soton.ecs.runwayredeclarationtool.ui.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.FinalCalculation;

public class ExplanationView extends AnchorPane {

  private final FinalCalculation calculation;

  public ExplanationView(FinalCalculation calc) {
    this.calculation = calc;

    var expBox = new VBox();
    expBox.setAlignment(Pos.CENTER);
    expBox.setSpacing(20.0d);
    this.getChildren().add(expBox);

    expBox.getChildren().add(new Label("testing!"));
  }
}
