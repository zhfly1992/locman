<%@page import="com.alibaba.druid.sql.visitor.functions.If"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
 <%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page language='java'%>
<jsp:useBean id='loginUtil' scope='request'
	class='com.run.locman.login.loginUtil' />
<%@ page import='java.util.*'%>
<%@ page import='java.sql.*'%>

<html>
<head>

<style type="text/css">
[uib-typeahead-popup].dropdown-menu {
	display: block;
}
</style>
<style type="text/css">
.uib-time input {
	width: 50px;
}
</style>
<style type="text/css">
[uib-tooltip-popup].tooltip.top-left>.tooltip-arrow, [uib-tooltip-popup].tooltip.top-right>.tooltip-arrow,
	[uib-tooltip-popup].tooltip.bottom-left>.tooltip-arrow, [uib-tooltip-popup].tooltip.bottom-right>.tooltip-arrow,
	[uib-tooltip-popup].tooltip.left-top>.tooltip-arrow, [uib-tooltip-popup].tooltip.left-bottom>.tooltip-arrow,
	[uib-tooltip-popup].tooltip.right-top>.tooltip-arrow, [uib-tooltip-popup].tooltip.right-bottom>.tooltip-arrow,
	[uib-tooltip-html-popup].tooltip.top-left>.tooltip-arrow, [uib-tooltip-html-popup].tooltip.top-right>.tooltip-arrow,
	[uib-tooltip-html-popup].tooltip.bottom-left>.tooltip-arrow, [uib-tooltip-html-popup].tooltip.bottom-right>.tooltip-arrow,
	[uib-tooltip-html-popup].tooltip.left-top>.tooltip-arrow, [uib-tooltip-html-popup].tooltip.left-bottom>.tooltip-arrow,
	[uib-tooltip-html-popup].tooltip.right-top>.tooltip-arrow, [uib-tooltip-html-popup].tooltip.right-bottom>.tooltip-arrow,
	[uib-tooltip-template-popup].tooltip.top-left>.tooltip-arrow, [uib-tooltip-template-popup].tooltip.top-right>.tooltip-arrow,
	[uib-tooltip-template-popup].tooltip.bottom-left>.tooltip-arrow, [uib-tooltip-template-popup].tooltip.bottom-right>.tooltip-arrow,
	[uib-tooltip-template-popup].tooltip.left-top>.tooltip-arrow, [uib-tooltip-template-popup].tooltip.left-bottom>.tooltip-arrow,
	[uib-tooltip-template-popup].tooltip.right-top>.tooltip-arrow, [uib-tooltip-template-popup].tooltip.right-bottom>.tooltip-arrow,
	[uib-popover-popup].popover.top-left>.arrow, [uib-popover-popup].popover.top-right>.arrow,
	[uib-popover-popup].popover.bottom-left>.arrow, [uib-popover-popup].popover.bottom-right>.arrow,
	[uib-popover-popup].popover.left-top>.arrow, [uib-popover-popup].popover.left-bottom>.arrow,
	[uib-popover-popup].popover.right-top>.arrow, [uib-popover-popup].popover.right-bottom>.arrow,
	[uib-popover-html-popup].popover.top-left>.arrow, [uib-popover-html-popup].popover.top-right>.arrow,
	[uib-popover-html-popup].popover.bottom-left>.arrow, [uib-popover-html-popup].popover.bottom-right>.arrow,
	[uib-popover-html-popup].popover.left-top>.arrow, [uib-popover-html-popup].popover.left-bottom>.arrow,
	[uib-popover-html-popup].popover.right-top>.arrow, [uib-popover-html-popup].popover.right-bottom>.arrow,
	[uib-popover-template-popup].popover.top-left>.arrow, [uib-popover-template-popup].popover.top-right>.arrow,
	[uib-popover-template-popup].popover.bottom-left>.arrow, [uib-popover-template-popup].popover.bottom-right>.arrow,
	[uib-popover-template-popup].popover.left-top>.arrow, [uib-popover-template-popup].popover.left-bottom>.arrow,
	[uib-popover-template-popup].popover.right-top>.arrow, [uib-popover-template-popup].popover.right-bottom>.arrow
	{
	top: auto;
	bottom: auto;
	left: auto;
	right: auto;
	margin: 0;
}

[uib-popover-popup].popover, [uib-popover-html-popup].popover, [uib-popover-template-popup].popover
	{
	display: block !important;
}
</style>
<style type="text/css">
.uib-datepicker-popup.dropdown-menu {
	display: block;
	float: none;
	margin: 0;
}

