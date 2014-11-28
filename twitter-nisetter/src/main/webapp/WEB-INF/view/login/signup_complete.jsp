<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="SignUp Success"/>
</c:import>
<div class="contents">
	<div class="container">
		<div class="row">
			<html:errors />
				<ul class="nav nav-list well">
					<h2>Sign<font color="orange">Up</font> Completed.</h2>
					<p>Forget Username or Password ? We can't help you.</p><BR>
					<s:form>
						<input class="btn btn-info btn-big" type="submit" name="redirectIndex" value="Go LogIn Page">
					</s:form>
				</ul>
			</div>
		</div>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>