/*
 * Copyright SwerveSoft, Inc. 2010
 */
package ss.hockey;

/**
 * @author tinetti
 */
public enum GameVersion {

  NHL12("NHL 12"), NHL11("NHL 11"), NHL10("NHL 10"), NHL09("NHL 09");
  private final String name;

  private GameVersion(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static GameVersion getLatest() {
    return NHL12;
  }
}
