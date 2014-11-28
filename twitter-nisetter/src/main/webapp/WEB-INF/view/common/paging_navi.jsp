<%@page pageEncoding="UTF-8"%>
<%--
pagingNavi という名前で Action から PagingNavi が取得できることが前提
--%>
<c:if test="${pagingNavi.displayPagingNavi}">
	<c:if test="${pagingNavi.allPageCount != 1}">

<div class="paging-navi">
	<c:if test="${pagingNavi.existPrePage}">
		<s:link href="${f:h(userName)}/${f:h(pagingNavi.prePageLinkHref)}">Back</s:link>
	</c:if>
	<c:forEach var="links" items="${pagingNavi.pageNumberLinkList}">
		<c:if test="${!links.current}">
			<span id="pageNumberElement"><s:link href="${f:h(userName)}/${f:h(links.pageNumberLinkHref)}">${f:h(links.pageNumberElement)}</s:link></span>
		</c:if>
		<c:if test="${links.current}">
			<span id="pageNumberElement">${f:h(links.pageNumberElement)}</span>
		</c:if>
	</c:forEach>
	[${f:h(pagingNavi.currentPageNumber)}/${f:h(pagingNavi.allPageCount)}]
	<c:if test="${pagingNavi.existNextPage}">
		<s:link href="${f:h(userName)}/${f:h(pagingNavi.nextPageLinkHref)}">Next</s:link>
	</c:if>
</div>

</c:if>
</c:if>
