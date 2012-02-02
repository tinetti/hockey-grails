package ss.hockey;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author tinetti
 */
public enum GameLength {

  Regulation("Regulation"), Overtime("Overtime"), DoubleOvertime("Double OT"), TripleOvertime("Triple OT"), QuadrupleOvertime("Quadruple OT"), FivePlusOvertimes("5+ Overtimes"), Shootout("Shootout");

  private final String name;

  private GameLength(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