.uib-button-bar {
	padding: 10px 9px 2px;
}
</style>
<style type="text/css">
.uib-position-measure {
	display: block !important;
	visibility: hidden !important;
	position: absolute !important;
	top: -9999px !important;
	left: -9999px !important;
}

.uib-position-scrollbar-measure {
	position: absolute !important;
	top: -9999px !important;
	width: 50px !important;
	height: 50px !important;
	overflow: scroll !important;
}

.uib-position-body-scrollbar-measure {
	overflow: scroll !important;
}
</style>
<style type="text/css">
.uib-datepicker .uib-title {
	width: 100%;
}

.uib-day button, .uib-month button, .uib-year button {
	min-width: 100%;
}

.uib-left, .uib-right {
	width: 100%
}
</style>
<style type="text/css">
.ng-animate




.item




:not


 


(
.left


 


)
:not


 


(
.right


 


){
-webkit-transition




:


 


0
s


 


ease-in-out


 


left




;
transition




:


 


0
s


 


ease-in-out


 


left






}
</style>
<style type="text/css">
@charset "UTF-8";

[
ng\:cloak
]
,
[
ng-cloak
]
,
[
data-ng-cloak
]
,
[
x-ng-cloak
]
,
.ng-cloak
,
.x-ng-cloak
,
.ng-hide




:not


 


(
.ng-hide-animate


 


){
display




:


 


none


 


!
important




;
}
ng\:form {
	display: block;
}

.ng-animate-shim {
	visibility: hidden;
}

.ng-anchor {
	position: absolute;
}
</style>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- <title translate="TOP_LOGO_TITLE" class="ng-scope">AEP-使能平台</title> -->
<title>AEP-使能平台</title>
<link href="../../static/images/favicon.ico" rel="shortcut icon"
	type="image/x-icon">
<link rel="stylesheet" type="text/css"
	href="../../static/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="../../static/css/iconfont.css">
<link rel="stylesheet" type="text/css"
	href="../../static/css/jquery.fullpage.css">
<!--offline libs  start-->

<!--online libs  start-->
<link href="../../static/css/bootstrap.min.css" rel="stylesheet">
<link href="../../static/css/animate.min.css" rel="stylesheet">
<link href="../../static/css/ui-grid.min.css" rel="stylesheet">
<link href="../../static/css/jquery.datetimepicker.min.css" rel="stylesheet">

<!--online libs  end-->
<link rel="stylesheet" type="text/css"
	href="../../static/css/nanoscroller.css">
<link rel="stylesheet" type="text/css" href="../../static/css/base.css">

</head>

<%-- <jsp:include  page="../../static/jsp/check.jsp" flush="true"/> --%>
<%-- <body>
	<img src="../../static/images/test.jpg">
	<div>${test}</div>
</body> --%>



<script language="JavaScript" src="<%=path%>/static/js/md5.js"></script>
<script type="text/javascript" src="../../static/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="../../static/js/check_register.js"></script>
<script>
<%-- function checkUserExit(){
	console.log("111");
	<%
	System.out.println("222222");
	System.out.println(request.getParameter("account"));
	if (StringUtils.isNotBlank(request.getParameter("account"))) {
		String userInfo = loginUtil.checkUserExit(request);
		System.out.println(userInfo);
	}%>
}; --%>

function login(){
	
	<%request.getParameter("account");//获取用户输入UserID
			request.getParameter("password"); //获取用户输入的Password 
			if (StringUtils.isNotBlank(request.getParameter("account"))
					&& StringUtils.isNotBlank(request.getParameter("password"))) {

				request.getParameter("password");
				request.setAttribute("password", "");
				String loginInfo = loginUtil.login(request);
				System.out.println(request.getParameter("account"));
				System.out.println(request.getParameter("password"));
				System.out.println(loginInfo);
			}%>
};

/* 
var xmlHttp;

function createXMLHttpRequest() {
	//表示当前浏览器不是ie,如ns,firefox
	if(window.XMLHttpRequest) {
		xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
}


//field为获取用户所填写的用户名
function validate(field) {
    //判断用户名是否为空
	if (trim(field.value).length != 0) {
		//创建Ajax核心对象XMLHttpRequest
		createXMLHttpRequest();
		//将获取用户名发送到另一个jsp中去验证
		var url = "user_validate.jsp?userId=" + trim(field.value) + "&time=" + new Date().getTime();
		
		//设置请求方式为GET，设置请求的URL，设置为异步提交，true为异步，false为同步
		xmlHttp.open("GET", url, true);	
		//将方法地址复制给onreadystatechange属性
		//类似于电话号码
		xmlHttp.onreadystatechange=callback;
		
		//将设置信息发送到Ajax引擎
		xmlHttp.send(null);
	} else {
		document.getElementById("spanUserId").innerHTML = "";
	}
}

//发送请求之后，返回的状体
function callback() {
	//alert(xmlHttp.readyState);
	//Ajax引擎状态为成功
	if (xmlHttp.readyState == 4) {
		//HTTP协议状态为成功
		if (xmlHttp.status == 200) {
			if (trim(xmlHttp.responseText) != "") {
				//设置请返回的消息信息
				document.getElementById("spanUserId").innerHTML = "<font color='red'>" + xmlHttp.responseText + "</font>"
			}else {
				document.getElementById("spanUserId").innerHTML = "";
			}
		}else {
			alert("请求失败，错误码=" + xmlHttp.status);
		}
	}
}
 */

