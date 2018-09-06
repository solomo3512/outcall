<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" %> 
<html>

 <head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title> TEST TOMCAT </title>
  <meta name="Generator" content="EditPlus">
  <meta name="Author" content="">
  <meta name="Keywords" content="">
  <meta name="Description" content="">
 </head>

 <body>
    <center><h1>Welecome to ASServer 2017-11-05 </h1></center>
 </body>
 
 <head>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="hojo/hojo.js" djConfig="isDebug:false, parseOnLoad:false"></script>
<script type="text/javascript">
function logon() {
	var loginName = hojo.byId("icc.login.loginName").value;
	var password = hojo.byId("icc.login.password").value;
	var loginType = "Local"; //手机
//    var loginType = "sip"; //软电话
//    var loginType = "gateway"; //IP话机或者语音网关
	window.location.href = "./phoneBar/phonebar.html?loginName=" + loginName + "&password=" + password + "&loginType=" + loginType;
}
</script>
</head>
<body>
	<div align="center">
        <table border=1 cellpadding="0" cellspacing="0">
			<tr>
				<td>UserName:</td><td><input type="text" id="icc.login.loginName" value=""/></td>
			</tr>
			<tr>
				<td>密码：</td><td><input type="password" id="icc.login.password" value="" /></td>
			</tr>
			<tr>
				<td colspan="2" align="right">
					<a href="javascript:logon();">登录</a>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>