<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>
  <title>SwerveSoft Hockey - Matchups</title>
</head>
<body>
<div class="body">
  <h1>Player Details: ${playerDetails.playerDTOs*.name}</h1>
  
  <div class="playerListChart">
    <g:if test="${chart}">
      <img width="${chart.width}" height="${chart.height}" alt="chart" src="data:${chart.type};base64,${chart.data.encodeAsBase64()}" />
    </g:if>
  </div>

  <g:each var="matchupList" in="${playerDetails.matchupLists}">
    <h2>${matchupList.description}</h2>
    <br/>
    <h:matchupTable matchupList="${matchupList}" />
    <br/>
  </g:each>

  <div class="filterPanel">
    <g:form action="viewPlayerDetails" method="get">
      <h:gameFilterTable gameFilter="${gameFilter}" />
      <div class="buttonTable">
        <span class="button">
          <g:submitButton name="applyFilter" value="Filter"/>
        </span>
      </div>
    </g:form>
  </div>
</div>
</body>
</html>