</script>


<body>



	<div class="framework ng-scope" style="">
		<div class="aep-login-container-body ng-scope">
			<div class="nano has-scrollbar" always-visible="true">
				<div class="nano-content" ng-transclude="" tabindex="0"
					style="right: -17px;">
					<div class="login-header-line ng-scope"></div>
					<div class="login-header-box ng-scope">
						<div class="logo-box-name">${test}智能井盖管理平台</div>
					</div>
					<div class="login-container-middle ng-scope">
						<div class="container-middle-center">
							<div class="middle-left">
								<h2 class="sefon-info animated slideInLeft delay-02s">智能井盖管理平台
									震撼上线！</h2>
								<p class="animated slideInLeft delay-04s">轻松安全地连接设备</p>
								<p class="animated slideInLeft delay-06s">快速开发物联网应用</p>
								<p class="animated slideInLeft delay-08s">在物联网大数据中挖掘价值</p>
							</div>
							<div class="middle-right animated slideInRight delay-02s">
								<div class="login-box">
									<div class="login-top-line"></div>
									<!-- <div class="login-box-icon"></div> -->
									<div class="login-box-name">登&nbsp;&nbsp;&nbsp;&nbsp;录</div>
									<form name="loginForm"
										class="" id="loginForm">

										<!-- <div class="form-group-login validation-info ng-scope" ng-if="Validation===false" style="">
			                                <div class="login-verify">
			                                    <span class="fa fa-exclamation-circle login-verify-icon"></span>
			                                    <span class="login-verify-text ng-binding">用户不存在，请重新输入</span>
			                                </div>
			                            </div> -->

										
										<div class="form-group-login">
											<div class="login-input-icon icon-user"></div>
											<input type="text"
												class="form-control login-user ng-pristine ng-untouched ng-empty ng-invalid ng-invalid-required inputClass"
												name="account" placeholder="请输入用户名称" id="account">
												
												<div style="text-align:center; height:16px" >
													<label id="account_msg"></label>
												</div>
										</div>
										<div class="form-group-login">
											<div class="login-input-icon icon-password"></div>
											<input type="password"
												class="form-control login-password ng-pristine ng-untouched ng-empty ng-invalid inputClass"
												name="password"
												placeholder="请输入登录密码" id="password_">
												
												<div style="text-align:center; height:16px" >
													<label id="password_msg" ></label>
												</div>
										</div>
										<div class="form-group-login">
											<button onclick="login()"
												class="btn btn-com btn-block login-submit animated faster">登
												录</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<div class="footer ng-scope">
						<h4>专业技术咨询&nbsp;&nbsp;&nbsp;&nbsp;全方位产品解读&nbsp;&nbsp;&nbsp;&nbsp;成功客户案例分享</h4>
						<ul>
							<li><a>首页</a></li>
							<li><a>关于四方</a></li>
							<li><a>联系我们</a></li>
							<li><a>合作伙伴</a></li>
							<li><a>产品咨询</a></li>
							<li><a>友情链接</a></li>
							<li><a>售后服务</a></li>
							<li><a>技术论坛</a></li>
							<li><a>管理团队</a></li>
							<li><a>加入我们</a></li>
						</ul>
						<p class="ng-scope">Copyright © 1993-2020 成都四方信息技术有限公司 |
							四川省成都市高新区高朋大道11号22C座 | 版权所有 | 蜀ICP备12022557号-4</p>
					</div>
				</div>
				<div class="nano-pane" style="opacity: 1; visibility: visible;">
					<div class="nano-slider"
						style="height: 218px; transform: translate(0px, 0px);"></div>
				</div>
			</div>
		</div>
	</div>


	<%-- 
<%
	System.out.print(request.getServerName());
	String userAccount = "account";//获取用户输入UserID 
	String password = "password"; //获取用户输入的Password 
	String loginInfo = loginUtil.login(userAccount, password);
	%> --%>

</body>
</html>