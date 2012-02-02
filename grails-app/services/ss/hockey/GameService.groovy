package ss.hockey

class GameService {

  static transactional = true

  GameListDTO findGames(GameFilter gameFilter) {
    List<Game> games
    if (gameFilter.gameIds) {
      List<Long> gameIds = new ArrayList<Long>(gameFilter.gameIds)
      gameIds.sort() { Long it -> -it }
      log.debug "findGames with gameIds: ${gameIds}"
      games = []
      gameIds.each { Long it ->
        Game game = Game.get(it)
        if (game) {
          games << game
        } else {
          log.error "unable to find game: ${it}"
        }
      }
    } else {
      List<Object> params = []

      StringBuffer hql = new StringBuffer('from ').append(Game.class.name)
      hql.append(' where 1=1')

      if (gameFilter.gameVersion) {
        hql.append(' and gameVersion = ?')
        params.add(gameFilter.gameVersion)
      }

      if (gameFilter.fromDate) {
        hql.append(' and date >= ?')
        params.add(gameFilter.fromDate)
      }

      if (gameFilter.toDate) {
        hql.append(' and date <= ?')
        params.add(gameFilter.toDate)
      }

      hql.append(' order by date desc')

      games = Game.findAll(hql.toString(), params)
    }

    if (log.debugEnabled) {
      log.debug "findGames - gameFilter:${gameFilter} - filtering ${games.size()} games..."
    }

    Long offset = gameFilter.offset ?: 0
    Long max = (gameFilter.max ?: Long.MAX_VALUE) as Long
    Long skipped = 0
    Long gamesFound = 0

    List<GameDTO> gameDTOs = []
    games.each { game ->
      GameDTO gameDTO = new GameDTO(game: game)
      if (gameDTO.matchesPlayerIds(gameFilter)) {
        gamesFound++
        if (offset == skipped) {
          if (gameDTOs.size() < max) {
            gameDTOs << gameDTO
          }
        } else {
          skipped++
        }
      }
    }

    log.debug "findGames - returning ${gameDTOs.size()} games"

    return new GameListDTO(games: gameDTOs, max: gamesFound)
  }

  GamePredictionDTO predictGame(GameFilter gameFilter) {
    MatchupListDTO matchupList = getMatchupList(gameFilter)

    /* map from GamePredictionDTO property -> PlayerDTO property */
    Map<String, String> properties = [
          'awayScore':'goalsForPerGame',
          'homeScore':'goalsAgainstPerGame',
          'awayShots':'shotsForPerGame',
          'homeShots':'shotsAgainstPerGame',
          'awayHits':'hitsForPerGame',
          'homeHits':'hitsAgainstPerGame',
          'awayTimeOnAttack':'timeOnAttackForPerGame',
          'homeTimeOnAttack':'timeOnAttackAgainstPerGame',
          'awayPassingPercentage':'passingPercentageFor',
          'homePassingPercentage':'passingPercentageAgainst',
          'awayPenaltyMinutes':'penaltyMinutesForPerGame',
          'homePenaltyMinutes':'penaltyMinutesAgainstPerGame',
          'awayRating':'ratingForPerGame',
          'homeRating':'ratingAgainstPerGame',
          'awayDrinks':'drinksForPerGame',
          'homeDrinks':'drinksAgainstPerGame'
        ]
    Map<String, Double> values = [:]
    Map<String, Long> weights = [:]
    properties.each { String predictionProperty, String playerProperty ->
      values[predictionProperty] = 0D
      weights[predictionProperty] = 0L
    }

    Long totalGames = 0
    matchupList.matchupStats.each { AggregatedMatchupStatsDTO aggMatchupStats ->
      Long gameCount = aggMatchupStats.player.gameIds.size()
      Long weight = aggMatchupStats.matchupType.weight
      if (gameCount > 0 && weight > 0) {
        totalGames += gameCount
        properties.each { String predictionProperty, String playerProperty ->
          def value = aggMatchupStats.player[playerProperty]
          if (value != null) {
            values[predictionProperty] += (value * weight * gameCount)
            weights[predictionProperty] += (weight * gameCount)
          }
        }
      }
    }

    values.each { String key, Double value ->
      if (weights[key] > 0) {
        values[key] /= weights[key]
      }
    }


    String description = getPredictionDescription(gameFilter, totalGames)
    values['description'] = description
    values['matchupList'] = matchupList
    return new GamePredictionDTO(values)
  }

