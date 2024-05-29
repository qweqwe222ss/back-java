<%@ page language="java" pageEncoding="utf-8"%>
<%-- <jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" /> --%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String base = "http://" + request.getServerName() + ":"+request.getServerPort()+"/";
    // String username = security.getUsername_login();
%>
