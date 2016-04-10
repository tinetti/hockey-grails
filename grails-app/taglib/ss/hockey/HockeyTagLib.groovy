package ss.hockey

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.plugins.web.taglib.FormTagLib
import org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib

class HockeyTagLib {

  static namespace = 'h'

  static DateFormat DATE_FORMAT = new SimpleDateFormat("EEEEE MM-dd-yyyy")
  static DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd-yyyy hh:mm a")

  static NumberFormat PERCENT_FORMAT = new DecimalFormat("00.0%")
  static NumberFormat SINGLE_DECIMAL_FORMAT = new DecimalFormat("0.0")
  static NumberFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.00")

  GameService gameService
  PlayerService playerService

  def gameTable = { attrs, body ->
    List games = attrs['games']
    Boolean showDeleteLink = attrs['showDeleteLink'] == 'true'
    Boolean showEditLink = attrs['showEditLink'] == 'true'

    out << "\
<table class=\"listTable gameTable\" id=\"gameTable\"> \n\
  <thead> \n\
    <tr> \n"

    if (games?.size() > 0 && (games[0] instanceof RecordGameDTO)) {
      out << "<th>Value</th> \n"
    }

    out << "\
      <th class=\"playersColumn\">Players</th> \n\
      <th class=\"scoreColumn\">Score</th> \n\
      <th class=\"ratingColumn\">Rating</th> \n\
      <th class=\"teamColumn\">Team</th> \n\
      <th class=\"shotsColumn\">Shots</th> \n\
      <th class=\"hitsColumn\">Hits</th> \n\
      <th class=\"toaColumn\">T.O.A.</th> \n\
      <th class=\"passingColumn\">Passing</th> \n\
      <th class=\"drinksColumn\">Drinks</th> \n\
      <th class=\"versionColumn\">Version</th> \n\
      <th class=\"dateColumn\">Date</th> \n\
    </tr> \n\
  </thead> \n\
  <tbody> \n"

    games.eachWithIndex { o, i ->
      RecordGameDTO recordGame
      GameDTO game
      if (o instanceof RecordGameDTO) {
        recordGame = o
        game = recordGame.game
      } else if (o instanceof GameDTO) {
        recordGame = null
        game = o
      }

      String awayClass = game.awayStats.score > game.homeStats.score ? 'winner' : 'loser'
      String homeClass = game.homeStats.score > game.awayStats.score ? 'winner' : 'loser'
      out << "\
    <tr class=\""
      out << ((i % 2) == 0 ? 'odd' : 'even')
      out << "\"> \n"

      if (recordGame) {
        out << "\
      <td class=\"recordValueColumn\">${recordGame.valueAsString}</td> \n"
      }

      out << "\
      <td class=\"playerName\"> \n\
        <div class=\"${awayClass}\"> \n"

      game.awayPlayers.each { Player player ->
        renderViewPlayerDetails(player, game.date)
      }

      out << "\
        </div> \n\
        <div class=\"${homeClass}\"> \n"

      game.homePlayers.each { Player player ->
        renderViewPlayerDetails(player, game.date)
      }

      out << "\
        </div> \n"

      if (game.gameLength != GameLength.Regulation) {
        out << "<span class=\"gameLength\">${game.gameLength.name}</span><br/> \n"
      }

      if (game.notes?.length() > 0) {
        out << "<span class=\"gameNotes\">${game.notes}</span> \n"
      }

      out << "\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayStats.score}</div> \n\
        <div class=\"${homeClass}\">${game.homeStats.score}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${formatRating(game.awayRating)}</div> \n\
        <div class=\"${homeClass}\">${formatRating(game.homeRating)}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayStats?.team?.fullName ?: ''}</div> \n\
        <div class=\"${homeClass}\">${game.homeStats?.team?.fullName ?: ''}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayStats.shots ?: ''}</div> \n\
        <div class=\"${homeClass}\">${game.homeStats.shots ?: ''}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayStats.hits ?: ''}</div> \n\
        <div class=\"${homeClass}\">${game.homeStats.hits ?: ''}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayStats.timeOnAttackString ?: ''}</div> \n\
        <div class=\"${homeClass}\">${game.homeStats.timeOnAttackString ?: ''}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${toPercentageString(game.awayStats.passingPercentage)}</div> \n\
        <div class=\"${homeClass}\">${toPercentageString(game.homeStats.passingPercentage)}</div> \n\
      </td> \n\
      <td> \n\
        <div class=\"${awayClass}\">${game.awayDrinksDescription}</div> \n\
        <div class=\"${homeClass}\">${game.homeDrinksDescription}</div> \n\
      </td> \n\
      <td> \n\
        <div>${game.gameVersion.name}</div> \n\
      </td> \n\
      <td> \n\
        <span>${DATE_TIME_FORMAT.format(game.date)}</span> \n"

      if (showEditLink && game.id) {
        out << "<span>&nbsp;<a href=\"editGame/${game.id}\">Edit</a></span>"
      }

      if (showDeleteLink && game.id) {
        out << "<span>&nbsp;<a href=\"deleteGame/${game.id}\" onclick=\"return confirm('Are you sure you want to delete this game?');\">Delete</a></span> \n"
      }

      out << "\
        </div> \n\
      </td> \n\
    </tr> \n"
    }

    out << "\
  </tbody> \n\
</table> \n"

    Long gameCount = (attrs['gameCount'] ?: "0") as Long
    attrs['total'] = gameCount
    if (gameCount > games.size()) {
      RenderTagLib renderTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib')
      out << "<!-- renderTagLib: ${renderTagLib} -->"
      out << "<div class=\"paginateButtons\"> \n"

      renderTagLib.paginate.call(attrs)
      out << "</div> \n"
    }
  }

