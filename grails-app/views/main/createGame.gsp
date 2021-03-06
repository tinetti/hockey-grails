<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>
  <title>Create Game</title>
</head>
<body>

  <g:if test="${prediction}">
    <h1>Predict Game: ${prediction.description}</h1>
  </g:if>
  <g:else>
    <h1>${gameId ? 'Edit' : 'Enter'} Game</h1>
  </g:else>

<div class="createGameBody">

  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${command}">
    <div class="errors">
      <g:renderErrors bean="${command}" as="list"/>
    </div>
  </g:hasErrors>
  <g:hasErrors bean="${game}">
    <div class="errors">
      <g:renderErrors bean="${game}" as="list"/>
    </div>
  </g:hasErrors>
  <g:hasErrors bean="${game?.awayStats}">
    <div class="errors">
      <g:renderErrors bean="${game.awayStats}" as="list"/>
    </div>
  </g:hasErrors>
  <g:hasErrors bean="${game?.homeStats}">
    <div class="errors">
      <g:renderErrors bean="${game.homeStats}" as="list"/>
    </div>
  </g:hasErrors>

  <g:set var="singleDecimalFormat" value="${new java.text.DecimalFormat('0.0')}" />

  <g:form method="post">
    <input type="hidden" name="gameId" value="${gameId}" />
    <div class="dialog">
      <table class="createGameTable">
        <thead>
        <tr>
          <th><span></span></th>
          <th><span>Away</span></th>
          <th><span>Home</span></th>
        </tr>
        </thead>
        <tbody>

        <tr>
          <td class="label">Players:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayPlayerIds', 'errors')}">
            <g:select value="${command.awayPlayerIds}" name="awayPlayerIds" id="awayPlayerIds" from="${playerList}" optionKey="id" optionValue="name" multiple="true" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homePlayerIds', 'errors')}">
            <g:select value="${command.homePlayerIds}" name="homePlayerIds" id="homePlayerIds" from="${playerList}" optionKey="id" optionValue="name" multiple="true" class="createGameValueEditor"/>
          </td>
        </tr>

        <tr>
          <td class="label">Team:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayTeam', 'errors')}">
            <g:select name="awayTeam" id="awayTeamName" from="${ss.hockey.NhlTeam.enumConstants}" optionValue="fullNameWithRating" value="${command?.awayTeam}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homeTeam', 'errors')}">
            <g:select name="homeTeam" id="homeTeamName" from="${ss.hockey.NhlTeam.enumConstants}" optionValue="fullNameWithRating" value="${command?.homeTeam}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PScore:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayScore)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homeScore)}</span></td>
          </tr>
          <tr class="predictionRow">
            <td class="label">Handicap:</td>
            <td><span>${singleDecimalFormat.format(((prediction.homeScore - prediction.awayScore) * 20 / 3))}</span></td>
            <td><span>${singleDecimalFormat.format(((prediction.awayScore - prediction.homeScore) * 20 / 3))}</span></td>
          </tr>
        </g:if>

        <tr>
          <td class="label">Score:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayScore', 'errors')}">
            <g:select value="${command?.awayScore}" name="awayScore" id="awayScore" from="${0..30}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homeScore', 'errors')}">
            <g:select value="${command?.homeScore}" name="homeScore" id="homeScore" from="${0..30}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PShots:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayShots)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homeShots)}</span></td>
          </tr>
        </g:if>

        <tr>
          <td class="label">Shots:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayShots', 'errors')}">
            <input type="tel" name="awayShots" id="awayShots" value="${command?.awayShots}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homeShots', 'errors')}">
            <input type="tel" name="homeShots" id="homeShots" value="${command?.homeShots}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PHits:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayHits)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homeHits)}</span></td>
          </tr>
        </g:if>

        <tr>
          <td class="label">Hits:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayHits', 'errors')}">
            <input type="tel" name="awayHits" id="awayHits" value="${command?.awayHits}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homeHits', 'errors')}">
            <input type="tel" name="homeHits" id="homeHits" value="${command?.homeHits}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PTOA:</td>
            <td><span>${ss.hockey.DateUtils.secondsToString(prediction.awayTimeOnAttack ?: 0)}</span></td>
            <td><span>${ss.hockey.DateUtils.secondsToString(prediction.homeTimeOnAttack ?: 0)}</span></td>
          </tr>
        </g:if>

        <tr>
          <td class="label">TOA:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayTimeOnAttackString', 'errors')}">
            <input type="tel" name="awayTimeOnAttackString" id="awayTimeOnAttack" value="${command?.awayTimeOnAttackString}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homeTimeOnAttackString', 'errors')}">
            <input type="tel" name="homeTimeOnAttackString" id="homeTimeOnAttack" value="${command?.homeTimeOnAttackString}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PPass %:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayPassingPercentage)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homePassingPercentage)}</span></td>
          </tr>
        </g:if>

        <tr>
          <td class="label">Pass %:</td>
          <td class="value ${hasErrors(bean: command, field: 'awayPassingPercentage', 'errors')}">
            <input type="tel" name="awayPassingPercentage" id="awayPassingPercentage" value="${command?.awayPassingPercentage}" class="createGameValueEditor"/>
          </td>
          <td class="value ${hasErrors(bean: command, field: 'homePassingPercentage', 'errors')}">
            <input type="tel" name="homePassingPercentage" id="homePassingPercentage" value="${command?.homePassingPercentage}" class="createGameValueEditor"/>
          </td>
        </tr>

        <g:if test="${prediction}">
          <tr class="predictionRow">
            <td class="label">PRating:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayRating ?: 0)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homeRating ?: 0)}</span></td>
          </tr>
          <tr class="predictionRow">
            <td class="label">PDrinks:</td>
            <td><span>${singleDecimalFormat.format(prediction.awayDrinks ?: 0)}</span></td>
            <td><span>${singleDecimalFormat.format(prediction.homeDrinks ?: 0)}</span></td>
          </tr>
        </g:if>

        <tr class="prop">
          <td>Length:</td>
          <td class="value ${hasErrors(bean: command, field: 'gameLength', 'errors')}">
            <g:select name="gameLength" id="gameLength" from="${ss.hockey.GameLength.enumConstants}" optionValue="name" value="${command?.gameLength}" class="createGameValueEditor"/>
          </td>
          <td/>
        </tr>

        <tr>
          <td class="label">Notes:</td>
          <td colspan="2" class="value ${hasErrors(bean: command, field: 'notes', 'errors')}">
            <g:textField style="width: 347px;" name="notes" value="${command?.notes}" optionValue="name" class="createGameValueEditor"/>
          </td>
        </tr>

        <tr>
          <td class="label">Version:</td>
          <td colspan="2" class="value ${hasErrors(bean: command, field: 'gameVersion', 'errors')}">
            <g:select name="gameVersion" id="gameVersionName" from="${ss.hockey.GameVersion.enumConstants}" optionValue="name" value="${command?.gameVersion}" class="createGameValueEditor"/>
          </td>
        </tr>

        <tr>
          <td class="label">Date:</td>
          <td colspan="2" class="value ${hasErrors(bean: command, field: 'date', 'errors')}">
            <g:datePicker name="date" id="date" default="${new Date()}" value="${command?.date}" precision="minute" />
          </td>
        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttonTable">
      <g:if test="${prediction || gameId}">
        <span class="button">
      	  <g:actionSubmit value="Save" action="saveGame" />
        </span>
      </g:if>
      <g:if test="${!gameId}">
        <span class="button">
      	  <g:actionSubmit value="Predict" action="predictGame" />
        </span>
      </g:if>
    </div>
  </g:form>

</div>
<div>

  <g:if test="${matchupList}">
    <h1>Matchups</h1>
    <h:matchupTable matchupList="${matchupList}" extendedStats="false" />
    <br/>
  </g:if>
  
  <g:if test="${previousGames?.size()}">
    <h1>Previous Games</h1>
    <h:gameTable games="${previousGames}" gameCount="${previousGames.size()}" showEditLink="false" showDeleteLink="false" />
    <br/>
  </g:if>

  <h1>Teams</h1>
  <h:nhlTeamTable />

<!-- ${prediction} -->
</div>
</body>
</html>