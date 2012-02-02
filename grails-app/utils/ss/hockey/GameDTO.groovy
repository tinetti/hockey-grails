/*
 * Copyright SwerveSoft, Inc. 2010
 */

package ss.hockey

/**
 *
 * @author tinetti
 */
class GameDTO {

  Game game
  List<PlayerDTO> players

  Long getId() {
    return game.id
  }

  Date getDate() {
    return game.date
  }

  String getNotes() {
    return game.notes
  }

  GameVersion getGameVersion() {
    return game.gameVersion
  }

  GameLength getGameLength() {
    return game.gameLength
  }

  TeamStats getHomeStats() {
    return game.homeStats
  }

  TeamStats getAwayStats() {
    return game.awayStats
  }

  Set<GamePlayer> getGamePlayers() {
    return game.gamePlayers
  }

  Double getHomeRating() {
    return RatingUtils.getRating(homeStats, awayStats, gameLength)
  }

  Double getAwayRating() {
    return RatingUtils.getRating(awayStats, homeStats, gameLength)
  }

  Double getHomeScoreRating() {
    return RatingUtils.getScoreRating(homeStats, awayStats, gameLength == GameLength.Shootout)
  }

  Double getAwayScoreRating() {
    return RatingUtils.getScoreRating(awayStats, homeStats, gameLength == GameLength.Shootout)
  }

  Double getHomeHitsRating() {
    return RatingUtils.getHitsRating(homeStats, awayStats, homePlayerIds.size(), awayPlayerIds.size())
  }

  Double getAwayHitsRating() {
    return RatingUtils.getHitsRating(awayStats, homeStats, awayPlayerIds.size(), homePlayerIds.size())
  }

  Double getHomeTimeOnAttackRating() {
    return RatingUtils.getTimeOnAttackRating(homeStats, awayStats)
  }

  Double getAwayTimeOnAttackRating() {
    return RatingUtils.getTimeOnAttackRating(awayStats, homeStats)
  }

  Double getHomeActualDrinks() {
    return homeStats?.drinks
  }
  
  Double getAwayActualDrinks() {
    return awayStats?.drinks
  }
  
  Double getHomeEstimatedDrinks() {
    return RatingUtils.getDrinks(homeStats, awayStats, game.notes)
  }

  Double getAwayEstimatedDrinks() {
    return RatingUtils.getDrinks(awayStats, homeStats, game.notes)
  }
  
  Double getHomeDrinks() {
    return homeActualDrinks ?: homeEstimatedDrinks
  }
  
  Double getAwayDrinks() {
    return awayActualDrinks ?: awayEstimatedDrinks
  }
  
  String getHomeDrinksDescription() {
    return homeActualDrinks ? "${homeActualDrinks as Long}":  "~${homeEstimatedDrinks as Long}"
  }
  
  String getAwayDrinksDescription() {
    return awayActualDrinks ? "${awayActualDrinks as Long}":  "~${awayEstimatedDrinks as Long}"
  }

  synchronized List<PlayerDTO> getPlayers() {
    if (!players) {
      List<PlayerDTO> temp = []
      gamePlayers.each { gamePlayer ->
        temp.add(new PlayerDTO(player: gamePlayer.player))
      }
      players = temp
    }
    return players
  }

  Set<Long> getPlayerIds() {
    Set<Long> playerIds = new HashSet<Long>()
    gamePlayers.each { gamePlayer ->
      playerIds.add(gamePlayer.player.id)
    }
    return playerIds
  }

  List<Player> getHomePlayers() {
    List<Player> players = new ArrayList<Player>()
    gamePlayers.each { gamePlayer ->
      if (gamePlayer.home) {
        players.add(gamePlayer.player)
      }
    }
    players.sort()
    return players
  }

  Set<Long> getHomePlayerIds() {
    Set<Long> playerIds = new HashSet<Long>()
    gamePlayers.each { gamePlayer ->
      if (gamePlayer.home) {
        playerIds.add(gamePlayer.player.id)
      }
    }
    return playerIds
  }

  Collection<Player> getAwayPlayers() {
    List<Player> players = new ArrayList<Player>()
    gamePlayers.each { gamePlayer ->
      if (!gamePlayer.home) {
        players.add(gamePlayer.player)
      }
    }
    players.sort()
    return players
  }

  Set<Long> getAwayPlayerIds() {
    Set<Long> playerIds = new HashSet<Long>()
    gamePlayers.each { gamePlayer ->
      if (!gamePlayer.home) {
        playerIds.add(gamePlayer.player.id)
      }
    }
    return playerIds
  }

  Boolean matchesPlayerIds(GameFilter gameFilter) {
    if (gameFilter.excludePlayerIdFilter && !gameFilter.excludePlayerIdFilter.isEmpty() && gameFilter.excludePlayerIdFilter.matches(playerIds)) {
      return false
    }

    if (gameFilter.gamePlayerIdFilter && !gameFilter.gamePlayerIdFilter.matches(playerIds)) {
      return false
    }

    if (gameFilter.team1PlayerIdFilter) {
      if (gameFilter.team2PlayerIdFilter) {
        return (gameFilter.team1PlayerIdFilter.matches(awayPlayerIds) && gameFilter.team2PlayerIdFilter.matches(homePlayerIds)) || (gameFilter.team1PlayerIdFilter.matches(homePlayerIds) && gameFilter.team2PlayerIdFilter.matches(awayPlayerIds))
      }

      return gameFilter.team1PlayerIdFilter.matches(awayPlayerIds) || gameFilter.team1PlayerIdFilter.matches(homePlayerIds)
    }

    return true
  }

  @Override
  boolean equals(Object o) {
    if (o == null || !(o instanceof GameDTO)) {
      return false
    }
    return this.game == ((GameDTO)o).game
  }

  @Override
  public int hashCode() {
    return game.hashCode();
  }

  String toString() {
      return "GameDTO[${id}: ${awayPlayers*.name} vs ${homePlayers*.name}: ${homeStats?.score}-${awayStats?.score}]"
  }

}

