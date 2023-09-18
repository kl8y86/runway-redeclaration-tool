package uk.ac.soton.ecs.runwayredeclarationtool.services.calculations.exceptions;

public class AlreadyCalculatedException extends RuntimeException {

  public AlreadyCalculatedException() {
    super("Cannot modify attributes of a completed calculation.");
  }
}
