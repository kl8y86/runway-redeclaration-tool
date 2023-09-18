package uk.ac.soton.ecs.runwayredeclarationtool.tests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.AircraftType;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Obstacle;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Runway;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.CalculationCase;
import uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models.PartialCalculation;

public class CalculationsTests {

  private final Aircraft b747 = new Aircraft("B744", 500);
  private final Aircraft a320 = new Aircraft("A320", 300);

  private final Runway r09l = new Runway("09L", "09L", 3902, 3902, 3902, 3595, 306);
  private final Runway r27r = new Runway("27R", "27R", 3884, 3962, 3884, 3884, 0);
  private final Runway r09r = new Runway("09R", "09R", 3600, 3660, 3660, 3353, 307);
  private final Runway r27l = new Runway("27L", "27L", 3660, 3660, 3660, 3660, 0);

  @Test
  public void towardsObstacle_resaSatisfied_itCalculatesCorrectValues() {
    // Arrange
    var partial = new PartialCalculation(
        r09l,
        b747,
        CalculationCase.TOWARDS_OBSTACLE,
        new Obstacle(20, 100),
        2600
    );

    // Act
    var calc = partial.calculate();
    var result = calc.getResult();

    // Assert
    assertEquals(1846, result.tora());
    assertEquals(1846, result.toda());
    assertEquals(1846, result.asda());
    assertEquals(2300, result.lda());
  }

  @Test
  public void towardsObstacle_resaNotSatisfied_itCalculatesCorrectValues() {
    // Arrange
    var partial = new PartialCalculation(
        r27l,
        b747,
        CalculationCase.TOWARDS_OBSTACLE,
        new Obstacle(4, 10),
        2000
    );

    // Act
    var calc = partial.calculate();
    var result = calc.getResult();

    // Assert
    assertEquals(1690, result.tora());
    assertEquals(1690, result.toda());
    assertEquals(1690, result.asda());
    assertEquals(1700, result.lda());
  }

  @Test
  public void awayFromObstacle_resaSatisfied_itCalculatesCorrectValues() {
    // Arrange
    var partial = new PartialCalculation(
        r09l,
        b747,
        CalculationCase.AWAY_FROM_OBSTACLE,
        new Obstacle(20, 100),
        50
    );

    // Act
    var calc = partial.calculate();
    var result = calc.getResult();

    // Assert
    assertEquals(3252, result.tora());
    assertEquals(3252, result.toda());
    assertEquals(3252, result.asda());
    assertEquals(2485, result.lda());
  }

  @Test
  public void awayFromObstacle_resaNotSatisfied_itCalculatesCorrectValues() {
    // Arrange
    var partial = new PartialCalculation(
        r27l,
        b747,
        CalculationCase.AWAY_FROM_OBSTACLE,
        new Obstacle(4, 10),
        80
    );

    // Act
    var calc = partial.calculate();
    var result = calc.getResult();

    // Assert
    assertEquals(3070, result.tora());
    assertEquals(3070, result.toda());
    assertEquals(3070, result.asda());
    assertEquals(3270, result.lda());
  }

}
