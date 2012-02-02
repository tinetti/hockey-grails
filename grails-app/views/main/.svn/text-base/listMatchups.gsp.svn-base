<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>
  <title>SwerveSoft Hockey - Matchups</title>
</head>
<body>
<div class="body">
  <h1>Matchups</h1>

  <g:if test="${matchupList}">
    <h3>${matchupList.description}</h3>
    <br/>
    <h:matchupTable matchupList="${matchupList}" />
    <br/>
  </g:if>

  <div class="wideFilterPanel">
    <g:form action="listMatchups" method="get">
      <h:gameFilterTable gameFilter="${gameFilter}" />
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
