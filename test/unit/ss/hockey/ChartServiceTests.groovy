package ss.hockey

import grails.test.GrailsUnitTestCase

import org.jfree.data.time.TimeSeriesCollection

class ChartServiceTests extends GrailsUnitTestCase {
  
  ChartService chartService
  
  void setUp() {
    super.setUp()
    chartService = new ChartService()
  }
  
  void testCreateTimeSeriesCollection() {
    Player johnny = Mom.johnny
    Player pauly = Mom.pauly
    
    PlayerDTO johnnyDTO = new PlayerDTO(player:johnny)
    PlayerDTO paulyDTO = new PlayerDTO(player:pauly)
    
    List<PlayerDTO> players = [johnnyDTO, paulyDTO]
    
    int[] johnnyScores = [3,5,7]
    int[] paulyScores = [4,6]
    
    int numGames = 100
    List<GameDTO> games = []
    numGames.times { int index ->
      Game game = new Game(id:index, date:(new Date() - numGames + index))
      
      game.gamePlayers << new GamePlayer(id:index, game:game, player:johnny, home:true)
      game.gamePlayers << new GamePlayer(id:index + numGames + 1, game:game, player:pauly, home:false)
      
      game.homeStats = new TeamStats(team:NhlTeam.SABRES, score:johnnyScores[index % johnnyScores.length]) 
      game.awayStats = new TeamStats(team:NhlTeam.BLACKHAWKS, score:paulyScores[index % paulyScores.length])
      
      games << new GameDTO(game:game, players:players)
    }
    
    TimeSeriesCollection tsc = chartService.createTimeSeriesCollection(players, games)
    
    assertNotNull(tsc)
  }
}