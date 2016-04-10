package ss.hockey

import java.awt.BasicStroke
import java.awt.Color
import java.awt.image.BufferedImage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List;

import javax.imageio.ImageIO

import org.jfree.chart.*
import org.jfree.chart.axis.*
import org.jfree.chart.plot.*
import org.jfree.chart.renderer.xy.*
import org.jfree.data.time.*
import org.jfree.data.xy.DefaultXYDataset
import org.jfree.data.xy.XYDataset

class ChartService {

  static final Integer MIN_GAMES = 5;

  static transactional = false

  GameService gameService

  ChartDTO createPlayerDetailsChart(List<Long> playerIds, GameFilter gameFilter) {
    DefaultXYDataset dataset = new DefaultXYDataset()

    List<List<Double>> allRatings = []
    for (Long playerId : playerIds) {
      if (playerId) {
        Player player = Player.get(playerId)
        gameFilter.gamePlayerIdFilter = new PlayerIdFilter(ids:[playerId]as Set)
        List<GameDTO> games = gameService.findGames(gameFilter).games
        if (games.size()) {
          List<Double> ratings = []
          games.eachWithIndex { GameDTO game, Integer index ->
            Double rating = game.awayPlayerIds?.contains(playerId) ? game.awayRating : game.homeRating
            if (rating != null) {
              ratings.add(rating)
            }
          }

          ratings = average(ratings, 50)
          allRatings.add(ratings)
        }
      }
    }

    Integer greatestLength = 0
    allRatings.each {
      greatestLength = Math.max(greatestLength, it.size())
    }

    allRatings.eachWithIndex { List<Double> ratings, Integer index ->
      List<Double> xValues = []
      for (double i = 0; i < ratings.size(); i++) {
        xValues.add(i * greatestLength / ratings.size)
      }
      double[][] series = [
        xValues as double[],
        ratings as double[]
      ]as double[][]

      dataset.addSeries(Player.get(playerIds[index]).name, series)
    }

    JFreeChart chart = createPlayerDetailsChart(dataset)

    return createChartDTO(chart, "Ratings")
  }

  List<Double> average(List<Double> values, int size) {
    List<Double> averages = []
    for (int i = size; i < values.size(); i++) {
      Double sum = 0
      for (int j = 0; j < size; j++) {
        sum += values[i - j]
      }
      sum /= size
      averages.add(sum)
    }
    return averages
  }

  ChartDTO createWinningPercentageChart(List<PlayerDTO> players, List<GameDTO> games) {
    if (games == null || games.isEmpty()) {
      return null
    }

    TimeSeriesCollection timeSeriesCollection = createTimeSeriesCollection(players, games)
    JFreeChart chart = createWinningPercentageChart(timeSeriesCollection)

    return createChartDTO(chart, "Winning Percentages")
  }

  ChartDTO createChartDTO(JFreeChart chart, String description) {
    chart.antiAlias = true
    Integer width = 1500
    Integer height = 500
    BufferedImage bufferedImage = chart.createBufferedImage(width, height)
    ByteArrayOutputStream baos = new ByteArrayOutputStream()
    ImageIO.write(bufferedImage, 'png', baos)

    return new ChartDTO(description:description, type:'image/png', width:(width*0.65D), height:(height*0.75D), data:baos.toByteArray())
  }

  TimeSeriesCollection createTimeSeriesCollection(List<PlayerDTO> players, List<GameDTO> games) {
    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection()

    // order games by date asc
    games = new ArrayList<GameDTO>(games)
    games.sort { GameDTO g1, GameDTO g2 ->
      return g1.date - g2.date
    }

    // strip out old games (90 days)
    Date lastDate = new Date(games[-1].date.time + 1000)
    games = games.findAll { GameDTO game ->
      return game.date > (lastDate - 90)
    }

    if (games.size()) {
      Date firstDate = new Date(games[0].date.time - 1000)

      Map<PlayerDTO, List<GameDTO>> playerGameLists = collectPlayerGames(players, games)
      playerGameLists.each { PlayerDTO player, List<GameDTO> playerGames ->
        TimeSeries timeSeries = createTimeSeries(player, playerGames, firstDate, lastDate)
        if (timeSeries) {
          timeSeriesCollection.addSeries(timeSeries)
        }
      }
    }

    return timeSeriesCollection
  }

