package ss.hockey

class PlayerService {

  static transactional = true

  GameService gameService

  List<PlayerDTO> findPlayers(GameFilter gameFilter, List<GameDTO> games = null) {
    List<Player> players = Player.list()

    if (gameFilter?.gamePlayerIdFilter?.ids) {
      List<Player> temp = []
      players.each { Player player ->
        if (gameFilter.gamePlayerIdFilter.contains(player.id)) {
          temp << player
        }
      }
      players = temp
    }

    // put 'Other' at the end
    Player other = Player.get(Player.OTHER_PLAYER_ID)
    if (other) {
      int index = -1
      players.eachWithIndex { player, i ->
        if (player.id == other.id) {
          index = i
        }
      }
      if (index >= 0) {
        players.remove(index)
        players.add(other)
      }
    }

    List<PlayerDTO> playerDTOs = players.collect { Player player -> new PlayerDTO(player:player) }

    if (gameFilter) {
      List<GameDTO> gameDTOs = games ?: gameService.findGames(gameFilter).games
      Map<PlayerDTO, List<GameDTO>> playerDTOToListOfGameDTOs = collectGames(playerDTOs, gameDTOs)

      // might have to trim some games out...
      if (gameFilter?.maxGamesPerPlayer) {
        playerDTOToListOfGameDTOs.values().each { List<GameDTO> temp ->
          while (temp.size() > gameFilter.maxGamesPerPlayer) {
            temp.remove(0)
          }
        }
      }

      aggregateGameStats(playerDTOToListOfGameDTOs)
    }

    // sort by win percentage
    playerDTOs.sort { PlayerDTO playerDTO1, PlayerDTO playerDTO2 ->
      def v1 = playerDTO1.winPercentage
      def v2 = playerDTO2.winPercentage
      def mod = -1
      if (v1 == null) {
        return v2 == null ? 0 : 0 - mod
      } else if (v2 == null) {
        return mod
      } else {
        return v1.compareTo(v2) * mod
      }
    }

    if (gameFilter) {
      playerDTOs.retainAll { PlayerDTO it ->
        return it.gamesWonIds.size() > 0 || it.gamesLostIds.size() > 0
      }
    }

    return playerDTOs
  }

  PlayerDetailsDTO getPlayerDetails(GameFilter gameFilter) {
    // save the players we're interested in
    PlayerIdFilter playerIdFilter = gameFilter.gamePlayerIdFilter
    List<PlayerDTO> playerDTOs = playerIdFilter.ids.collect { Long id ->
      return new PlayerDTO(player:Player.get(id))
    }

    // copy the GameFilter
    gameFilter = new GameFilter(gameVersion:gameFilter.gameVersion, fromDate:gameFilter.fromDate, toDate:gameFilter.toDate)

    // create a list of other players to match up with/against
    List<Player> otherPlayers = Player.list()
    List<PlayerDTO> otherPlayerDTOs = Player.list().collect { Player player ->
      return new PlayerDTO(player:player)
    }
    otherPlayerDTOs.removeAll(playerDTOs)

    PlayerDetailsDTO playerDetailsDTO = new PlayerDetailsDTO(playerDTOs:playerDTOs)
    PlayerIdFilter team1PlayerIdFilter
    PlayerIdFilter team2PlayerIdFilter

    MatchupListDTO teammateMatchupList = new MatchupListDTO(description:"With Player:")
    MatchupListDTO opponentMatchupList = new MatchupListDTO(description:"Against Player:")
    Set<Long> ids

    // solo
    ids = new HashSet<Long>(playerIdFilter.ids)
    team1PlayerIdFilter = new PlayerIdFilter(ids:ids, matchingStrategy:MatchingStrategy.EXACT)
    team2PlayerIdFilter = new PlayerIdFilter()
    gameFilter.team1PlayerIdFilter = team1PlayerIdFilter
    gameFilter.team2PlayerIdFilter = team2PlayerIdFilter
    teammateMatchupList.matchupStats.addAll(gameService.getMatchupList(gameFilter, [
      new AllMatchupTypeDTO(description:"(Alone)")
    ]).matchupStats)

    otherPlayerDTOs.each { PlayerDTO otherPlayerDTO ->
      // teammate
      ids = new HashSet<Long>()
      ids.addAll(playerIdFilter.ids)
      ids.add(otherPlayerDTO.id)
      team1PlayerIdFilter = new PlayerIdFilter(ids:ids, matchingStrategy:MatchingStrategy.ALL)
      team2PlayerIdFilter = new PlayerIdFilter()
      gameFilter.team1PlayerIdFilter = team1PlayerIdFilter
      gameFilter.team2PlayerIdFilter = team2PlayerIdFilter
      teammateMatchupList.matchupStats.addAll(gameService.getMatchupList(gameFilter, [
        new AllMatchupTypeDTO(description:"${otherPlayerDTO.name}")
      ]).matchupStats)

      // opponent
      team1PlayerIdFilter = new PlayerIdFilter(ids:playerIdFilter.ids, matchingStrategy:MatchingStrategy.ALL)
      team2PlayerIdFilter = new PlayerIdFilter(ids:new HashSet<Long>([otherPlayerDTO.id]), matchingStrategy:MatchingStrategy.ALL)
      gameFilter.team1PlayerIdFilter = team1PlayerIdFilter
      gameFilter.team2PlayerIdFilter = team2PlayerIdFilter
      opponentMatchupList.matchupStats.addAll(gameService.getMatchupList(gameFilter, [
        new AllMatchupTypeDTO(description:"${otherPlayerDTO.name}")
      ]).matchupStats)
    }
    teammateMatchupList.matchupStats.sort { AggregatedMatchupStatsDTO it -> 0 - it.player.winPercentage }
    opponentMatchupList.matchupStats.sort { AggregatedMatchupStatsDTO it -> 0 - it.player.winPercentage }

    playerDetailsDTO.matchupLists << teammateMatchupList
    playerDetailsDTO.matchupLists << opponentMatchupList
    
    // home/away
    gameFilter.team1PlayerIdFilter = null
    gameFilter.team2PlayerIdFilter = null
    gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids:ids, matchingStrategy:MatchingStrategy.ANY)
    gameFilter.home = true
    playerDetailsDTO.matchupLists << gameService.getMatchupList(gameFilter, [new HomeAwayMatchupTypeDTO(home:true, playerIds:ids)])
    gameFilter.home = false
    playerDetailsDTO.matchupLists << gameService.getMatchupList(gameFilter, [new HomeAwayMatchupTypeDTO(home:false, playerIds:ids)])

