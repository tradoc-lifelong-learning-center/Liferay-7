<%@ include file="/init.jsp" %>

<jsp:useBean id="entries" class="java.lang.Object" scope="request"/>

<c:set var="articles" value="${entries.getArticles() }" />

<p>
	<b><liferay-ui:message key="contentstatsdashboard.caption"/></b>
</p>

<%-- <p><c:out value="${articles }"/></p>--%>


<c:forEach items="${articles }" var = "article" varStatus="i">

	<p><c:out value="${article.getTitle() }"/></p>
</c:forEach> 