  Long getDrinks(Player player, Date date) {
    // get all games in the last 12 hours
    Date twelveHoursAgo = new Date(date.time - (1000 * 60 * 60 * 12))
    Date oneMinuteAgo = new Date(date.time - (1000 * 60))
    GameFilter gameFilter = new GameFilter(fromDate:twelveHoursAgo, toDate:oneMinuteAgo)
    List<GameDTO> games = findGames(gameFilter).games

    Long drinks = 0
    games.each { GameDTO game ->
      Double temp
      if (game.awayPlayerIds.contains(player.id)) {
        temp = RatingUtils.getDrinks(game.awayStats, game.homeStats, game.notes)
      } else if (game.homePlayerIds.contains(player.id)) {
        temp = RatingUtils.getDrinks(game.homeStats, game.awayStats, game.notes)
      }
      if (temp) {
        drinks += temp
      }
    }

    return drinks
  }

  String getDrinksDescription(Player player, Date date) {
    Long drinks = getDrinks(player, date)
    Integer beers = drinks / 12

    switch (beers) {
      case 0..3:
        return "sober"

      case 4..5:
        return "buzzed"

      case 6:
        return "tipsy"

      case 7..8:
        return "drunk"

      case 9..10:
        return "bombed"

      case 11..12:
        return "shitfaced"

      case 13..1000:
        return "blacked out"
    }
  }

  String getPredictionDescription(GameFilter gameFilter, Long totalGames) {
    Date now = new Date()
    String description = ""
    if (gameFilter.team1PlayerIdFilter?.ids) {
      List<String> names = gameFilter.team1PlayerIdFilter.ids.collect {
        Player player = Player.get(it)
        String drinks = getDrinksDescription(player, now)
        return "${player.name} (${drinks})"
      }
      description += names.join(" & ")
    } else {
      description += "Everybody"
    }

    description += " vs "

    if (gameFilter.team2PlayerIdFilter?.ids) {
      List<String> names = gameFilter.team2PlayerIdFilter.ids.collect {
        Player player = Player.get(it)
        String drinks = getDrinksDescription(player, now)
        return "${player.name} (${drinks})"
      }
      description += names.join(" & ")
    } else {
      description += "Everybody"
    }

    return "${description} (based on ${totalGames} games)"
  }

