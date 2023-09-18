package uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models;

import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Obstacle;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Runway;

public class PartialCalculation {

  protected Runway runway;
  protected Aircraft aircraftType;
  protected CalculationCase calculationCase;
  protected Obstacle obstacle;

  protected int obstDistanceFromThreshold;
  protected boolean obstLeftOfCentreline;
  protected int obstDistanceFromCentreline;

  public PartialCalculation(Runway runway, Aircraft aircraftType,
      CalculationCase calculationCase,
      Obstacle obstacle, int obstDistanceFromThreshold, boolean obstLeftOfCentreline,
      int obstDistanceFromCentreline) {
    this.runway = runway;
    this.aircraftType = aircraftType;
    this.calculationCase = calculationCase;
    this.obstacle = obstacle;
    this.obstDistanceFromThreshold = obstDistanceFromThreshold;
    this.obstLeftOfCentreline = obstLeftOfCentreline;
    this.obstDistanceFromCentreline = obstDistanceFromCentreline;
  }

  public PartialCalculation(Runway runway, Aircraft aircraftType,
      CalculationCase calculationCase,
      Obstacle obstacle, int obstDistanceFromThreshold) {
    this(runway, aircraftType, calculationCase, obstacle, obstDistanceFromThreshold, true, 0);
  }

  @Override
  public String toString() {
    return "Calculation of: \n"
        + "Runway: " + runway + "\n"
        + "A/C Type: " + aircraftType + "\n"
        + "Case: " + calculationCase + "\n"
        + "Obstacle: " + obstacle + "\n"
        + "Obstacle distance from threshold: " + obstDistanceFromThreshold + "\n"
        + "Obstacle position: " + obstDistanceFromCentreline + "m " + (obstLeftOfCentreline ? "left"
        : "right") + " of centreline" + "\n";
  }

  public FinalCalculation calculate() {
    return new FinalCalculation(this);
  }

  public Runway getRunway() {
    return runway;
  }

  public void setRunway(Runway runway) {
    this.runway = runway;
  }

  public Aircraft getAircraftType() {
    return aircraftType;
  }

  public void setAircraftType(
      Aircraft aircraftType) {
    this.aircraftType = aircraftType;
  }

  public CalculationCase getCalculationCase() {
    return calculationCase;
  }

  public void setCalculationCase(
      CalculationCase calculationCase) {
    this.calculationCase = calculationCase;
  }

  public Obstacle getObstacle() {
    return obstacle;
  }

  public void setObstacle(Obstacle obstacle) {
    this.obstacle = obstacle;
  }

  public int getObstDistanceFromThreshold() {
    return obstDistanceFromThreshold;
  }

  public void setObstDistanceFromThreshold(int obstDistanceFromThreshold) {
    this.obstDistanceFromThreshold = obstDistanceFromThreshold;
  }

  public boolean isObstLeftOfCentreline() {
    return obstLeftOfCentreline;
  }

  public void setObstLeftOfCentreline(boolean obstLeftOfCentreline) {
    this.obstLeftOfCentreline = obstLeftOfCentreline;
  }

  public int getObstDistanceFromCentreline() {
    return obstDistanceFromCentreline;
  }

  public void setObstDistanceFromCentreline(int obstDistanceFromCentreline) {
    this.obstDistanceFromCentreline = obstDistanceFromCentreline;
  }
}
