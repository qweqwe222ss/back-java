<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page language="java" import="security.*"%>
<%@ include file="include/basePath.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>log in</title>
    <style type="text/css">
        body {
            min-height: 100%;
            width: 100%;
            background-color:#e6e9ed;
            overflow: hidden;
        }
        .l-box1{     position: fixed;
            top: 50%;
            left: 50%;
            margin-left: -210px;
            margin-top: -146px;}
        .tit01{
            font-size: 26px;
            color: #1890ff;
            margin: 0 auto 40px auto;
            text-align: center;
            font-weight: 700;}
        .ip01{width:420px; margin:0 auto 30px auto; text-align:center;}
        .ip03{ width:420px; height:50px; line-height:50px; border:0px; font-size:16px; text-indent:45px;border-radius:5px; color: #000;}
        .ip-n{ background:url(image/n2.png) no-repeat 15px center #fff; background-size:25px;}
        .ip-p{ background:url(image/p2.png) no-repeat 15px center #fff; background-size:25px;}
        .bn01{ background:#1890ff; color:#fff; font:20px/30px Arial, Helvetica, sans-serif; padding:14px 0; width:420px; border:0; border-radius:5px;}
        .footer{height: 40px;
            line-height: 40px;
            position: fixed;
            bottom: 0;
            width: 100%;
            text-align: center;
            color: #fff;
            font-family: Arial;
            font-size: 12px;
            letter-spacing: 1px;
        }
        .h-t {
            color: #ff4949;
            font-size: 12px;
            line-height: 1.4;
            padding-top: 4px;
            text-align: left;
            display: none;
        }
    </style>
</head>

<body>
<div class="l-box1">
    <form action="<%=basePath%>public/login.action"  onsubmit="return toVaild()"
          class="ng-pristine ng-invalid ng-touched" method="post">
        <div class="tit01"></div>
        <div class="ip01">
            <input id="j_username" name="j_username" type="text" class="ip03 ip-n" placeholder="User name" />
            <div class="h-t ht-name">请输入您的账号</div>
        </div>
        <div class="ip01">
            <input id="j_password" name="j_password" type="password" class="ip03 ip-p" placeholder="Password" />
            <div class="h-t ht-pwd">请输入您的密码</div>
        </div>
        <div class="ip01">
            <input id="googleAuthCode" name="googleAuthCode" type="text" class="ip03 ip-p" placeholder="GoogleAuthCode"/>
            <div class="h-t ht-code">请输入谷歌验证码</div>

        </div>
        <div class="ip01"><input name="提交" type="submit" class="bn01" value="Log in"/></div>
    </form>
</div>
<div class="footer">Copyright © 2018-2021 malaifa All Rights Reserved.</div>
</body>
</html>
<input type="hidden" name="error" id="error" value="${error}" />

<script type="text/javascript" src="<%=basePath%>js/jquery.min.js"></script>
<script type='text/javascript'>

    //初始化执行一次
    setTimeout(function() {
        start();
    }, 100);

    function start() {
        var error = $("#error").val();
        if (null == error || "" == error) {
            $(".ht-code").hide();
        } else {
            $(".ht-code").show();
            $(".ht-code").html(error);
            return false;
        }
    }

    var userVal,passVal,codeVal

    function toVaild(){


        userVal = $('#j_username').val()
        passVal = $('#j_password').val()
        codeVal = $('#googleAuthCode').val()

        if(!userVal && !passVal && !codeVal){
            $(".ht-name").show();
            $(".ht-pwd").show();
            $(".ht-code").show();
            return false;
        }
        if(!userVal){
            $(".ht-name").show();
        }
        if(!passVal){
            $(".ht-pwd").show();
        }
        if(!codeVal){
            $(".ht-code").show();
        }

        if(userVal){
            $(".ht-name").hide();
        }
        if(passVal){
            $(".ht-pwd").hide();
        }
        if(codeVal){
            $(".ht-code").hide();
        }

        if(!userVal || !passVal || !codeVal){
            return false;
        }

    }

    function clickSbbmit(e){
        e.preventDefault();

        console.log(passVal,userVal)

    }
</script>