    return playerDetailsDTO
  }

  PlayerDTO getAveragePlayer(List<PlayerDTO> playerDTOs) {
    Player averagePlayer = new Player(name: "Total")
    PlayerDTO averagePlayerDTO = new PlayerDTO(player: averagePlayer)

    Long wStreakCount = 0
    Long wStreakSum = 0
    Long lStreakCount = 0
    Long lStreakSum = 0

    playerDTOs.each { PlayerDTO playerDTO ->
      List<String> collectionProperties = [
        'gameIds',
        'gamesWonIds',
        'gamesLostIds'
      ]
      collectionProperties.each { prop ->
        averagePlayerDTO[prop].addAll(playerDTO[prop])
      }

      List<String> longProperties = [
        'goalsFor',
        'goalsAgainst',
        'goalsWithShotsFor',
        'goalsWithShotsAgainst',
        'shotsFor',
        'shotsForGames',
        'shotsAgainst',
        'shotsAgainstGames',
        'hitsFor',
        'hitsForGames',
        'hitsAgainst',
        'hitsAgainstGames',
        'timeOnAttackFor',
        'timeOnAttackForGames',
        'timeOnAttackAgainst',
        'timeOnAttackAgainstGames',
        'ratingFor',
        'ratedGamesFor',
        'ratingAgainst',
        'ratedGamesAgainst',
        'drinksFor',
        'drinksGamesFor',
        'drinksAgainst',
        'drinksGamesAgainst'
      ]
      longProperties.each { prop ->
        averagePlayerDTO[prop] += playerDTO[prop]
      }

      playerDTO.streaks.each {
        if (it > 0) {
          wStreakCount++
          wStreakSum += it
        } else {
          lStreakCount++
          lStreakSum += it
        }
      }
    }

    averagePlayerDTO.streaks.add(wStreakCount == 0D ? 0D : ((Double) wStreakSum / (Double) wStreakCount))
    averagePlayerDTO.streaks.add(lStreakCount == 0D ? 0D : ((Double) lStreakSum / (Double) lStreakCount))
    averagePlayerDTO.streaks.add(0L)

    return averagePlayerDTO
  }

  Map<PlayerDTO, List<GameDTO>> collectGames(List<PlayerDTO> playerDTOs, List<GameDTO> gameDTOs) {
    Map<Long, PlayerDTO> idToPlayerDTOs = [:]
    playerDTOs.each { playerDTO ->
      idToPlayerDTOs[playerDTO.id] = playerDTO
    }

    Map<PlayerDTO> playerDTOToListOfGameDTOs = [:]
    gameDTOs.each { GameDTO gameDTO ->
      gameDTO.players.each { PlayerDTO playerDTO ->
        // replace with the one in our existing map
        playerDTO = idToPlayerDTOs.get(playerDTO.id)

        if (playerDTO) {
          List<GameDTO> temp = playerDTOToListOfGameDTOs.get(playerDTO)
          if (temp == null) {
            temp = []
            playerDTOToListOfGameDTOs.put(playerDTO, temp)
          }
          temp.add(gameDTO)
        }
      }
    }
    return playerDTOToListOfGameDTOs
  }

  HockeySessionListDTO getHockeySessionList(Map params) {
    List<GameDTO> allGames = gameService.findGames(new GameFilter()).games

    String str = params?.max ?: "10"
    Integer max = Integer.valueOf(str)

    str = params?.offset ?: "0"
    Integer offset = Integer.valueOf(str)

    Set<Date> allDates = new HashSet()
    Map<Date, List<GameDTO>> gameDTOListByDate = new LinkedHashMap() // Date -> List<GameDTO>
    for (GameDTO game : allGames) {
      boolean process
      Date date = getHockeySessionDate(game.game.date)
      if (gameDTOListByDate[date]) {
        // already know we process it
        process = true
      } else {
        allDates.add(date)
        process = offset < allDates.size() && offset + max >= allDates.size()
      }

      if (process) {
        allDates.add(date)
        List<GameDTO> games = gameDTOListByDate[date]
        if (games == null) {
          games = new ArrayList<GameDTO>()
          gameDTOListByDate[date] = games
        }

        games << game
      }
    }

    List<HockeySessionDTO> hockeySessions = new ArrayList<HockeySessionDTO>()
    gameDTOListByDate.each { key, value ->
      // Date, List<GameDTO>
      List<GameDTO> games = new ArrayList<GameDTO>(value)
      List<PlayerDTO> players = aggregateGameStats(games)

      HockeySessionDTO hockeySession = new HockeySessionDTO()
      hockeySession.date = key
      hockeySession.players = players
      games.each {
        hockeySession.gameIds << it.game.id
      }

      hockeySessions << hockeySession
    }

    return new HockeySessionListDTO(hockeySessions:hockeySessions, count:allDates.size())
  }

  List<PlayerDTO> aggregateGameStats(List<GameDTO> games) {
    Map<Player, List<GameDTO>> gamesByPlayers = [:]
    games.each { GameDTO game ->
      game.players.each { PlayerDTO player ->
        List<GameDTO> playerGames = gamesByPlayers[player.player]
        if (playerGames == null) {
          playerGames = []
          gamesByPlayers[player.player] = playerGames
        }

        playerGames << game
      }
    }

    Map<PlayerDTO, List<GameDTO>> gamesByPlayerDTO = [:]
    gamesByPlayers.each { Player player, List<GameDTO> value ->
      gamesByPlayerDTO[new PlayerDTO(player:player)] = value
    }

    return aggregateGameStats(gamesByPlayerDTO)
  }

  List<PlayerDTO> aggregateGameStats(Map<PlayerDTO, List<GameDTO>> playerDTOToListOfGameDTOs) {
    playerDTOToListOfGameDTOs.each { PlayerDTO playerDTO, List<GameDTO> gameDTOs ->

      // create a copy of the list and order them by date
      gameDTOs = new ArrayList<GameDTO>(gameDTOs)
      gameDTOs.sort { it.date.time }

      gameDTOs.each { GameDTO gameDTO -> playerDTO << gameDTO }
    }

    List<PlayerDTO> players = new ArrayList(playerDTOToListOfGameDTOs.keySet())
    sortPlayers(players)
    return players
  }

  void sortPlayers(List<PlayerDTO> players) {
    // sort by win percentage then rating
    players.sort { PlayerDTO player1, PlayerDTO player2 ->
      def diff = player2.winPercentage - player1.winPercentage
      if (diff > 0) {
        return 1
      } else if (diff < 0) {
        return -1
      }
      return player2.ratingFor - player1.ratingFor
    }
  }

  static Date getHockeySessionDate(Date date) {
    Calendar c = GregorianCalendar.getInstance()
    c.setTime(date)
    if (c.get(Calendar.HOUR_OF_DAY) < 12) {
      c.add(Calendar.DATE, -1)
    }
    c.set(Calendar.MILLISECOND, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.HOUR_OF_DAY, 12)

    Date sessionDate = c.getTime()

    return sessionDate
  }
}
