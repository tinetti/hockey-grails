package ss.hockey

import java.util.Date;
import java.util.List;
import java.util.Set;


class MainController {

  PlayerService playerService
  GameService gameService
  ChartService chartService

  def index = { redirect(action: 'listPlayers') }

  def createGame = { CreateGameCommand command ->
    command.errors = null
    return [command:command, playerList: Player.list()]
  }

  def predictGame = { CreateGameCommand command ->
    def model = [command: command, 'playerList': Player.list()]

    if (command.validate()) {
      Game game = getGame(command)
      PlayerIdFilter awayPlayerIdFilter = new PlayerIdFilter(ids:game.awayPlayerIds, matchingStrategy:MatchingStrategy.ALL)
      PlayerIdFilter homePlayerIdFilter = new PlayerIdFilter(ids:game.homePlayerIds, matchingStrategy:MatchingStrategy.ALL)
      GameFilter gameFilter = new GameFilter(gameVersion:command.gameVersion, team1PlayerIdFilter:awayPlayerIdFilter, team2PlayerIdFilter:homePlayerIdFilter)
      GamePredictionDTO prediction = gameService.predictGame(gameFilter)

      // find the last few games with these players
      gameFilter.team1PlayerIdFilter.matchingStrategy = MatchingStrategy.EXACT
      gameFilter.team2PlayerIdFilter.matchingStrategy = MatchingStrategy.EXACT
      gameFilter.max = 10
      List<GameDTO> previousGames = gameService.findGames(gameFilter).games

      model['matchupList'] = prediction.matchupList
      model['prediction'] = prediction
      model['previousGames'] = previousGames
    }

    render view:'createGame', model:model
  }

  def saveGame = { CreateGameCommand command ->
    Game game = getGame(command)
    if (game.awayStats.validate() && game.homeStats.validate() && game.validate() && game.save()) {
      List<NewGameRecordDTO> newGameRecords = gameService.getNewGameRecords(game)
      flash['newGameRecords'] = newGameRecords
      flash.message = "Game saved!"
      redirect(action: 'listGames')
    } else {
      render(view:'createGame', model:[command:command, playerList:Player.list(), game:game])
    }
  }

  def editGame = {
    def model = ['playerList': Player.list()]
    String gameId = params['id']
    Game game = Game.get(gameId as Long)
    if (game) {
      GameDTO gameDTO = new GameDTO(game: game)

      CreateGameCommand command = new CreateGameCommand()
      command.date = game.date
      command.awayPlayerIds = new ArrayList<Long>(gameDTO.awayPlayerIds)
      command.homePlayerIds = new ArrayList<Long>(gameDTO.homePlayerIds)
      command.awayTeam = game.awayStats.team
      command.homeTeam = game.homeStats.team
      command.awayScore = game.awayStats.score
      command.homeScore = game.homeStats.score
      command.awayShots = game.awayStats.shots
      command.homeShots = game.homeStats.shots
      command.awayHits = game.awayStats.hits
      command.homeHits = game.homeStats.hits
      command.awayTimeOnAttackString = game.awayStats.timeOnAttackString
      command.homeTimeOnAttackString = game.homeStats.timeOnAttackString
      command.awayPassingPercentage = game.awayStats.passingPercentage
      command.homePassingPercentage = game.homeStats.passingPercentage
      command.awayPenaltyMinutesString = game.awayStats.penaltyMinutesString
      command.homePenaltyMinutesString = game.homeStats.penaltyMinutesString
      command.gameLength = game.gameLength
      command.notes = game.notes
      command.gameVersion = game.gameVersion

      model['command'] = command
      model['gameId'] = gameId
    }

    render view: 'createGame', model: model
  }

  def deleteGame = {
    def gameId = params['id']
    if (gameId) {
      Game game = Game.get(gameId as Long)
      if (game) {
        game.delete()
        flash.message = 'Game Deleted.'
      }
    }

    redirect(action: 'listGames')
  }

