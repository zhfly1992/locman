<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><style type="text/css">[uib-typeahead-popup].dropdown-menu{display:block;}</style><style type="text/css">.uib-time input{width:50px;}</style><style type="text/css">[uib-tooltip-popup].tooltip.top-left > .tooltip-arrow,[uib-tooltip-popup].tooltip.top-right > .tooltip-arrow,[uib-tooltip-popup].tooltip.bottom-left > .tooltip-arrow,[uib-tooltip-popup].tooltip.bottom-right > .tooltip-arrow,[uib-tooltip-popup].tooltip.left-top > .tooltip-arrow,[uib-tooltip-popup].tooltip.left-bottom > .tooltip-arrow,[uib-tooltip-popup].tooltip.right-top > .tooltip-arrow,[uib-tooltip-popup].tooltip.right-bottom > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.top-left > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.top-right > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.bottom-left > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.bottom-right > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.left-top > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.left-bottom > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.right-top > .tooltip-arrow,[uib-tooltip-html-popup].tooltip.right-bottom > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.top-left > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.top-right > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.bottom-left > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.bottom-right > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.left-top > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.left-bottom > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.right-top > .tooltip-arrow,[uib-tooltip-template-popup].tooltip.right-bottom > .tooltip-arrow,[uib-popover-popup].popover.top-left > .arrow,[uib-popover-popup].popover.top-right > .arrow,[uib-popover-popup].popover.bottom-left > .arrow,[uib-popover-popup].popover.bottom-right > .arrow,[uib-popover-popup].popover.left-top > .arrow,[uib-popover-popup].popover.left-bottom > .arrow,[uib-popover-popup].popover.right-top > .arrow,[uib-popover-popup].popover.right-bottom > .arrow,[uib-popover-html-popup].popover.top-left > .arrow,[uib-popover-html-popup].popover.top-right > .arrow,[uib-popover-html-popup].popover.bottom-left > .arrow,[uib-popover-html-popup].popover.bottom-right > .arrow,[uib-popover-html-popup].popover.left-top > .arrow,[uib-popover-html-popup].popover.left-bottom > .arrow,[uib-popover-html-popup].popover.right-top > .arrow,[uib-popover-html-popup].popover.right-bottom > .arrow,[uib-popover-template-popup].popover.top-left > .arrow,[uib-popover-template-popup].popover.top-right > .arrow,[uib-popover-template-popup].popover.bottom-left > .arrow,[uib-popover-template-popup].popover.bottom-right > .arrow,[uib-popover-template-popup].popover.left-top > .arrow,[uib-popover-template-popup].popover.left-bottom > .arrow,[uib-popover-template-popup].popover.right-top > .arrow,[uib-popover-template-popup].popover.right-bottom > .arrow{top:auto;bottom:auto;left:auto;right:auto;margin:0;}[uib-popover-popup].popover,[uib-popover-html-popup].popover,[uib-popover-template-popup].popover{display:block !important;}</style><style type="text/css">.uib-datepicker-popup.dropdown-menu{display:block;float:none;margin:0;}.uib-button-bar{padding:10px 9px 2px;}</style><style type="text/css">.uib-position-measure{display:block !important;visibility:hidden !important;position:absolute !important;top:-9999px !important;left:-9999px !important;}.uib-position-scrollbar-measure{position:absolute !important;top:-9999px !important;width:50px !important;height:50px !important;overflow:scroll !important;}.uib-position-body-scrollbar-measure{overflow:scroll !important;}</style><style type="text/css">.uib-datepicker .uib-title{width:100%;}.uib-day button,.uib-month button,.uib-year button{min-width:100%;}.uib-left,.uib-right{width:100%}</style><style type="text/css">.ng-animate.item:not(.left):not(.right){-webkit-transition:0s ease-in-out left;transition:0s ease-in-out left}</style><style type="text/css">@charset "UTF-8";[ng\:cloak],[ng-cloak],[data-ng-cloak],[x-ng-cloak],.ng-cloak,.x-ng-cloak,.ng-hide:not(.ng-hide-animate){display:none !important;}ng\:form{display:block;}.ng-animate-shim{visibility:hidden;}.ng-anchor{position:absolute;}</style>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title translate="TOP_LOGO_TITLE" class="ng-scope">AEP-使能平台</title>
    <link href="./src/images/favicon.ico" rel="shortcut icon" type="image/x-icon">
    <link rel="stylesheet" type="text/css" href="./src/libs/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" type="text/css" href="./src/libs/font-ali/iconfont.css">
    <link rel="stylesheet" type="text/css" href="./src/libs/fullpage/jquery.fullpage.css">
    <!--offline libs  start-->
 <!--   <link rel="stylesheet" type="text/css" href="./src/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="./src/css/animate.min.css?v=83156cbc61">
    <link rel="stylesheet" type="text/css" href="./src/libs/angular-ui/ui-grid.min.css">
    <link rel="stylesheet" type="text/css" href="./src/libs/datetimepicker/jquery.datetimepicker.css">-->
    <!--offline libs  end-->

    <!--online libs  start-->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/animate.css@3.7.0/animate.min.css?v=83156cbc61" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/angular-ui-grid@4.0.7/ui-grid.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/jquery-datetimepicker@2.5.20/build/jquery.datetimepicker.min.css" rel="stylesheet">
    
    <!--online libs  end-->

    <!--Arcgis js lib css start-->
    <!--<link rel="stylesheet" type="text/css" href="./src/libs/arcgis/3.17compact/esri/css/esri.css">-->
    <!--<link rel="stylesheet" href="src/libs/arcgis_v317/esri/css/esri.css">-->
    <!--Arcgis js lib css end-->
    <link rel="stylesheet" type="text/css" href="./src/libs/angular-scroll/nanoscroller.css">
    <link rel="stylesheet" type="text/css" href="./src/css/darkLight/base.css?v=d9776cc13a">