  def hockeySessionTables = { attrs, body ->
    HockeySessionListDTO hockeySessionList = attrs['hockeySessionList']
    hockeySessionList.hockeySessions.each { HockeySessionDTO hockeySession ->
      out << "\
<h1> \n\
  <a href=\"listGames?gameIds=${hockeySession.gameIds}\">${DATE_FORMAT.format(hockeySession.date)}</a> \n\
</h1> \n\
<div class=\"list\"> \n"
      Map playerTableAttrs = ['players': hockeySession.players]
      playerTable.call(playerTableAttrs, body)
      out << "\
 </div> \n"
    }

    Long count = hockeySessionList.count
    if (count > hockeySessionList.hockeySessions.size()) {
      RenderTagLib renderTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib')
      out << "<!-- renderTagLib: ${renderTagLib} -->"
      out << "<br /> \n"
      out << "<div class=\"paginateButtons\"> \n"

      attrs['total'] = count
      renderTagLib.paginate.call(attrs)
      out << "</div> \n"
    }
  }

  def playerTable = { attrs, body ->
    List<PlayerDTO> players = attrs['players']
    PlayerRecordsDTO playerRecordsDTOHigh = attrs['high']
    PlayerRecordsDTO playerRecordsDTOLow = attrs['low']

    out << "\
<table class=\"listTable playerTable\"> \n\
  <thead> \n\
  <tr> \n\
    <th class=\"playerColumn sortable\">Name</th> \n\
    <th class=\"gamesPlayedColumn sortable\">GP</th> \n\
    <th class=\"gamesPlayedColumn sortable\">W</th> \n\
    <th class=\"gamesPlayedColumn sortable\">L</th> \n\
    <th class=\"winPercentageColumn sortable\">W%</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">GF/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">GA/G</th> \n\
    <th class=\"streakColumn sortable\">Strk</th> \n\
    <th style=\"width: 45px;\" class=\"sortable\">RF</th> \n\
    <th style=\"width: 45px;\" class=\"sortable\">RA</th> \n\
    <th style=\"width: 45px;\" class=\"sortable\">DF/G</th> \n\
    <th style=\"width: 45px;\" class=\"sortable\">DA/G</th> \n\
    <th style=\"width: 100px;\" class=\"sortable\">Streaks</th> \n\
  </tr> \n\
  </thead> \n\
  <tbody>"

    players = new ArrayList<PlayerDTO>(players)
    players.retainAll {
      it.gameIds.size() > 0
    }

    players.eachWithIndex { PlayerDTO player, Integer index ->
      out << "\
    <tr class=\"${(index % 2) == 0 ? 'odd' : 'even'}\"> \n\
      <td class=\"playerName\"> \n"
      if (player.id) {
        renderViewPlayerDetails(player.player)
      } else {
        out << "<span>${player.name}</span> \n"
      }
      out << "\
      </td> \n\
      <td> \n\
        <a href=\"listGames?gameIds=${player.gameIds}\"> \n\
          <span>${player.gameIds.size()}</span> \n\
        </a> \n\
      </td> \n\
      <td> \n\
        <span> \n\
          <a href=\"listGames?gameIds=${player.gamesWonIds}\"> \n\
            <span>${player.gamesWonIds.size()}</span> \n\
          </a> \n\
        </span> \n\
      </td> \n\
      <td> \n\
        <span> \n\
          <a href=\"listGames?gameIds=${player.gamesLostIds}\"> \n\
            <span>${player.gamesLostIds.size()}</span> \n\
          </a> \n\
        </span> \n\
      </td> \n\
      <td class=\"${player.winPercentage == playerRecordsDTOHigh?.winPercentage ? 'winner' : player.winPercentage == playerRecordsDTOLow?.winPercentage ? 'loser' : ''}\"> \n\
        <span>${PERCENT_FORMAT.format(player.winPercentage)}</span> \n\
      </td> \n\
      <td class=\"${player.goalsForPerGame == playerRecordsDTOHigh?.goalsForPerGame ? 'winner' : player.goalsForPerGame == playerRecordsDTOLow?.goalsForPerGame ? 'loser' : ''}\"> \n\
        <span>${DOUBLE_DECIMAL_FORMAT.format(player.goalsForPerGame)}</span> \n\
      </td> \n\
      <td class=\"${player.goalsAgainstPerGame == playerRecordsDTOHigh?.goalsAgainstPerGame ? 'winner' : player.goalsAgainstPerGame == playerRecordsDTOLow?.goalsAgainstPerGame ? 'loser' : ''}\"> \n\
        <span>${DOUBLE_DECIMAL_FORMAT.format(player.goalsAgainstPerGame)}</span> \n\
      </td> \n\
      <td style=\"text-align:center;\" class=\"${player.currentStreak == player.bestStreak ? '' : player.currentStreak == player.worstStreak ? '' : ''}\"> \n\
        <span>${player.streakDescription}</span> \n\
      </td> \n\
      <td class=\"${player.ratingForPerGame == playerRecordsDTOHigh?.ratingForPerGame ? 'winner' : player.ratingForPerGame == playerRecordsDTOLow?.ratingForPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.ratingForPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.ratingAgainstPerGame == playerRecordsDTOHigh?.ratingAgainstPerGame ? 'winner' : player.ratingAgainstPerGame == playerRecordsDTOLow?.ratingAgainstPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.ratingAgainstPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.drinksForPerGame == playerRecordsDTOHigh?.drinksForPerGame ? 'winner' : player.drinksForPerGame == playerRecordsDTOLow?.drinksForPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.drinksForPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.drinksAgainstPerGame == playerRecordsDTOHigh?.drinksAgainstPerGame ? 'winner' : player.drinksAgainstPerGame == playerRecordsDTOLow?.drinksAgainstPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.drinksAgainstPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"\"> \n\
        <span>${player.bestStreakDescription}</span><span> / </span><span>${player.worstStreakDescription}</span> \n\
      </td> \n\
    </tr> \n"
    }

    out << "\
  </tbody> \n\
</table> \n"

    if (attrs['extendedStats'] != 'false') {
      out << "\
<h4>Extended Stats</h4> \n\
<table class=\"listTable playerTable\"> \n\
  <thead> \n\
  <tr> \n\
    <th class=\"playerColumn sortable\">Name</th> \n\
    <th style=\"width: 25px;\" class=\"sortable\">GF</th> \n\
    <th style=\"width: 25px;\" class=\"sortable\">GA</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">SF/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">SA/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">S%F</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">S%A</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">HF/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">HA/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">TF/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">TA/G</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">P%F</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">P%A</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">DF</th> \n\
    <th style=\"width: 40px;\" class=\"sortable\">DA</th> \n\
  </tr> \n\
  </thead> \n\
  <tbody>"

      players.eachWithIndex { PlayerDTO player, Integer index ->
        out << "\
    <tr class=\"${(index % 2) == 0 ? 'odd' : 'even'}\"> \n\
      <td class=\"playerName\"> \n"
        if (player.id) {
          out << "<a href=\"viewPlayer/${player.id}\">${player.name}</a> \n"
        } else {
          out << "<span>${player.name}</span> \n"
        }
        out << "\
      </td> \n\
      <td>  \n\
        <span>${player.goalsFor}</span> \n\
      </td> \n\
      <td> \n\
        <span>${player.goalsAgainst}</span> \n\
      </td> \n\
      <td class=\"${player.shotsForPerGame == playerRecordsDTOHigh?.shotsForPerGame ? 'winner' : player.shotsForPerGame == playerRecordsDTOLow?.shotsForPerGame ? 'loser' : ''}\"> \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.shotsForPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.shotsAgainstPerGame == playerRecordsDTOHigh?.shotsAgainstPerGame ? 'winner' : player.shotsAgainstPerGame == playerRecordsDTOLow?.shotsAgainstPerGame ? 'loser' : ''}\"> \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.shotsAgainstPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.shotPercentageFor == playerRecordsDTOHigh?.shotPercentageFor ? 'winner' : player.shotPercentageFor == playerRecordsDTOLow?.shotPercentageFor ? 'loser' : ''}\"> \n\
        <span>${PERCENT_FORMAT.format(player.shotPercentageFor ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.shotPercentageAgainst == playerRecordsDTOHigh?.shotPercentageAgainst ? 'winner' : player.shotPercentageAgainst == playerRecordsDTOLow?.shotPercentageAgainst ? 'loser' : ''}\"> \n\
        <span>${PERCENT_FORMAT.format(player.shotPercentageAgainst ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.hitsForPerGame == playerRecordsDTOHigh?.hitsForPerGame ? 'winner' : player.hitsForPerGame == playerRecordsDTOLow?.hitsForPerGame ? 'loser' : ''}\"> \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.hitsForPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.hitsAgainstPerGame == playerRecordsDTOHigh?.hitsAgainstPerGame ? 'winner' : player.hitsAgainstPerGame == playerRecordsDTOLow?.hitsAgainstPerGame ? 'loser' : ''}\"> \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.hitsAgainstPerGame ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.timeOnAttackForPerGame == playerRecordsDTOHigh?.timeOnAttackForPerGame ? 'winner' : player.timeOnAttackForPerGame == playerRecordsDTOLow?.timeOnAttackForPerGame ? 'loser' : ''}\"> \n\
        <span>${player.timeOnAttackForPerGameString ?: '0:00'}</span> \n\
      </td> \n\
      <td class=\"${player.timeOnAttackAgainstPerGame == playerRecordsDTOHigh?.timeOnAttackAgainstPerGame ? 'winner' : player.timeOnAttackAgainstPerGame == playerRecordsDTOLow?.timeOnAttackAgainstPerGame ? 'loser' : ''}\"> \n\
        <span>${player.timeOnAttackAgainstPerGameString ?: '0:00'}</span> \n\
      </td> \n\
      <td class=\"${player.passingPercentageForAverage == playerRecordsDTOHigh?.passingPercentageFor ? 'winner' : player.passingPercentageForAverage == playerRecordsDTOLow?.passingPercentageFor ? 'loser' : ''}\"> \n\
        <span>${PERCENT_FORMAT.format(player.passingPercentageForAverage ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.passingPercentageAgainstAverage == playerRecordsDTOHigh?.passingPercentageAgainst ? 'winner' : player.passingPercentageAgainstAverage == playerRecordsDTOLow?.passingPercentageAgainst ? 'loser' : ''}\"> \n\
        <span>${PERCENT_FORMAT.format(player.passingPercentageAgainstAverage ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.drinksForPerGame == playerRecordsDTOHigh?.drinksForPerGame ? 'winner' : player.drinksForPerGame == playerRecordsDTOLow?.drinksForPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.drinksFor ?: 0)}</span> \n\
      </td> \n\
      <td class=\"${player.drinksAgainstPerGame == playerRecordsDTOHigh?.drinksAgainstPerGame ? 'winner' : player.drinksAgainstPerGame == playerRecordsDTOLow?.drinksAgainstPerGame ? 'loser' : ''}\">  \n\
        <span>${SINGLE_DECIMAL_FORMAT.format(player.drinksAgainst ?: 0)}</span> \n\
      </td> \n\
    </tr> \n"
      }
    }

    out << "\
  </tbody> \n\
</table> \n"
  }



