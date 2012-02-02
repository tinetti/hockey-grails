<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>

  <title>SwerveSoft Hockey - Session List</title>
</head>
<body>
<div class="body">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <h1>Session List</h1>
  <h:hockeySessionTables hockeySessionList="${hockeySessionList}" />
</div>
</body>
</html>
