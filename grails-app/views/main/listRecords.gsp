<%--
  Created by IntelliJ IDEA.
  User: tinetti
  Date: Oct 3, 2010
  Time: 4:44:36 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="hockey-main"/>
  <title>Records</title>
</head>
<body>

<div class="body">
  <h1>Records</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:each in="${recordGameLists}" var="recordGameList">
    <g:set var="tableId" value="table_id_${recordGameList.hashCode()}" />
    <h2>
      <input type="button" value="show" onclick="document.getElementById('${tableId}').style.display = 'block';" />
      ${recordGameList.description}
    </h2>
    <div id="${tableId}" style="display: none;">
      <input type="button" value="hide" onclick="document.getElementById('${tableId}').style.display = 'none';" />
      <h:gameTable games="${recordGameList.recordGames}" showDeleteLink="false" showEditLink="false" />
      <input type="button" value="hide" onclick="document.getElementById('${tableId}').style.display = 'none';" />
    </div>
  </g:each>

  <br />

  <div class="filterPanel">
    <g:form action="listRecords" method="get">
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