  def gameFilterTable = { attrs, body ->
    GameFilter gameFilter = attrs['gameFilter']
    Boolean renderMaxGamesPerPlayer = attrs['renderMaxGamesPerPlayer'] == 'true'

    out << "\
<table class=\"filterTable\"> \n\
  <tr> \n"
    if (gameFilter.gamePlayerIdFilter) {
      renderPlayerIdFilter(gameFilter.gamePlayerIdFilter, false, "playerIds", "matchingStrategy")
    } else {
      if (gameFilter.team1PlayerIdFilter) {
        renderPlayerIdFilter(gameFilter.team1PlayerIdFilter, false, "playerIds", "matchingStrategy")
        if (gameFilter.team2PlayerIdFilter) {
          out << "<td><div>VS</div></td> \n"
          renderPlayerIdFilter(gameFilter.team2PlayerIdFilter, false, "opponentIds", "opponentMatchingStrategy")
        }
      }
    }
    if (gameFilter.excludePlayerIdFilter) {
      renderPlayerIdFilter(gameFilter.excludePlayerIdFilter, true, "excludePlayerIds", "excludeMatchingStrategy")
    }

    out << "\
    <td> \n\
      <div>Game Version:</div> \n\
      <div> \n\
        <select name=\"gameVersion\" id=\"gameVersion\" > \n\
          <option value=\"null\">All</option> \n"
    for (GameVersion gameVersion in GameVersion.values()) {
      out << "<option value=\"${gameVersion.name()}\" "
      if (gameVersion == gameFilter.gameVersion) {
        out << "selected=\"selected\""
      }
      out << ">${gameVersion.name}</option> \n"
    }
    out << "\
        </select> \n\
      </div> \n\
      <br /> \n\
      <div>From Date:</div> \n\
      <div> \n"

    FormTagLib formTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.FormTagLib')
    def dateAttrs = [:]
    dateAttrs['name'] = 'fromDate'
    dateAttrs['id'] = 'fromDate'
    dateAttrs['default'] = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-2009")
    dateAttrs['value'] = gameFilter.fromDate
    dateAttrs['precision'] = 'day'
    formTagLib.datePicker.call(dateAttrs)

    out << "\
      </div> \n\
      <div>To Date:</div> \n\
      <div> \n"

    dateAttrs['name'] = 'toDate'
    dateAttrs['id'] = 'toDate'
    dateAttrs['default'] = new Date() + 1
    dateAttrs['value'] = gameFilter.toDate
    dateAttrs['precision'] = 'day'
    formTagLib.datePicker.call(dateAttrs)

    out << "\
      </div> \n"
    if (renderMaxGamesPerPlayer) {
      out << "\
      <br /> \n\
      <div>Max Games Per Player:</div> \n\
      <div> \n\
        <input type=\"text\" name=\"maxGamesPerPlayer\" id=\"maxGamesPerPlayer\" value=\"${gameFilter.maxGamesPerPlayer ?: ''}\" /> \n\
      </div> \n"
    }
    out << "\
    </td> \n\
  </tr> \n\
</table>"
  }

