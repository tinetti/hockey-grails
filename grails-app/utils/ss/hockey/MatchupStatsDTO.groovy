package ss.hockey

/**
 * Created by IntelliJ IDEA.
 * User: tinetti
 * Date: Sep 4, 2010
 * Time: 1:09:28 PM
 * To change this template use File | Settings | File Templates.
 */
class MatchupStatsDTO {
  Game game
  List<Player> forPlayers
  List<Player> againstPlayers
  TeamStats forStats
  TeamStats againstStats

  String toString() {
    return "game: ${game}, forPlayers: ${forPlayers}, againstPlayers: ${againstPlayers}"
  }
}
