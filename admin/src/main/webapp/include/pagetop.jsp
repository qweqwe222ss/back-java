<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page contentType="text/html; charset=UTF-8" import="java.util.ResourceBundle" %>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String base = "http://" + request.getServerName() + ":"+request.getServerPort()+"/";
	String bases = "https://"+request.getServerName()+path+"/";


	ResourceBundle res = ResourceBundle.getBundle("config");
	String adminUrl = res.getString("backstage_url");
	String dmUrl = res.getString("dm_url");


%>