  //List<RecordGameListDTO>
  def getRecordGameLists = { GameFilter gameFilter, Integer topGames = 10 ->
    List<RecordGameListDTO> recordGameLists = []

    def intValueStringConverter = {
      String.valueOf(it as int)
    }
    def inverseIntValueStringConverter = {
      String.valueOf(-it as int)
    }
    def toaValueStringConverter = { DateUtils.secondsToString(it) }
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getPercentInstance()
    def percentageValueStringConverter = { numberFormat.format(it) }

    recordGameLists << new PropertyDiffRecordGameListDTO(description: "Score Beatdown", property: "score", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxRecordGameListDTO(description: "Highest Team Score", property: "score", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxSumRecordGameListDTO(description: "Highest Combined Score", property: "score", valueStringConverter: intValueStringConverter)
    recordGameLists << new RatingDiffRecordGameListDTO(description: "Rating Beatdown", valueStringConverter: intValueStringConverter)
    recordGameLists << new DrinksDiffRecordGameListDTO(description: "Drinks Beatdown", valueStringConverter: intValueStringConverter)
    recordGameLists << new DrinksMaxRecordGameListDTO(description: "Most Drinks", valueStringConverter: intValueStringConverter)
    recordGameLists << new DrinksMinRecordGameListDTO(description: "Least Drinks", valueStringConverter: inverseIntValueStringConverter)
    recordGameLists << new DrinksMaxSumRecordGameListDTO(description: "Most Combined Drinks", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyDiffRecordGameListDTO(description: "Hits Beatdown", property: "hits", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxRecordGameListDTO(description: "Most Team Hits", property: "hits", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxSumRecordGameListDTO(description: "Most Combined Hits", property: "hits", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyDiffRecordGameListDTO(description: "Shots Beatdown", property: "shots", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxRecordGameListDTO(description: "Most Team Shots", property: "shots", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxSumRecordGameListDTO(description: "Most Combined Shots", property: "shots", valueStringConverter: intValueStringConverter)
    recordGameLists << new PropertyMaxRecordGameListDTO(description: "Best Shot %", property: "shotPercentage", valueStringConverter: percentageValueStringConverter)
    recordGameLists << new PropertyDiffRecordGameListDTO(description: "TOA Beatdown", property: "timeOnAttack", valueStringConverter: toaValueStringConverter)
    recordGameLists << new PropertyMaxRecordGameListDTO(description: "Most Team TOA", property: "timeOnAttack", valueStringConverter: toaValueStringConverter)
    recordGameLists << new PropertyMaxSumRecordGameListDTO(description: "Most Combined TOA", property: "timeOnAttack", valueStringConverter: toaValueStringConverter)


    List<GameDTO> games = findGames(gameFilter).games

    log.debug "getRecordGameLists - evaluating ${games.size()} games for gameFilter:${gameFilter}"

    for (RecordGameListDTO recordGameList in recordGameLists) {
      for (GameDTO game in games) {
        recordGameList.evaluate(game)
      }
      recordGameList.sortAndTrim(topGames)
    }

    return recordGameLists
  }

  List<NewGameRecordDTO> getNewGameRecords(Game game) {
    List<NewGameRecordDTO> newGameRecords = []
    GameDTO gameDTO = new GameDTO(game: game)

    // all matchups
    GameFilter gameFilter = new GameFilter(gameVersion: GameVersion.getLatest())
    List<RecordGameListDTO> recordGameLists = getRecordGameLists(gameFilter, 0)
    addNewGameRecords(newGameRecords, recordGameLists, game.id, "all players")

    // filter out those with ranking worse than .30
    newGameRecords.retainAll { NewGameRecordDTO newGameRecord ->
      return newGameRecord.rank < 0.30D
    }

    newGameRecords.sort { NewGameRecordDTO newGameRecord ->
      return newGameRecord.rank
    }

    return newGameRecords
  }

  void addNewGameRecords(List<NewGameRecordDTO> recordDescriptions, List<RecordGameListDTO> recordGameLists, Long gameId, String matchupDescription) {
    recordGameLists.each { RecordGameListDTO recordGameList ->
      log.debug "addNewGameRecords - gameId:${gameId}, recordGameList:${recordGameList.description}, gamesEvaluated:${recordGameList.gamesEvaluated}"
      recordGameList.recordGames.eachWithIndex { RecordGameDTO recordGame, i ->
        if (recordGame.game.id == gameId) {
          recordDescriptions << new NewGameRecordDTO(value:recordGame.valueAsString,
              position: i,
              gamesPlayed:recordGameList.gamesEvaluated,
              recordGameListDescription:recordGameList.description,
              playersDescription:matchupDescription)
        }
      }
    }
  }

  MatchupListDTO getMatchupList(GameFilter gameFilter) {
    return getMatchupList(gameFilter, getAllMatchupTypes(gameFilter))
  }

  MatchupListDTO getMatchupList(GameFilter gameFilter, List<MatchupTypeDTO> matchupTypeList) {
    // get all MatchupStats
    List<MatchupStatsDTO> matchupStatsList = getMatchupStats(gameFilter)

    // collect Lists of MatchupStats by MatchupType
    Map<MatchupTypeDTO, List<MatchupStatsDTO>> matchupStatsListsByType = [:]
    matchupTypeList.each { MatchupTypeDTO matchupType ->
      List<MatchupStatsDTO> list = []
      matchupStatsListsByType[matchupType] = list
      matchupStatsList.each { MatchupStatsDTO matchupStats ->
        if (matchupType.matches(matchupStats)) {
          list << matchupStats
        }
      }
    }

    String description = ""
    if (gameFilter.team1PlayerIdFilter?.ids) {
      List<String> names = gameFilter.team1PlayerIdFilter.ids.collect {
        return Player.get(it).name
      }
      description += names.join(" & ")
    } else {
      description += "Everybody"
    }

    description += " vs "

    if (gameFilter.team2PlayerIdFilter?.ids) {
      List<String> names = gameFilter.team2PlayerIdFilter.ids.collect {
        return Player.get(it).name
      }
      description += names.join(" & ")
    } else {
      description += "Everybody"
    }

    // aggregate MatchupStats, collect by MatchupType into MatchupSummary
    MatchupListDTO matchupSummary = new MatchupListDTO(description: description)
    matchupStatsListsByType.each { entry ->
      // MatchupType key, List<MatchupStats> value
      matchupSummary.matchupStats << createAggregatedMatchupStats(entry.key, entry.value) // AggregatedMatchupStats
    }

    return matchupSummary
  }


  AggregatedMatchupStatsDTO createAggregatedMatchupStats(MatchupTypeDTO matchupType, List<MatchupStatsDTO> matchupStatsList) {
    List<MatchupStatsDTO> reverseOrder = new ArrayList<MatchupStatsDTO>(matchupStatsList)
    Collections.reverse(reverseOrder)
    List<Long> gameIds = matchupStatsList*.game.id

    Player mockPlayer = new Player(name: matchupType.description)
    PlayerDTO playerDTO = new PlayerDTO(player: mockPlayer)

    reverseOrder.each { MatchupStatsDTO matchupStats -> playerDTO << matchupStats }

    return new AggregatedMatchupStatsDTO(matchupType: matchupType, player: playerDTO)
  }


  List<MatchupStatsDTO> getMatchupStats(GameFilter gameFilter) {
    List<GameDTO> games = findGames(gameFilter).games
    List<MatchupStatsDTO> matchupStatsList = []

    games.each { GameDTO game ->
      MatchupStatsDTO matchupStats = getMatchupStats(gameFilter, game)
      if (matchupStats) {
        matchupStatsList << matchupStats
      }
    }

    return matchupStatsList
  }

  MatchupStatsDTO getMatchupStats(GameFilter gameFilter, GameDTO game) {
    if (gameFilter.gameVersion && gameFilter.gameVersion != game.gameVersion) {
      return null
    }

    MatchupStatsDTO matchupStats = new MatchupStatsDTO(game: game.game)
    Set<Long> gamePlayerIds = game.playerIds
    Set<Long> homePlayerIds = game.homePlayerIds
    Set<Long> awayPlayerIds = game.awayPlayerIds
    PlayerIdFilter gamePlayerIdFilter = gameFilter.gamePlayerIdFilter
    PlayerIdFilter team1PlayerIdFilter = gameFilter.team1PlayerIdFilter
    PlayerIdFilter team2PlayerIdFilter = gameFilter.team2PlayerIdFilter

    if (gamePlayerIdFilter?.matches(gamePlayerIds) && gameFilter.home) {
      matchupStats.forPlayers = game.homePlayers
      matchupStats.againstPlayers = game.awayPlayers
      matchupStats.forStats = game.homeStats
      matchupStats.againstStats = game.awayStats
    } else if (gamePlayerIdFilter?.matches(gamePlayerIds)) {
      matchupStats.forPlayers = game.awayPlayers
      matchupStats.againstPlayers = game.homePlayers
      matchupStats.forStats = game.awayStats
      matchupStats.againstStats = game.homeStats
    } else if (team1PlayerIdFilter?.matches(homePlayerIds) && team2PlayerIdFilter?.matches(awayPlayerIds)) {
      matchupStats.forPlayers = game.homePlayers
      matchupStats.againstPlayers = game.awayPlayers
      matchupStats.forStats = game.homeStats
      matchupStats.againstStats = game.awayStats
    } else if (team1PlayerIdFilter?.matches(awayPlayerIds) && team2PlayerIdFilter?.matches(homePlayerIds)) {
      matchupStats.forPlayers = game.awayPlayers
      matchupStats.againstPlayers = game.homePlayers
      matchupStats.forStats = game.awayStats
      matchupStats.againstStats = game.homeStats
    } else {
      return null
    }

    return matchupStats
  }

  List<MatchupTypeDTO> getAllMatchupTypes(GameFilter gameFilter) {
    List<MatchupTypeDTO> matchupTypes = []
    matchupTypes << new AllMatchupTypeDTO(description:"All", gameFilter: gameFilter)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 1, againstTeamSize: 1)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 1, againstTeamSize: 2)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 2, againstTeamSize: 1)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 2, againstTeamSize: 2)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 2, againstTeamSize: 3)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 3, againstTeamSize: 2)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 3, againstTeamSize: 3)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 3, againstTeamSize: 4)
    matchupTypes << new TeamSizeMatchupTypeDTO(gameFilter: gameFilter, forTeamSize: 4, againstTeamSize: 3)
    return matchupTypes
  }

  def average = { values, transformer = null ->
    Double sum = 0
    Double count = values.size()
    if (count == 0) {
      return 0
    }

    values.each {
      if (transformer) {
        sum += transformer.call(it)
      } else {
        sum += it
      }
    }

    return sum / count
  }
}