  TimeSeries createTimeSeries(PlayerDTO player, List<GameDTO> playerGames, Date firstDate, Date lastDate) {
    if (!player.id || playerGames.size() < MIN_GAMES) {
      return null
    }

    log.debug "creating time series for ${player}"
    TimeSeries timeSeries = new TimeSeries(player.name)
    Number wp = getWinPercentageAfter(playerGames, firstDate, player, 5)
    if (wp != null) {
      timeSeries.add(new Millisecond(firstDate), wp)
    }
    Number finalWP = wp
    for (int day = 1; firstDate + day < lastDate; day++) {
      Date date = firstDate + day
      wp = getWinPercentageAfter(playerGames, date, player, MIN_GAMES)
      if (wp != null) {
        timeSeries.add(new Millisecond(date), wp)
        finalWP = wp
      }
    }
    if (finalWP != null) {
      timeSeries.add(new Millisecond(lastDate), finalWP)
    }

    log.debug "created time series for ${player} - ${timeSeries.itemCount} points"
    return timeSeries
  }

  Map<PlayerDTO, List<GameDTO>> collectPlayerGames(List<PlayerDTO> players, List<GameDTO> games) {
    Map<PlayerDTO, List<GameDTO>> playerGames = new LinkedHashMap<PlayerDTO, List<GameDTO>>()
    players.each { PlayerDTO player ->
      playerGames[player] = []
    }

    Map<Long, PlayerDTO> playerIds = [:]
    players.each { PlayerDTO player ->
      playerIds[player.id] = player
    }

    games.each { GameDTO game ->
      game.playerIds.each { Long playerId ->
        PlayerDTO player = playerIds[playerId]
        if (player) {
          playerGames[player] << game
        }
      }
    }

    return playerGames
  }

  JFreeChart createWinningPercentageChart(XYDataset dataset) {
    String title = null
    String xAxisLabel = "Date"
    String yAxisLabel = "Winning Percentage"
    boolean legend = true
    StandardChartTheme theme = new StandardChartTheme("JFree")
    theme.setPlotBackgroundPaint(Color.white)
    theme.setDomainGridlinePaint(Color.gray)
    theme.setRangeGridlinePaint(Color.gray)
    DateAxis xAxis = new DateAxis(xAxisLabel)
    xAxis.dateFormatOverride = SimpleDateFormat.getDateInstance()
    xAxis.tickUnit = new DateTickUnit(DateTickUnitType.DAY, 7)
    xAxis.minorTickCount = 7
    xAxis.minorTickMarksVisible = true
    NumberAxis yAxis = new NumberAxis(yAxisLabel)
    yAxis.setRange(-0.05D, 1.05D)
    yAxis.numberFormatOverride = NumberFormat.getPercentInstance()
    XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false)
    renderer.stroke = new BasicStroke(1.5F)
    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer)
    plot.setRangeAxis(1, yAxis)
    plot.orientation = PlotOrientation.VERTICAL

    JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend)
    theme.apply(chart)
    return chart
  }

  JFreeChart createPlayerDetailsChart(XYDataset dataset) {
    String title = null
    String yAxisLabel = "Rating"
    PlotOrientation orientation = PlotOrientation.VERTICAL
    boolean legend = true
    StandardChartTheme theme = new StandardChartTheme("JFree")
    theme.setPlotBackgroundPaint(Color.white)
    theme.setDomainGridlinePaint(Color.gray)
    theme.setRangeGridlinePaint(Color.gray)
    NumberAxis xAxis = new NumberAxis()
    xAxis.tickLabelsVisible = false
    NumberAxis yAxis = new NumberAxis(yAxisLabel)
    XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false)
    renderer.stroke = new BasicStroke(1.5F)
    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer)
    plot.setRangeAxis(1, yAxis)
    plot.setOrientation(orientation)

    JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend)
    theme.apply(chart)
    return chart
  }

  Number getWinPercentageAfter(List<GameDTO> playerGames, Date date, PlayerDTO player, int minGames) {
    Integer gamesPlayed = 0
    Integer gamesWon = 0

    playerGames.each { GameDTO game ->
      if (game.date.time >= date.time) {
        if (game.awayPlayerIds.contains(player.id)) {
          gamesPlayed++
          if (game.awayStats.score > game.homeStats.score) {
            gamesWon++
          }
        } else if (game.homePlayerIds.contains(player.id)) {
          gamesPlayed++
          if (game.awayStats.score < game.homeStats.score) {
            gamesWon++
          }
        }
      }
    }

    if (gamesPlayed < minGames) {
      return null
    }

    Double wp = gamesPlayed == 0 ? 0D : (Double) gamesWon / (Double) gamesPlayed

    return wp
  }

}
