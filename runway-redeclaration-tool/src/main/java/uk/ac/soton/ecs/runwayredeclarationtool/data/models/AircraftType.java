package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

public class AircraftType {

  // TODO stub class
  private String code;
  private int blastArea;

  public AircraftType(String code, int blastArea) {
    this.code = code;
    this.blastArea = blastArea;
  }

  @Override
  public String toString() {
    return this.code + " (BA=" + blastArea + ")";
  }

  public String getCode() {
    return code;
  }

  public int getBlastArea() {
    return blastArea;
  }
}
