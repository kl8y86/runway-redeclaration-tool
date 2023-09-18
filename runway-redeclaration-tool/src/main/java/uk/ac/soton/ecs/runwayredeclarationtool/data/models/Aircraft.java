package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

import com.j256.ormlite.field.DatabaseField;

public class Aircraft {

  public static final String MODEL = "model";
  public static final String BLASTDIST = "blastDist";

  @DatabaseField(columnName = MODEL, canBeNull = false)
  private String model;
  @DatabaseField(columnName = BLASTDIST, canBeNull = false)
  private int blastDist;

  public Aircraft(String modelName, int blastDistance){
    model = modelName;
    blastDist = blastDistance;
  }

  /**
   * ORMLite constructor.
   */
  Aircraft() {
  }

  @Override
  public String toString() {
    return this.model + " (BA=" + blastDist + ")";
  }

  public String getModel() {
    return model;
  }

  public int getBlastDist () {
    return blastDist;
  }
}
