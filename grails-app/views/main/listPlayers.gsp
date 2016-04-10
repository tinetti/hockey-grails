<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>

  <title>SwerveSoft Hockey - Player List</title>
</head>
<body>
<div class="body">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <h1>Player List</h1>
  <div>
    <h:playerTable players="${players}" high="${playerRecordsDTOHigh}" low="${playerRecordsDTOLow}" />
  </div>

  <div class="playerListChart">
    <g:if test="${charts}">
      <g:each in="${charts}" var="chart">
      	<br />
      	<h3>${chart.description}</h3>
	    <img width="${chart.width}" height="${chart.height}" alt="chart" src="data:${chart.type};base64,${chart.data.encodeAsBase64()}" />
	  </g:each>
    </g:if>
  </div>

  <div class="filterPanel">
    <g:form action="listPlayers" method="get">
      <h:gameFilterTable gameFilter="${gameFilter}" renderMaxGamesPerPlayer="true" />
      <div class="buttonTable">
        <span class="button">
          <g:submitButton name="applyFilter" value="Filter"/>
          <g:submitButton name="resetFilter" value="Reset"/>
        </span>
      </div>
    </g:form>
  </div>
</div>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</body>
</html>