  def listPlayers = { ListGamesCommand command ->
    GameFilter gameFilter = session['gameFilter']
    if (!gameFilter || params['resetFilter']) {
      gameFilter = createDefaultPlayerListGameFilter()
    }

    if (command && params['applyFilter'] && !params['resetFilter']) {
      gameFilter.gameVersion = command.gameVersion
      gameFilter.fromDate = command.fromDate
      gameFilter.toDate = command.toDate
      gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids: command.playerIds, matchingStrategy: command.matchingStrategy)
      gameFilter.excludePlayerIdFilter = new PlayerIdFilter(ids: command.excludePlayerIds, matchingStrategy: command.excludeMatchingStrategy)
      gameFilter.maxGamesPerPlayer = command.maxGamesPerPlayer
    }

    GameFilter modelGameFilter = clone(gameFilter)

    List<GameDTO> games = gameService.findGames(gameFilter).games
    List<PlayerDTO> players = playerService.findPlayers(gameFilter, games)

    PlayerRecordsDTO playerRecordsDTOHigh = PlayerDTOUtils.getPlayerStatsRecordsHigh(players)
    PlayerRecordsDTO playerRecordsDTOLow = PlayerDTOUtils.getPlayerStatsRecordsLow(players)

    players.add(playerService.getAveragePlayer(players))

