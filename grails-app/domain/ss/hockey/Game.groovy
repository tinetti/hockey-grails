package ss.hockey

import org.codehaus.groovy.grails.validation.Validateable

class Game {

  static constraints = {
    date(nullable: false, unique: true)
    gameLength(nullable: false)
    gameVersion(nullable: false)
    homeStats(nullable: false)
    awayStats(nullable: false, validator: { TeamStats val, Game obj ->
      if (val.score == obj.homeStats.score) {
        return ['tie']
      }

      if (val.team == obj.homeStats.team && val.team != NhlTeam.OTHER) {
        return ['sameTeam']
      }
    })
    notes(nullable: true)
    gamePlayers(validator: { Set<GamePlayer> val, Game obj ->
      // duplicated in CreateGameCommand
      Set<Long> awayPlayerIds = new HashSet<Long>(obj.awayPlayerIds)
      Set<Long> homePlayerIds = new HashSet<Long>(obj.homePlayerIds)

      if (awayPlayerIds.isEmpty()) {
        return ['awayPlayerIds']
      }
      if (homePlayerIds.isEmpty()) {
        return ['homePlayerIds']
      }

      awayPlayerIds.retainAll(homePlayerIds)
      awayPlayerIds.remove(Player.OTHER_PLAYER_ID)

      if (awayPlayerIds.size() > 0) {
        return ['samePlayers']
      }
    })
  }

  static hasMany = [gamePlayers:GamePlayer]
  static embedded = ['homeStats', 'awayStats']

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
  GameLength gameLength = GameLength.Regulation
  GameVersion gameVersion = GameVersion.getLatest()

  Set<GamePlayer> gamePlayers = new HashSet<GamePlayer>()

  TeamStats homeStats
  TeamStats awayStats

  String notes

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

  String toString() {
    return "Game[date=${date}]"
  }
}

@Validateable
class TeamStats {

  static constraints = {
    team(nullable: false)
    score(nullable: false)
    shots(nullable: true)
    hits(nullable: true)
    timeOnAttack(nullable: true)
    passingPercentage(nullable: true)
  }

  static transients = [
    'timeOnAttackString',
    'shotPercentage'
  ]

  NhlTeam team

  Integer score

  Integer shots
  Integer hits
  Integer timeOnAttack
  Integer passingPercentage

  String getTimeOnAttackString() {
    return DateUtils.secondsToString(timeOnAttack)
  }

  void setTimeOnAttackString(String toa) {
    timeOnAttack = DateUtils.stringToSeconds(toa)
  }

  Double getShotPercentage() {
    return shots ? score / (shots as Double) : 0
  }

  String toString() {
    return "TeamStats[score=${score}, team=${team}, shots=${shots}, hits=${hits}, timeOnAttack=${timeOnAttackString}]"
  }
}
