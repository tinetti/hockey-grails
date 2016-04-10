package ss.hockey

/**
 * SwerveSoft
 * User: tinetti
 * Date: Oct 3, 2010
 * Time: 5:17:52 PM
 */
class RecordGameDTO implements Comparable {
  Double value
  GameDTO game
  boolean homeTeam
  def valueStringConverter

  int compareTo(Object that) {
    double diff = value - that.value
    if (diff == 0) {
      return that.game.game.date - this.game.game.date
    } else {
      return diff > 0 ? 1 : -1
    }
  }

  String toString() {
    return "RecordGame: ${value} - ${game.date}"
  }

  String getValueAsString() {
    try {
      return valueStringConverter(value)
    } catch (Exception e) {
      new Exception("unable to convert value: ${value}", e).printStackTrace()
      return value as String
    }
  }
}
