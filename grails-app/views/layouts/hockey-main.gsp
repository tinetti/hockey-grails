<html>
<head>
  <meta name="viewport" content="width=device-width; initial-scale=1.0;user-scalable=no;"/>
  <title><g:layoutTitle default="SwerveSoft Hockey"/></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'hockey-main.css')}"/>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <g:layoutHead/>
  <g:javascript library="application"/>
</head>
<body>
<div id="spinner" class="spinner" style="display:none;">
  <img src="${resource(dir: 'images', file: 'spinner.gif')}" alt="${message(code: 'spinner.alt', default: 'Loading...')}"/>
</div>
<!--      <h1 align="left" style="padding-left: 10px;">SwerveSoft Hockey</h1>-->
<!--        <div id="hockeyLogo"><a href="http://swervesoft.com/hockey"><img src="${resource(dir: 'images', file: 'hockey_logo.png')}" alt="Hockey" border="0" /></a></div>-->

<table class="navTable">
  <tr>
    <td><span><g:link class="menuButton" controller="main" action="createGame">Enter Game</g:link></span></td>
    <td><span><g:link class="menuButton" controller="main" action="listPlayers">Players</g:link></span></td>
    <td><span><g:link class="menuButton" controller="main" action="listGames">Games</g:link></span></td>
    <td><span><g:link class="menuButton" controller="main" action="listHockeySessions">Sessions</g:link></span></td>
    <td><span><g:link class="menuButton" controller="main" action="listMatchups">Matchups</g:link></span></td>
    <td><span><g:link class="menuButton" controller="main" action="listRecords">Records</g:link></span></td>
    %{--<td><span><g:link class="menuButton" controller="player" action="create">Add Player</g:link></span></td>--}%
  </tr>
</table>
<div id="mainBody">
  <g:layoutBody/>
</div>
</body>
</html>