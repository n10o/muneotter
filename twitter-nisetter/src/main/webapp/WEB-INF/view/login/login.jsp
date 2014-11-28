<!DOCTYPE html>
<html>
<head>
	<title>Muneotter</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap-custom.css"/>
</head>
<body>
<header>
	<div class="container">
			<div class="navbar navbar-static-top" >
				<div class="navbar-inner">
					<div class="container">
						<a class="brand" href="${ctx}/"><font color="#00bfff">Muneo</font>tter</a>
					</div>
				</div>
			</div>
	</div>
</header>

<div class="contents">
	<div class="container">
		<div class="hero-unit">
			<h1><font color="#00bfff">Muneo</font><font color="#FFFFFF">tter</font></h1>
			<p><font color="#FFFFFF">Muneo night.</font></p>
			<s:form>
				<p><s:submit styleClass="btn btn-info btn-large" property="redirectTour" value="Learn more &raquo;" /></p>
			</s:form>
		</div>

		<html:errors styleClass="alert alert-warn"/>

		<div class="row">
			<div class="span6">
				<ul class="nav nav-list well">
					<h3>Log<font color="#00bfff">In</font></h3>
					<s:form>
						<html:text property="userName" placeholder="Username (4 to 16 characters)"/>
						<html:password property="password" placeholder="Password  (4 to 32 characters)"/>
						<html:checkbox property="autoLogin"/> AutoLogin
						<p><s:submit styleClass="btn btn-info btn-big" property="doLogin" value="LogIn" /></p>
					</s:form>
				</ul>
			</div>
			<div class="span6">
				<ul class="nav nav-list well">
					<h3>Sign<font color="orange">Up</font></h3>
					<s:form>
						<html:text property="signUpUserName" placeholder="Username (4 to 16 characters)"/>
			            <html:password property="signUpPassword" placeholder="Password  (4 to 32 characters)"/>
						<p><s:submit styleClass="btn btn-warning btn-big" property="doSignup" value="SignUp"/></p>
					</s:form>
				</ul>
			</div>
		</div>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>