  def matchupTable = { attrs, body ->
    MatchupListDTO matchupList = attrs['matchupList']
    List<PlayerDTO> players = matchupList.matchupStats*.player
    attrs['players'] = players
    playerTable.call(attrs, body)
  }

  def nhlTeamTable = { attrs, body ->
    out << "<table width=\"600p\"> \n"
    out << "<tr><th>League</th><th>Rating</th><th>Team</th><th>Offense</th><th>Defense</th><th>Goalie</th></tr> \n"

    NhlLeague.values().each { NhlLeague league ->
      List<NhlTeam> nhlTeams = []
      NhlTeam.each { NhlTeam team ->
        if (team.league == league) {
          nhlTeams.add team
        }
      }

      nhlTeams.sort { NhlTeam team1, NhlTeam team2 ->
        NhlTeamRating rating1 = NhlTeamRating.getLatest(team1)
        NhlTeamRating rating2 = NhlTeamRating.getLatest(team2)
        int r1 = rating1?.totalRating ?: 0
        int r2 = rating2?.totalRating ?: 0
        return r2 - r1
      }

      nhlTeams.each { NhlTeam team ->
        NhlTeamRating rating = NhlTeamRating.getLatest(team)
        if (rating) {
          out << "<tr><td>${league}</td><td>${rating.totalRating}</td><td>${team.fullName}</td><td>${rating.offense}</td><td>${rating.defense}</td><td>${rating.goalie}</td></tr> \n"
        }
      }
    }

    out << "</table> \n"
  }


