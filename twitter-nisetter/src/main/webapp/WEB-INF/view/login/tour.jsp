<!DOCTYPE html>
<html>
<head>
	<title>Muneotter</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bootstrap-custom.css"/>
</head>
<body>
<!-- header start -->
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
			<h1><font color="#00bfff">Muneo</font><font color="#FFFFFF">tter Tour</font></h1>
      　　	</div>

		<ul class="nav nav-list well">
			<h2>Implemented Functions</font></h2>
			<h4>ログイン/ログアウト/サインアップ</font></h4>
			<li>利用可能文字は半角英数字</li>
			<h4>ツイート</h4>
			<li>最大140文字</li>
			<li>ツイート数が10を超えるとページングされます。http://FQDN/member/memberName/数字でアクセス可能</li>
			<h4>フォロー/フォロー解除</h4>
			<li>フォローすると、他ユーザのツイートが取り込める。解除すると見えなくなる。</li>
			<li>フォロー解除はフォローユーザ名のの左側にあるチェックボックスを選択して、UnFollowボタンを押下</li>
			<h4>メンバーページのURLを生成</h4>
			<li>例 http://FQDN/member/memberName</li>
			<li>存在しない場合はMember not foundページが表示</li>
			<li>自分のページでは自分とフォロワーのタイムラインを表示。他人のページではその人のツイートのみを表示</li>
			<li>ログインしていない場合はゲストユーザとして、他人のページを表示可能</li>
			<h4>他にも色々加わりましたが、説明が大変なので、詳しくはメールを参照下さい</h4>
		</ul>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>