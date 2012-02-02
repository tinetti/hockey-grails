package ss.hockey

import java.util.Date;
import java.util.List;
import java.util.Set;

import grails.test.*

class GameTests extends HockeyTestCase {

  void testSaveGame() {
    Game game = createGame()

    game.save(flush:true)

    Game game2 = Game.read(game.id)

    assertGameEquals(game, game2)
  }

  static void assertGameEquals(Game g1, Game g2) {
    assertEquals g1.id, g2.id
    assertEquals g1.date, g2.date
    assertEquals g1.gameLength, g2.gameLength
    assertEquals g1.gameVersion, g2.gameVersion
    assertTeamStatsEquals g1.homeStats, g2.homeStats
    assertTeamStatsEquals g1.awayStats, g2.awayStats
    assertEquals g1.notes, g2.notes
    
    assertEquals g1.gamePlayers, g2.gamePlayers

    for (GamePlayer gp1 : g1.gamePlayers) {
      for (GamePlayer gp2 : g2.gamePlayers) {
        if (gp1.player == gp2.player) {
          assertGamePlayerEquals gp1, gp2
        }
      }
    }
  }
  
  static void assertTeamStatsEquals(TeamStats ts1, TeamStats ts2) {
    assertEquals ts1.team, ts2.team
    assertEquals ts1.score, ts2.score
    assertEquals ts1.shots, ts2.shots
    assertEquals ts1.hits, ts2.hits
    assertEquals ts1.timeOnAttack, ts2.timeOnAttack
  }
  
  static void assertGamePlayerEquals(GamePlayer gp1, GamePlayer gp2) {
    assertEquals gp1.id, gp2.id
    assertEquals gp1.home, gp2.home
  }
}