</head>
<body>
<!-- uiView: aep --><div class="framework ng-scope" ng-class="{'framework-clear': $state.current.name === 'print'}" ui-view="aep" style=""><div class="aep-login-container-body ng-scope">
     <div class="nano has-scrollbar" always-visible="true"><div class="nano-content" ng-transclude="" tabindex="0" style="right: -17px;"> 
        <div class="login-header-line ng-scope"></div>
        <div class="login-header-box ng-scope">
            <!-- <div class="logo-box logo-box-sefon" ui-sref="portal"></div>
            <div class="logo-box logo-box-iron" ui-sref="portal"></div> -->
            <!-- <div class="logo-line"></div> -->
             <!-- <div class="logo-title">公共物联网平台</div>  -->
            <!-- <div class="logo-portal" ui-sref="portal">首页</div> -->
            <div class="logo-box-name">彭州市智能井盖管理平台</div>
        </div>
        <div class="login-container-middle ng-scope">
            <div class="container-middle-center">
                <div class="middle-left">
                    <h2 class="sefon-info animated slideInLeft delay-02s">智能井盖管理平台 震撼上线！</h2>
                    <!-- <h2 class="china-iron-info animated slideInLeft delay-02s">中国铁塔公共物联网平台 震撼上线！</h2> -->
                    <p class="animated slideInLeft delay-04s">轻松安全地连接设备</p>
                    <p class="animated slideInLeft delay-06s">快速开发物联网应用</p>
                    <p class="animated slideInLeft delay-08s">在物联网大数据中挖掘价值</p>
                </div>
                <div class="middle-right animated slideInRight delay-02s">
                    <div class="login-box">
                        <div class="login-top-line"></div>
                        <!-- <div class="login-box-icon"></div> -->
                        <div class="login-box-name">登&nbsp;&nbsp;&nbsp;&nbsp;录</div>
                        <form name="loginForm" class="form-horizontal ng-pristine ng-invalid ng-invalid-required" novalidate="">
                            <!-- ngIf: Validation===false --> 
                            <div class="form-group-login">
                                <div class="login-input-icon icon-user"></div>
                                <input type="text" class="form-control login-user ng-pristine ng-untouched ng-empty ng-invalid ng-invalid-required" name="account" ng-model="model.account" ng-blur="checkUserExit()" placeholder="请输入用户名称" required="">
                            </div>
                            <div class="form-group-login">
                                <div class="login-input-icon icon-password"></div>
                                <input type="password" class="form-control login-password ng-pristine ng-untouched ng-empty ng-invalid ng-invalid-required" name="account" ng-model="model.password" placeholder="请输入登录密码" required="">
                            </div> 
                            <div class="form-group-login">
                                <button ng-click="login()" class="btn btn-com btn-block login-submit animated faster" ng-class="{'shake': !Validation}">登 录</button>
                            </div>
                            <div class="form-group-login">
                                <a class="login-span-click register-span-click" ui-sref="register()" href="#/register">立即注册</a>
                                <a class="login-span-click" ui-sref="forgetPassword()" href="#/forgetPassword">忘记密码</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="footer ng-scope">
            <h4>专业技术咨询&nbsp;&nbsp;&nbsp;&nbsp;全方位产品解读&nbsp;&nbsp;&nbsp;&nbsp;成功客户案例分享</h4>
            <ul>
                <li><a href="">首页</a></li>
                <li><a href="">关于四方</a></li>
                <li><a href="">联系我们</a></li>
                <li><a href="">合作伙伴</a></li>
                <li><a href="">产品咨询</a></li>
                <li><a href="">友情链接</a></li>
                <li><a href="">售后服务</a></li>
                <li><a href="">技术论坛</a></li>
                <li><a href="">管理团队</a></li>
                <li><a href="">加入我们</a></li>
            </ul>
            <p translate="FOOTER_COPYRIGHT" class="ng-scope">Copyright © 1993-2019 成都四方信息技术有限公司 | 四川省成都市高新区高朋大道11号22C座 | 版权所有 | 蜀ICP备12022557号-4</p>
        </div>
    </div><div class="nano-pane" style="opacity: 1; visibility: visible;"><div class="nano-slider" style="height: 23px; transform: translate(0px, 0px);"></div></div></div> 
