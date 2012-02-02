package ss.hockey

import groovy.util.GroovyTestCase

import java.util.List

class HockeyTestCase extends GroovyTestCase {

  int counter

  List<Player> players

  Map<NhlTeam, NhlTeamRating> nhlTeamRatings

  protected void setUp() {
    super.setUp()
    counter = 0

    // save players
    players = [
      new Player(name:'Johnny'),
      new Player(name:'Pauly'),
      new Player(name:'Chas'),
      new Player(name:'Alex')
    ]
    players.each { Player player ->
      assertNotNull(player.save())
    }

    // save nhl team ratings
    nhlTeamRatings = [:]
    NhlTeam.values().each { NhlTeam nhlTeam ->
      Integer offense = (counter++ % 10) + 80
      Integer defense = (counter++ % 10) + 80
      Integer goalie = (counter++ % 10) + 80
      Date date = new Date()
      NhlTeamRating nhlTeamRating = new NhlTeamRating(team:nhlTeam, date:date, offense:offense, defense:defense, goalie:goalie)
      
      assertNotNull(nhlTeamRating.save())
      
      nhlTeamRatings[nhlTeam] = nhlTeamRating
    }
  }

  protected void tearDown() {
    super.tearDown()
  }

  Game createGame() {
    Game game = new Game()
    game.date = new Date()
    game.gameLength = GameLength.Regulation
    game.gameVersion = GameVersion.getLatest()
    game.homeStats = createTeamStats()
    game.awayStats = createTeamStats()
    game.gamePlayers << createGamePlayer(game)
    game.gamePlayers << createGamePlayer(game)
    game.gamePlayers << createGamePlayer(game)
    game.gamePlayers << createGamePlayer(game)
    game.notes = "test game"
    return game
  }

  TeamStats createTeamStats() {
    NhlTeam nhlTeam = NhlTeam.values()[counter++ % NhlTeam.values().length]
    Integer score = counter++ % 15
    Integer shots = counter++ % 25
    Integer hits = counter++ % 25
    Integer timeOnAttack = counter++ % 800
    return new TeamStats(team:nhlTeam, score:score, shots:shots, hits:hits, timeOnAttack:timeOnAttack)
  }

  GamePlayer createGamePlayer(Game game) {
    Player player = players[counter++ % players.size()]
    Boolean home = counter % 2 == 0
    return new GamePlayer(game:game, player:player, home:home)
  }
}