  def renderPlayerIdFilter = { PlayerIdFilter playerIdFilter, Boolean exclude, String playerSelectElementId, String matchingStrategySelectElementId ->

    def messageSource = grailsApplication.mainContext.getBean('messageSource')

    List<PlayerDTO> players = playerService.findPlayers(null)

    out << "\
    <td width=\"200px\"> \n\
      <div>${exclude ? 'Exclude' : 'Include'} Players:</div> \n\
      <div> \n\
        <select class=\"playerSelect\" name=\"${playerSelectElementId}\" id=\"${playerSelectElementId}\" multiple=\"true\"> \n"
    players.each { player ->
      out << "<option value=\"${player.id}\""
      if (playerIdFilter.contains(player.id)) {
        out << "selected=\"selected\""
      }
      out << ">${player.name}</option>"
    }
    out << "\
        </select> \n\
      </div> \n\
      <div>${exclude ? 'Exclude' : 'Include'} games with:<br /> \n\
        <select name=\"${matchingStrategySelectElementId}\" id=\"${matchingStrategySelectElementId}\" > \n"
    for (MatchingStrategy matchingStrategy in MatchingStrategy.values()) {
      out << "<option value=\"${matchingStrategy.name()}\" "
      if (matchingStrategy == playerIdFilter.matchingStrategy) {
        out << "selected=\"selected\""
      }
      out << ">${messageSource.getMessage(MatchingStrategy.class.name + '.' + matchingStrategy.name(), null, null)}</option> \n"
    }
    out << "\
        </select> \n\
        these players \n\
      </div> \n\
    </td> \n"
  }

  def renderViewPlayerDetails = { Player player, Date date = null ->
    String drinksText = date == null ? "" : " (${gameService.getDrinks(player, date)})"
    out << "<a href=\"viewPlayerDetails?playerIds=${player.id}\">${player.name}</a>${drinksText} \n"
  }


  def formatRating = { rating ->
    if (!rating) {
      return ""
    }
    return (rating as Long) as String
  }
  
  def toPercentageString(Number pct) {
    return pct ? "${pct}%" : ''
  }
}
