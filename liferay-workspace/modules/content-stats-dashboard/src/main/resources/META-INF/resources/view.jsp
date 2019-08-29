<%@ include file="/init.jsp" %>

<jsp:useBean id="entries" class="java.lang.Object" scope="request"/>

<c:set var="articles" value="${entries.getArticles() }" />

<p>
	<b><liferay-ui:message key="contentstatsdashboard.caption"/></b>
</p>

<%-- <p><c:out value="${articles }"/></p>--%>


<table class="table table-striped">
	<thead class="thead-dark">
		<tr>
			<th scope="col">Title</th>
			<th scope="col">ID</th>
			<th scope="col">Type</th>
			<th scope="col">Create Date</th>
			<th scope="col">Modified Date</th>
		</tr>
	</thead>
	<tbody>
	
		<c:forEach items="${articles }" var = "article" varStatus="i">
	
			<tr>
				<td><c:out value="${article.getTitle() }"/></td>
				<td><c:out value="${article.getArticleId() }"/></td>
				<td><c:out value="${article.getType() }"/></td>
				<td><c:out value="${article.getCreateDate() }"/></td>
				<td><c:out value="${article.getModifiedDate() }"/></td>
			</tr>
		</c:forEach> 
	
	
	</tbody>

</table>

<%-- 
<c:forEach items="${articles }" var = "article" varStatus="i">




	<p><c:out value="${article.getTitle() }"/>, <c:out value="${article.getArticleId() }"/></p>
</c:forEach> 

--%>