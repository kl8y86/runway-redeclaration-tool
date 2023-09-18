package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Runway")
public class Runway {

  // for QueryBuilder to be able to find the fields
  public static final String AIRPORT = "airport";
  public static final String RUNWAY_DESIGNATOR = "runwaydesignator";
  public static final String TORA = "tora";
  public static final String TODA = "toda";
  public static final String ASDA = "asda";
  public static final String LDA = "lda";
  public static final String DISPLACED_THRESHOLD = "displacedthreshold";


  @DatabaseField(columnName = AIRPORT, canBeNull = false)
  private String airport;

  @DatabaseField(columnName = RUNWAY_DESIGNATOR, canBeNull = false)
  private String runwayDesignator;

  @DatabaseField(columnName = TORA, canBeNull = false)
  private int tora;

  @DatabaseField(columnName = TODA, canBeNull = false)
  private int toda;

  @DatabaseField(columnName = ASDA, canBeNull = false)
  private int asda;

  @DatabaseField(columnName = LDA, canBeNull = false)
  private int lda;

  @DatabaseField(columnName = DISPLACED_THRESHOLD, canBeNull = false)
  private int displacedThreshold;

  Runway() {
    // all persisted classes must define a no-arg constructor with at least package visibility
  }

  public Runway(String airport, String runwayDesignator, int tora, int toda, int asda, int lda,
      int displacedThreshold) {
    this.airport = airport;
    this.runwayDesignator = runwayDesignator;
    this.tora = tora;
    this.toda = toda;
    this.asda = asda;
    this.lda = lda;
    this.displacedThreshold = displacedThreshold;
  }


  public String getAirport() {
    return airport;
  }

  public void setAirport(String airport) {
    this.airport = airport;
  }

  public String getRunwayDesignator() {
    return runwayDesignator;
  }

  public void setRunwayDesignator(String runwayDesignator) {
    this.runwayDesignator = runwayDesignator;
  }

  public int getTora() {
    return tora;
  }

  public void setTora(int tora) {
    if (tora < 0) {
      System.err.println("TORA should not be negative (provided: " + tora + "). Used TORA = 0.");
      tora = 0;
    }
    this.tora = tora;
  }

  public int getToda() {
    return toda;
  }

  public void setToda(int toda) {
    if (toda < 0) {
      System.err.println("TODA should not be negative (provided: " + toda + "). Used TODA = 0.");
      toda = 0;
    }
    this.toda = toda;
  }

  public int getAsda() {
    return asda;
  }

  public void setAsda(int asda) {
    if (asda < 0) {
      System.err.println("ASDA should not be negative (provided: " + asda + "). Used ASDA = 0.");
      asda = 0;
    }
    this.asda = asda;
  }

  public int getLda() {
    return lda;
  }

  public void setLda(int lda) {
    if (lda < 0) {
      System.err.println("LDA should not be negative (provided: " + lda + "). Used LDA = 0.");
      lda = 0;
    }
    this.lda = lda;
  }

  public int getDisplacedThreshold() {
    return displacedThreshold;
  }

  public void setDisplacedThreshold(int displacedThreshold) {
    if (displacedThreshold < 0) {
      System.err.println(
          "Displaced Threshold should not be negative (provided: " + displacedThreshold
              + "). Used Displaced Threshold = 0.");
      displacedThreshold = 0;
    }
    this.displacedThreshold = displacedThreshold;
  }

  public String getOpposite() {
    var dir = this.getRunwayDesignator().substring(0, 2);
    var dirInt = Integer.parseInt(dir);

    if (dirInt <= 18) {
      dirInt += 18;
    } else {
      dirInt -= 18;
    }

    var end = "";
    if (this.getRunwayDesignator().length() > 2) {
      end = switch (this.getRunwayDesignator().charAt(2)) {
        case 'L' -> "R";
        case 'C' -> "C";
        case 'R' -> "L";
        default -> "?";
      };
    }

    return "" + dirInt + end;
  }

  public int getBearing() {
    return Integer.parseInt(this.runwayDesignator.substring(0, 2)) * 10;
  }

  @Override
  public int hashCode() {
    return airport.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    }
    return airport.equals(((Runway) other).airport);
  }
}
