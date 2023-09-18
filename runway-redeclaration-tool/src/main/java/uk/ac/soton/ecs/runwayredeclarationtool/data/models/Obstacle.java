package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

public class Obstacle {

  // TODO stub class
  private int height;
  private int length;
  private int distanceThreshold;

  public Obstacle(int height, int length, int distanceThreshold) {
    this.height = height;
    this.length = length;
    this.distanceThreshold = distanceThreshold;
  }

  public Obstacle(int height, int length) {
    this.height = height;
    this.length = length;
  }

  @Override
  public String toString() {
    return "obstacle of height " + height + ", length " + length;
  }

  public int getHeight() {
    return height;
  }

  public int getLength() {
    return length;
  }

  public int getDistanceThreshold() {
    return distanceThreshold;
  }
}
