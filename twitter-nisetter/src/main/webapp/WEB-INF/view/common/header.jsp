<!DOCTYPE html>
<html>
<head>
	<title>${f:h(param.title)}</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap-custom.css"/>
</head>
<body>
<!-- header start -->
<header>
        <div class="navbar navbar-static-top" >
          <div class="navbar-inner">
			<div class="container">
            <a class="brand" href="${ctx}/member/${f:h(userWebBean.memberName)}"><font color="#00bfff">Muneo</font>tter</a>
            <ul class="nav">
              <li><a href="${ctx}/member/${f:h(userWebBean.memberName)}">Home</a></li>
			</ul>
			<ul class="nav pull-right">

				<c:choose>
					<c:when test="${userWebBean.isLogin}"><li><a>UserName: ${f:h(userWebBean.memberName)}</a></li>
					</c:when>
					<c:otherwise><li><a>Guest (Unregistered)</a></li>
					</c:otherwise>
				</c:choose>

				</li>
			</ul>
			</div>
          </div>
        </div>
</header>
<!-- header end -->
