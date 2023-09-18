package uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models;

import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Obstacle;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Runway;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.exceptions.AlreadyCalculatedException;

public class FinalCalculation extends PartialCalculation {

  protected CalculationResult result;

  protected FinalCalculation(PartialCalculation partialCalculation) {
    super(partialCalculation.getRunway(), partialCalculation.getAircraftType(),
        partialCalculation.getCalculationCase(), partialCalculation.getObstacle(),
        partialCalculation.getObstDistanceFromThreshold(),
        partialCalculation.isObstLeftOfCentreline(),
        partialCalculation.getObstDistanceFromCentreline());

    switch (calculationCase) {
      case TOWARDS_OBSTACLE -> performCalculationsTowards();
      case AWAY_FROM_OBSTACLE -> performCalculationsAwayFrom();
      default -> throw new UnsupportedOperationException(
          "Unimplemented calculation case " + calculationCase);
    }
  }

  public CalculationResult getResult() {
    return result;
  }

  @Override
  public String toString() {
    return super.toString() + "\n"
        + "Computed Result: " + result;
  }

  @Override
  public void setRunway(Runway runway) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setAircraftType(Aircraft aircraftType) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setCalculationCase(CalculationCase calculationCase) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setObstacle(Obstacle obstacle) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setObstDistanceFromThreshold(int obstDistanceFromThreshold) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setObstLeftOfCentreline(boolean obstLeftOfCentreline) {
    throw new AlreadyCalculatedException();
  }

  @Override
  public void setObstDistanceFromCentreline(int obstDistanceFromCentreline) {
    throw new AlreadyCalculatedException();
  }

  private void performCalculationsTowards() {
    // ------------------------------
    // Take-off towards obstacle
    // ------------------------------

    // TOCS slope distance over obstacle
    int tocsDist = obstacle.getHeight() * 50;

    // If TOCS slope < RESA, use the RESA instead
    int resa = Math.max(tocsDist, 240 + obstacle.getLength());

    // Recalculate the TORA
    int tora = obstDistanceFromThreshold + runway.getDisplacedThreshold() - resa - 60;

    // As the stopway and clearway are obstructed, TORA = TODA = ASDA
    int toda = tora;
    int asda = tora;

    // ------------------------------
    // Landing towards obstacle
    // ------------------------------

    // We only need to recalculate the LDA
    // The RESA is always 240m in this case
    int lda = obstDistanceFromThreshold - 240 - 60;

    // ------------------------------
    // Result
    // ------------------------------
    if (tora < 0) {
      tora = 0;
    }
    if (toda < 0) {
      toda = 0;
    }
    if (asda < 0) {
      asda = 0;
    }
    if (lda < 0) {
      lda = 0;
    }
    this.result = new CalculationResult(
        tora, toda, asda, lda
    );
  }

  private void performCalculationsAwayFrom() { // landing over, takeoff away from
    // ------------------------------
    // Take-off away from obstacle
    // ------------------------------

    // For all values, we reduce them by the distance from the threshold to the front of the
    // obstacle, plus the engine blast allowance for the type
    int takeoffDist =
        obstacle.getLength() + obstDistanceFromThreshold + aircraftType.getBlastDist();

    int tora = runway.getTora() - takeoffDist;
    int toda = runway.getToda() - takeoffDist;
    int asda = runway.getAsda() - takeoffDist;

    // ------------------------------
    // Landing over obstacle
    // ------------------------------

    // Calculate the distance on the runway under the ALS
    int alsDist = obstacle.getHeight() * 50;

    // We need a minimum of 240m around the obstacle for the RESA
    int resa = Math.max(alsDist, 240 + obstacle.getLength());

    // The LDA is the only value we need to recalculate
    int lda = runway.getLda() - obstDistanceFromThreshold - resa - 60;

    // ------------------------------
    // Result
    // ------------------------------
    if (tora < 0) {
      tora = 0;
    }
    if (toda < 0) {
      toda = 0;
    }
    if (asda < 0) {
      asda = 0;
    }
    if (lda < 0) {
      lda = 0;
    }
    this.result = new CalculationResult(
        tora, toda, asda, lda
    );
  }
}
