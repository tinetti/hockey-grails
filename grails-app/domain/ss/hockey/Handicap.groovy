package ss.hockey

import java.util.Date;
import java.util.Set;

class Handicap {

  static hasMany = [gamePlayers:GamePlayer]

  static mapping = {
    sort "date":"desc"
    gamePlayers lazy:false
  }

  static transients = [
    'awayPlayerIds',
    'homePlayerIds',
    'awayPlayers',
    'homePlayers'
  ]

  Long id
  Long version

  Date date
  
  Long value

  Set<GamePlayer> gamePlayers = new HashSet<GamePlayer>()


  Set<Player> getAwayPlayers() {
    Set<Player> players = new HashSet<Player>()
    gamePlayers.each { GamePlayer gp ->
      if (!gp.home) {
        players.add(gp.player)
      }
    }
    return players
  }

  Set<Player> getHomePlayers() {
    Set<Player> players = new HashSet<Player>()
    gamePlayers.each { GamePlayer gp ->
      if (gp.home) {
        players.add(gp.player)
      }
    }
    return players
  }

  Set<Long> getAwayPlayerIds() {
    return awayPlayers.collect { it.id }
  }

  Set<Long> getHomePlayerIds() {
    return homePlayers.collect { it.id }
  }
}
