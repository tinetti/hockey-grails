<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>
  <title>SwerveSoft Hockey - Game List</title>
</head>
<body>
<div class="body">
  <g:if test="${flash.message}">
    <h1>${flash.message}</h1>
  </g:if>
  <g:else>
    <h1>Game List</h1>
  </g:else>

  <div class="list gameList">
    <g:if test="${flash['newGameRecords']}">
      <g:each in="${flash['newGameRecords']}" var="newGameRecord">
        <div class="message">${newGameRecord.description}</div>
      </g:each>
    </g:if>
    <h:gameTable games="${games}" gameCount="${gameCount}" showEditLink="true" showDeleteLink="true" />
  </div>

  <div class="filterPanel">
    <g:form action="listGames" method="get">
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
