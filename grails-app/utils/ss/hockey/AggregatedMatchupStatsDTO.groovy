package ss.hockey

/**
 * Created by IntelliJ IDEA.
 * User: tinetti
 * Date: Sep 4, 2010
 * Time: 1:08:28 PM
 * To change this template use File | Settings | File Templates.
 */
class AggregatedMatchupStatsDTO {
  MatchupTypeDTO matchupType
  PlayerDTO player

  String toString() {
    return "AggregatedMatchupStats[type:${matchupType}, player:${player}"
  }
}
