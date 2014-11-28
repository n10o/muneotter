<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="TweetList"/>
</c:import>

<div class="container">
	<div class="row">
	<html:errors styleClass="alert alert-error"/>
		<div class="span8">
			<c:choose>
				<c:when test="${(empty userWebBean.memberName) && (empty userName)}">
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${userName == userWebBean.memberName}"><h3>${f:h(userName)}'s TimeLine</h3></c:when>
						<c:otherwise><h3>${f:h(userName)}'s Tweet</h3></c:otherwise>
					</c:choose>
					<c:forEach var="tweetbean" items="${tweetBeanList}">
						<table class="table table-striped table-bordered table-condensed"><tbody>
							<p><i class="icon-user"></i> ${f:h(tweetbean.memberName)} <span style="float:right"><i class="icon-time"></i> ${f:h(tweetbean.tweetShortDate)}</span></p>
							<p><i class="icon-file"></i> ${f:h(tweetbean.tweetContents)}</p>
							<p></p>
							<hr></tbody></table>
					</c:forEach>
				</c:otherwise>
			</c:choose>



			<c:import url="${viewPrefix}/common/paging_navi.jsp"/>
		</div>

		<div class="span4">
			<ul class="nav nav-list well">
			<c:if test="${!empty userWebBean.memberName}">
				<c:if test="${userName == userWebBean.memberName}">
					<h3>Tweet</h3>
					<s:form method="POST">
						<html:textarea property="tweetForm" value="" placeholder="Tweet (Max 140 characters)"/><BR>
							<s:submit styleClass="btn btn-info btn-small" property="addTweet" value="Tweet"/>
							<s:submit styleClass="btn btn-info btn-small" property="autoTweet" value="AutoTweet"/>
					</s:form>

					<h3>Follow Someone</h3>
					<h5>Input Follower Name</h5>
					<s:form method="POST">
						<html:text property="followerName" value="" placeholder="Input Follower Name"/><BR>
							<s:submit styleClass="btn btn-info btn-small" property="addFollower" value="Follow"/>
					</s:form>

					<h5>Recommend User</h5>
					<s:form>
						<c:forEach var="recommendBean" items="${memberFollowingRecommendBeanList}">
							<label class="checkbox">
								<html:multibox property="recommendFollowerIdList" value="${f:h(recommendBean.followerId)}" />
								<a href="${ctx}/member/${f:h(recommendBean.followerName)}">${f:h(recommendBean.followerName)}</a>
							</label>
						</c:forEach>
						<s:submit styleClass="btn btn-info btn-small" property="addRecommend" value="Follow"/>
					</s:form>

					<h5>I'm Feeling Lucky</h5>
					<s:form>
						<s:submit styleClass="btn btn-info btn-small" property="autoFollow" value="AutoFollow"/>
					</s:form>

					<c:if test="${!empty memberFollowingBeanList}">
						<h3>Followers</h3>
						<s:form>
							<div class="control-group">
								<div class="controls">
									<c:forEach var="followerBean" items="${memberFollowingBeanList}">
										<label class="checkbox">
											<html:multibox property="stopFollowerIdList" value="${f:h(followerBean.followerId)}" />
											<a href="${ctx}/member/${f:h(followerBean.followerName)}">${f:h(followerBean.followerName)}</a>
										</label>
									</c:forEach>
								</div>
							</div>
							<s:submit styleClass="btn btn-info btn-small" property="stopFollow" value="UnFollow"/>
						</s:form>
					</c:if>

			    </c:if>
			</c:if>

			<s:form>
			<c:choose>
				<c:when test="${userWebBean.isLogin}">
					<h3>Are you tired?</h3>
					<input class="btn btn-info btn-small" type="submit" name="logout" value="Logout">
				</c:when>
				<c:otherwise>
					<h3>SignUp</h3>
					<input class="btn btn-info btn-small" type="submit" name="goToppage" value="SignUp">
				</c:otherwise>
			</c:choose>
			</s:form>
			</ul>
		</div>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>
