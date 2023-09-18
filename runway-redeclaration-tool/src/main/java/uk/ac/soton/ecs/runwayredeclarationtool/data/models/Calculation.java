package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("calculation")
public class Calculation {

  @XStreamAlias("airport")
  private String airport;
  private String runwayDesignator;
  private String tora;
  private String toda;
  private String asda;
  private String lda;
  private Obstacle obstacle;
  private User user;
  private String takeOffDirection;
  private Aircraft aircraft;


  public String getAirport() {
    return airport;
  }

  public String getRunwayDesignator() {
    return runwayDesignator;
  }

  public String getTora() {
    return tora;
  }

  public String getToda() {
    return toda;
  }

  public String getAsda() {
    return asda;
  }

  public String getLda() {
    return lda;
  }

  public User getUser() {
    return user;
  }

  public Obstacle getObstacle() {
    return obstacle;
  }

  public String getTakeOffDirection() {
    return takeOffDirection;
  }

  public Calculation(String airport, String runwayDesignator, String tora, String toda,
      String asda, String lda, Obstacle obstacle, User user, String takeOffDirection,
      Aircraft aircraft) {
    this.airport = airport;
    this.runwayDesignator = runwayDesignator;
    this.tora = tora;
    this.toda = toda;
    this.asda = asda;
    this.lda = lda;
    this.obstacle = obstacle;
    this.user = user;
    this.takeOffDirection = takeOffDirection;
    this.aircraft = aircraft;
  }

  public Aircraft getAircraft() {
    return aircraft;
  }
}
