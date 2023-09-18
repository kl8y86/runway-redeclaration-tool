package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

public enum Obstacles {
  ForeignObjDebris(1, 1),
  Aircraft(15, 40),
  LargeAircraft(20, 80),
  Vehicle(2, 10),
  Plant(4, 15),
  StandardWorkSite(10, 100);

  private int height;
  private int width;

  Obstacles(int height, int width) {
    this.height = height;
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
}