</div></div>

<!--load outside libs start-->
<!--Arcgis js libs start-->
<!--<script defer type="text/javascript" src="https://js.arcgis.com/3.17compact"></script>-->
<!--<script defer type="text/javascript" src="./src/libs/arcgis/3.17compact/init.js"></script>-->
<!--Arcgis js libs  end-->

<!--offline libs  start-->

<!-- <script type="text/javascript" src="./src/libs/jquery/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-route.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-animate.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-cookies.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-messages.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-locale_zh.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-clipboard.js"></script>
<script type="text/javascript" src="./src/libs/angular-ui/angular-ui-router.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-ui/ui-bootstrap-tpls-2.5.0.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-ui/ui-grid.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-translate/angular-translate.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-translate/angular-translate-loader-static-files.min.js"></script>
<script type="text/javascript" src="./src/libs/lodash/lodash.min.js"></script>
<script type="text/javascript" src="./src/libs/md5/spark-md5.min.js"></script>
<script type="text/javascript" src="./src/libs/datetimepicker/jquery.datetimepicker.full.min.js"></script>
<script type="text/javascript" src="./src/libs/baidu/echarts.min.js"></script>  -->

<!--offline libs  end-->

<!--online libs  start-->

<script src="https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular@1.5.9/angular.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-route@1.5.9/angular-route.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-animate@1.5.9/angular-animate.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-cookies@1.5.9/angular-cookies.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-messages@1.5.9/angular-messages.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-i18n@1.5.9/angular-locale_zh.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-clipboard@1.6.2/angular-clipboard.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-ui-router@0.4.3/release/angular-ui-router.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-ui-bootstrap@2.5.0/dist/ui-bootstrap-tpls.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-ui-grid@4.0.7/ui-grid.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/ui-grid-custom-cell-select@0.1.2/js/custom-cell-select.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-translate@2.16.0/dist/angular-translate.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/angular-translate-loader-static-files@2.16.0/angular-translate-loader-static-files.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/lodash@4.17.10/lodash.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/spark-md5@3.0.0/spark-md5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jquery-datetimepicker@2.5.20/build/jquery.datetimepicker.full.min.js"></script>
<!-- <script src="https://cdn.jsdelivr.net/npm/echarts@3.8.5/dist/echarts.min.js"></script> -->
<script src="https://cdn.bootcss.com/echarts/4.3.0-rc.1/echarts.min.js"></script>
<script src="https://cdn.bootcss.com/echarts/4.3.0-rc.1/extension/bmap.min.js"></script>
<!-- <script src="https://cdn.jsdelivr.net/npm/echarts@4.6.0/dist/echarts.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/echarts@4.6.0/dist/extension/bmap.min.js"></script> -->


<!--online libs  end-->

<script type="text/javascript" src="./src/libs/angular-ui/ui-switch.min.js"></script>
<script type="text/javascript" src="./src/libs/angular/angular-tree-control.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-scroll/jquery.nanoscroller.min.js"></script>
<script type="text/javascript" src="./src/libs/angular-scroll/scrollable.js"></script>
<script type="text/javascript" src="./src/libs/angular-qrcode/qrcode.js"></script>
<script type="text/javascript" src="./src/libs/angular-qrcode/qrcode_UTF8.js"></script>
<script type="text/javascript" src="./src/libs/angular-qrcode/angular-qrcode.js"></script>
<script type="text/javascript" src="./src/libs/fullpage/jquery.fullpage.min.js"></script>
<script type="text/javascript" src="./src/libs/fullpage/angular-fullpage.min.js"></script>
<script type="text/javascript" src="./src/libs/image-compress/lrz.all.bundle.js"></script>
<!--<script type="text/javascript" src="./src/libs/socket.io/socket.io.js"></script>-->
<script type="text/javascript" src="./src/libs/rabbitmq/sockjs-0.3.js"></script>
<script type="text/javascript" src="./src/libs/rabbitmq/stomp.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=3.0&amp;ak=x9mIiLLIl9qYijcZXVXrpWrMsDKrW4fY"></script><script type="text/javascript" src="http://api.map.baidu.com/getscript?v=3.0&amp;ak=x9mIiLLIl9qYijcZXVXrpWrMsDKrW4fY&amp;services=&amp;t=20200415105918"></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/Heatmap/2.0/src/Heatmap_min.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
<!--<script type="text/javascript" src="http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js"></script>-->
<!--<script type="text/javascript" src="./src/libs/baidu/TextIconOverlay_min.js"></script>-->
<script type="text/javascript" src="./src/libs/baidu/MarkerClusterer_min.js"></script>
<script type="text/javascript" src="./src/libs/baidu/AreaRestriction_min.js"></script>
<!--load outside libs end-->
<!--load bundle start-->
<!--<script type="text/javascript"  defer src="./js/template.tpl.min.js"></script>-->
<script type="text/javascript" defer="" src="./js/bundle.min.js?v=22715a8922"></script>
<!--load bundle end-->


</body>
</html>