    List<ChartDTO> charts = []
    try {
      ChartDTO chart = chartService.createWinningPercentageChart(players, games)
      if (chart) {
        charts << chart
      }

      gameFilter = new GameFilter()
      gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids:command.playerIds, matchingStrategy:command.matchingStrategy)
      gameFilter.gameVersion = modelGameFilter.gameVersion
      gameFilter.fromDate = modelGameFilter.fromDate
      gameFilter.toDate = modelGameFilter.toDate
      chart = chartService.createPlayerDetailsChart(players*.id, gameFilter)
      if (chart) {
        charts << chart
      }
    } catch (Throwable t) {
      log.error("error creating charts", t)
    }

    return [players: players,
      gameFilter: modelGameFilter,
      charts: charts,
      playerRecordsDTOHigh: playerRecordsDTOHigh,
      playerRecordsDTOLow: playerRecordsDTOLow]
  }

  def viewPlayerDetails = { ViewPlayerCommand command ->
    if (!command.playerIds) {
      redirect(action: 'listPlayers')
    } else {
      GameFilter gameFilter = new GameFilter()
      gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids:command.playerIds, matchingStrategy: command.matchingStrategy)
      gameFilter.gameVersion = command.gameVersion
      gameFilter.fromDate = command.fromDate
      gameFilter.toDate = command.toDate

      PlayerDetailsDTO playerDetails = playerService.getPlayerDetails(gameFilter)

      ChartDTO chart = chartService.createPlayerDetailsChart(command.playerIds, gameFilter)

      // empty out the player id filters before putting the GameFilter into the model
      gameFilter.team1PlayerIdFilter = null
      gameFilter.team2PlayerIdFilter = null

      return [playerDetails:playerDetails, gameFilter:gameFilter, chart:chart]
    }
  }

  def listGames = { ListGamesCommand command ->
    GameFilter gameFilter = new GameFilter()
    gameFilter.gameVersion = command.gameVersion
    gameFilter.fromDate = command.fromDate
    gameFilter.toDate = command.toDate
    gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids: command.playerIds, matchingStrategy: command.matchingStrategy)
    gameFilter.excludePlayerIdFilter = new PlayerIdFilter(ids: command.excludePlayerIds, matchingStrategy: command.excludeMatchingStrategy)
    gameFilter.maxGamesPerPlayer = command.maxGamesPerPlayer
    gameFilter.offset = params?.offset ? (params.offset as Long) : 0
    gameFilter.max = params?.max ? (params.max as Long) : 10

    if (params['gameIds']) {
      gameFilter.gameIds = HockeyUtils.stringToListOfLongs(params.gameIds)
    }

    GameListDTO gameList = gameService.findGames(gameFilter)

    return ['games': gameList.games, 'gameCount': gameList.max, gameFilter: gameFilter]
  }

  def listMatchups = { ListMatchupsCommand command ->
    GameFilter gameFilter = new GameFilter()
    gameFilter.gameVersion = command.gameVersion
    gameFilter.team1PlayerIdFilter = new PlayerIdFilter(ids: command.playerIds, matchingStrategy: command.matchingStrategy)
    gameFilter.team2PlayerIdFilter = new PlayerIdFilter(ids: command.opponentIds, matchingStrategy: command.opponentMatchingStrategy)
    gameFilter.excludePlayerIdFilter = new PlayerIdFilter(ids: command.excludePlayerIds, matchingStrategy: command.excludeMatchingStrategy)
    gameFilter.maxGamesPerPlayer = command.maxGamesPerPlayer

    Map model = ['gameFilter': gameFilter]

    if (command.playerIds != null) {
      MatchupListDTO matchupList = gameService.getMatchupList(gameFilter)
      model['matchupList'] = matchupList
    }

    return model
  }

  def listHockeySessions = {
    HockeySessionListDTO hockeySessionList = playerService.getHockeySessionList(params)

    StringBuffer buf = new StringBuffer()
    buf.append "sessions: count=${hockeySessionList.count}\n"

    hockeySessionList.hockeySessions.each { HockeySessionDTO hockeySession ->
      buf.append "\tsession: ${hockeySession.date}:${hockeySession.getPlayers()}"
    }

    return [hockeySessionList: hockeySessionList]
  }

  def listRecords = { ListRecordsCommand command ->
    GameFilter gameFilter = new GameFilter()
    gameFilter.gameVersion = command.gameVersion
    gameFilter.fromDate = command.fromDate
    gameFilter.toDate = command.toDate
    gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids: command.playerIds, matchingStrategy: command.matchingStrategy)
    gameFilter.excludePlayerIdFilter = new PlayerIdFilter(ids: command.excludePlayerIds, matchingStrategy: command.excludeMatchingStrategy)

    List<RecordGameListDTO> recordGameLists = gameService.getRecordGameLists(gameFilter)

    return [recordGameLists: recordGameLists, gameFilter: gameFilter]
  }

  Game getGame(CreateGameCommand command) {
    Game game
    if (command.gameId > 0) {
      game = Game.get(command.gameId)
      game.gamePlayers.each { GamePlayer gamePlayer ->
        gamePlayer.delete()
      }
      game.gamePlayers.clear()
    } else {
      game = new Game()
    }
    game.date = command.date
    game.gameVersion = command.gameVersion
    game.gameLength = command.gameLength
    game.notes = command.notes
    command.awayPlayerIds.each {
      game.addToGamePlayers(new GamePlayer(player: Player.get(it), home: false))
    }
    command.homePlayerIds.each {
      game.addToGamePlayers(new GamePlayer(player: Player.get(it), home: true))
    }

    game.awayStats = new TeamStats(team:command.awayTeam, score:command.awayScore, shots:command.awayShots, hits:command.awayHits, timeOnAttackString:command.awayTimeOnAttackString, passingPercentage:command.awayPassingPercentage, penaltyMinutesString:command.awayPenaltyMinutesString, drinks:command.awayDrinks)
    game.homeStats = new TeamStats(team:command.homeTeam, score:command.homeScore, shots:command.homeShots, hits:command.homeHits, timeOnAttackString:command.homeTimeOnAttackString, passingPercentage:command.homePassingPercentage, penaltyMinutesString:command.homePenaltyMinutesString, drinks:command.homeDrinks)

    return game
  }

  GameFilter createDefaultPlayerListGameFilter() {
    Date oneMonthAgo = new Date() - 30
    GameFilter gameFilter = new GameFilter(fromDate:oneMonthAgo)
    List<GameDTO> games = gameService.findGames(gameFilter).games
    games.sort { GameDTO game ->
      0 - game.date.time
    }
    Set<Long> playerIds = new HashSet<Long>()
    games.each { GameDTO game ->
      game.players.each { PlayerDTO player ->
        // don't include other player
        if (Player.OTHER_PLAYER_ID != player.id && playerIds.size() < 6) {
          playerIds.add(player.id)
        }
      }
    }

    return new GameFilter(gameVersion:GameVersion.getLatest(), gamePlayerIdFilter:new PlayerIdFilter(ids: playerIds, matchingStrategy: MatchingStrategy.ANY), excludePlayerIdFilter:new PlayerIdFilter(matchingStrategy:MatchingStrategy.ANY))
  }
  
  GameFilter clone(GameFilter original) {
    GameFilter copy = new GameFilter(offset:original.offset, max:original.max, gameVersion:original.gameVersion, gameIds:original.gameIds, fromDate:original.fromDate, toDate:original.toDate, maxGamesPerPlayer:original.maxGamesPerPlayer)
    copy.gamePlayerIdFilter = original.gamePlayerIdFilter ? new PlayerIdFilter(ids:original.gamePlayerIdFilter.ids, matchingStrategy:original.gamePlayerIdFilter.matchingStrategy) : null
    copy.team1PlayerIdFilter = original.team1PlayerIdFilter ? new PlayerIdFilter(ids:original.team1PlayerIdFilter.ids, matchingStrategy:original.team1PlayerIdFilter.matchingStrategy) : null
    copy.team2PlayerIdFilter = original.team2PlayerIdFilter ? new PlayerIdFilter(ids:original.team2PlayerIdFilter.ids, matchingStrategy:original.team2PlayerIdFilter.matchingStrategy) : null
    copy.excludePlayerIdFilter = original.excludePlayerIdFilter ? new PlayerIdFilter(ids:original.excludePlayerIdFilter.ids, matchingStrategy:original.excludePlayerIdFilter.matchingStrategy) : null
    return copy
  }
}


