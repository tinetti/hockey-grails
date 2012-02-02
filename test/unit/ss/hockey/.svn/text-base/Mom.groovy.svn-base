package ss.hockey

import java.util.Date;
import java.util.Set;

class Mom {
  
  static Player johnny = new Player(id:1, name:'Johnny')
  static Player pauly = new Player(id:2, name:'Pauly')
  static Player chas = new Player(id:3, name:'Chas')
  static Player alex = new Player(id:4, name:'Alex')
  static Player other = new Player(id:Player.OTHER_PLAYER_ID, name:'Other')
  
  static List<Player> players = [johnny, pauly, chas, alex, other]
  
  static int counter = 0
  
  static Game createGame() {
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
    game.notes = "test game: ${counter}"
    return game
  }
  
  static TeamStats createTeamStats() {
    NhlTeam nhlTeam = NhlTeam.values()[counter++ % NhlTeam.values().length]
    NhlTeamRating teamRating = createNhlTeamRating(nhlTeam)
    Integer score = counter++ % 15
    Integer shots = counter++ % 25
    Integer hits = counter++ % 25
    Integer timeOnAttack = counter++ % 800
    return new TeamStats(id:counter++, team:nhlTeam, teamRating:teamRating, score:score, shots:shots, hits:hits, timeOnAttack:timeOnAttack)
  }
  
  static NhlTeamRating createNhlTeamRating(NhlTeam nhlTeam) {
    return new NhlTeamRating(id:counter++, team:nhlTeam, offense:counter++, defense:counter++, goalie:counter++)
  }
  
  static GamePlayer createGamePlayer(Game game) {
    Player player = players[counter++ % players.size()]
    Boolean home = counter++ % 2 == 0
    return new GamePlayer(id:counter++, game:game, player:player, home:home)
  }
  
}