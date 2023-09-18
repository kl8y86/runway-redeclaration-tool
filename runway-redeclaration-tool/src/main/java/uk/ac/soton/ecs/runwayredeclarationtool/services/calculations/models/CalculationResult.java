package uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.models;

public record CalculationResult(int tora, int toda, int asda, int lda) {

  @Override
  public String toString() {
    return String.format("Result of TORA=%d, TODA=%d, ASDA=%d, LDA=%d");
  }
}