class ListGamesCommand {
  List<Long> playerIds
  MatchingStrategy matchingStrategy = MatchingStrategy.ANY

  List<Long> excludePlayerIds
  MatchingStrategy excludeMatchingStrategy = MatchingStrategy.ANY

  GameVersion gameVersion
  Date fromDate
  Date toDate

  Long maxGamesPerPlayer

  String toString() {
    return "${getClass().getSimpleName()}[playerIds=${playerIds}, gameVersion=${gameVersion}, fromDate=${fromDate}, toDate=${toDate}, maxTotalGames=${maxTotalGames}, maxGamesPerPlayer=${maxGamesPerPlayer}]"
  }
}

class ListRecordsCommand extends ListGamesCommand {
  {
    gameVersion = GameVersion.getLatest()
  }
}

class ViewPlayerCommand extends ListGamesCommand {
  {
    gameVersion = GameVersion.getLatest()
  }
}


class ListMatchupsCommand extends ListGamesCommand {
  ListMatchupsCommand(Map m) {
    gameVersion = GameVersion.getLatest()
    matchingStrategy = MatchingStrategy.ALL
  }

  List<Long> opponentIds
  MatchingStrategy opponentMatchingStrategy = MatchingStrategy.ALL
}


class CreateGameCommand {

  static constraints = {
    awayPlayerIds(validator: { List<Long> val, CreateGameCommand obj ->
      if (val == null || val.size() == 0) {
        return false
      }
    })
    homePlayerIds(validator: { List<Long> val, CreateGameCommand obj ->
      if (val == null || val.size() == 0) {
        return false
      }

      // duplicated in Game
      Set<Long> duplicatePlayerIds = new HashSet<Long>(obj.awayPlayerIds)
      duplicatePlayerIds.retainAll(obj.homePlayerIds)
      duplicatePlayerIds.remove(Player.OTHER_PLAYER_ID)

      if (duplicatePlayerIds.size() > 0) {
        return ['samePlayers']
      }
    })
  }

  Long gameId

  List<Long> awayPlayerIds
  List<Long> homePlayerIds

  GameVersion gameVersion = GameVersion.getLatest()
  GameLength gameLength = GameLength.Regulation
  Date date = new Date()
  String notes

  NhlTeam awayTeam
  NhlTeam homeTeam

  Integer awayScore
  Integer homeScore

  Integer awayShots
  Integer homeShots
  Integer awayHits
  Integer homeHits
  String awayTimeOnAttackString
  String homeTimeOnAttackString
  String awayPenaltyMinutesString
  String homePenaltyMinutesString
  Integer awayPassingPercentage
  Integer homePassingPercentage
  Integer awayDrinks
  Integer homeDrinks
}
