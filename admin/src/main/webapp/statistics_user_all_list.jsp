<%@ page language="java" pageEncoding="utf-8" isELIgnored="false" deferredSyntaxAllowedAsLiteral="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>

	<style>
		td {
			word-wrap: break-word; /* 让内容自动换行 */
			max-width: 200px; /* 设置最大宽度，以防止内容过长 */
		}
	</style>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>用户收益报表</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action"
								method="post" id="queryForm">
								<input type="hidden" name="status_para" />
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
								<input type="hidden" name="para_time" id="para_time" value="${para_time}"> 
								<input type="hidden" name="para_party_id" id="para_party_id" value="${para_party_id}"> 
								<input type="hidden" name="all_para_party_id" id="all_para_party_id" value="${all_para_party_id}">
								<input type="hidden" name="para_agent_view" id="para_agent_view" value="${para_agent_view}">
								<input type="hidden" name="no_agent_recom" id="no_agent_recom" value="${no_agent_recom}">
								
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="col-md-12 col-lg-2">
										<input id="para_username" name="para_username"
											class="form-control " placeholder="用户名、UID" value="${para_username}" />
									</div>
									<div class="col-md-12 col-lg-2">
										<input id="sellerName" name="sellerName"
											   class="form-control " placeholder="店铺名称" value="${sellerName}" />
									</div>
									<c:if test="${loginPartyId == ''}">
										<div class="col-md-12 col-lg-2">
											<input id="agentUserCode" name="agentUserCode"
												   class="form-control " placeholder="代理ID" value="${agentUserCode}" />
										</div>
									</c:if>
									<div class="col-md-12 col-lg-2">
										<input id="start_time" name="start_time"
											class="form-control " placeholder="开始日期" value="${start_time}" />
									</div>
									<div class="col-md-12 col-lg-2">
										<input id="end_time" name="end_time"
											class="form-control " placeholder="结束日期" value="${end_time}" />
									</div>

									<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									</div>
									<div class="col-md-12 col-lg-3">
										<div class="checkbox checkbox-primary">
											<input id="no_agent_recom_checkbox" name="no_agent_recom_checkbox" type="checkbox" onClick="checkView()" value="${no_agent_recom_checkbox}" > 
											<label for="no_agent_recom_checkbox"> 无上级代理商推广用户 </label>
										</div>
									</div>
																	 
									<div class="col-md-12 col-lg-2">
										<button type="button" class="btn btn-light btn-block"
											onClick="formSubmit()">查询</button>
									</div>
								</div>
								
							</form>
							
						</div>

					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="col-md-12 col-lg-12" style="margin-bottom: 10px;">
							<div class="mailbox clearfix">

								<c:if test="${security.isResourceAccessible('ADMIN_USER_ALL_STATISTICS_EXPORTDATA')}">
									<!-- <button type="button" onclick="exportData_confirm();"
														class="btn btn-light">查询结果导出excel文件</button> -->
								</c:if>
							</div>
						</div>
						
						<div class="panel-body">
						
							<table class="table table-bordered table-striped" border="1">
							
								<thead>
									<tr>
										<td colspan="9"
											style="text-align: center; vertical-align: middle;">用户</td>
										<td colspan="3" style="text-align: center;">充提</td>
										<c:choose>
											<c:when test="${isOpen == '1'}">
												<td colspan="6" style="text-align: center;">其他</td>
											</c:when>
											<c:otherwise>
												<td colspan="7" style="text-align: center;">其他</td>
											</c:otherwise>
										</c:choose>

									</tr>
									<tr style="text-align: center;">
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">用户名</td>
<%--										<td rowspan="2"--%>
<%--											style="text-align: center; vertical-align: middle;">店铺ID</td>--%>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">UID</td>

										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">店铺名称</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">账户类型</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">所属代理</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">代理ID</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">直属人数</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">团队人数</td>
										<td rowspan="2"
											style="text-align: center; vertical-align: middle;">余额</td>
										<td rowspan="2">充值</td>
										
										<td rowspan="2">提现</td>
										<td rowspan="2">充值差额</td>
										<td rowspan="2">赠送彩金</td>
										<c:if test="${isOpen == '1'}">
											<td rowspan="2">真实客损</td>
										</c:if>
										<td rowspan="2">店铺销售总额</td>
										<td rowspan="2">已发放佣金</td>
										<td rowspan="2">待发放金额</td>
										<td rowspan="2">用户备注</td>
									</tr>
								</thead>

								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>


											<td>
												<c:choose>
													<c:when test="${item.blank==1}">
														${item.username} <span style="color: red">（已拉黑）</span>
													</c:when>
													<c:otherwise>
														${item.username}
													</c:otherwise>
												</c:choose>

											</td>
<%--											<td>${item.sellerId}</td>--%>
											<td>${item.UID}</td>

											<td>${item.sellerName}</td>
											<td>
												<c:if test="${item.rolename == 'AGENT'}">
													<span class="right label label-primary">代理商</span>
												</c:if>
												<c:if test="${item.rolename == 'MEMBER'}">
													<span class="right label label-success">正式账号</span>
												</c:if>
												<c:if test="${item.rolename == 'TEST'}">
													<span class="right label label-default">试用账号</span>
												</c:if>
											</td>
											<td>${item.agentName}</td>
											<td>${item.agentCode}</td>
											<td><a href="#" onClick="getNextLvel('${item.partyId}')">${item.reco_num}</a></td>
											<td><a href="#" onClick="getAllNextLvel('${item.partyId}')">${item.reco_all_num}</a></td>
											<td>
													<fmt:formatNumber value="${item.money}" pattern="#0.0000" />
											</td>
											<td><fmt:formatNumber value="${item.recharge_usdt}" pattern="#0.0000" /></td>
<%--											<td>${item.recharge_eth}</td>--%>
<%--											<td>${item.recharge_btc}</td>--%>
											<td><fmt:formatNumber value="${item.withdraw}" pattern="#0.0000" /></td>
											<td>
												<c:if test="${item.difference < 0}">
												<span class="right label label-danger">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</span>
												</c:if>
												<c:if test="${item.difference > 0}">
												<span class="right label label-success">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</span>
												</c:if>
												<c:if test="${item.difference == 0}">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</c:if>
											</td>
												<td><fmt:formatNumber value="${item.gift_money}" pattern="#0.00" /></td>
											<c:if test="${isOpen == '1'}">
												<td>
<%--													<fmt:formatNumber value="${item.rechargeCommission}" pattern="#0.00" />/--%>
<%--													<fmt:formatNumber value="${item.withdrawCommission}" pattern="#0.00" />/--%>
													<fmt:formatNumber value="${item.commission}" pattern="#0.00" />

												</td>
											</c:if>
											<td><fmt:formatNumber value="${item.sellerTotalSales}" pattern="#0.00" /></td>
<%--											<td><fmt:formatNumber value="${item.translate}" pattern="#0.00" /></td>--%>
											<td><fmt:formatNumber value="${item.translate}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${item.willIncome}" pattern="#0.00" /></td>
											<td>${item.remarks}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	
	<div class="form-group">
		<input type="hidden" name="quote_currency" id="quote_currency" value="${quote_currency}">
		<!-- 			<input type="hidden" name="base_currency" id="base_currency" value=""> -->
		<%-- <form action="<%=basePath%>normal/adminSymbolsAction!list.action"
				method="post" id="succeededForm">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${param.pageNo}">
				<s:hidden name="id" id="id_reset"></s:hidden>
				<s:hidden name="name_para" id="name_para"></s:hidden>
				<s:hidden name="rolename_para" id="rolename_para"></s:hidden> --%>
		<div class="col-sm-2">
		
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height: 500px;">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">钱包</h4>
						</div>
						
						<%--<div class="modal-body">
							<div class="">
								<s:textfield id="money_revise" name="money_revise"
									cssClass="form-control " />
									<span  class="help-block">增加请输入正数，扣除请输入负数</span> 
							</div>
						 --%>
						 
						<div class="modal-body" id="wallet_get" style="height: 370px;">
							<%@ include file="statistics_user_all_money.jsp"%>
						</div>
						
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal">关闭</button>
							<!-- <button id="sub" type="submit" class="btn btn-default" onclick="modalConfirm(this)">确认</button> -->
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
			
		</div>
		<!-- </form> -->
	</div>

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		$.fn.datetimepicker.dates['zh'] = {
			days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
			daysShort : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
					"十月", "十一月", "十二月" ],
			monthsShort : [ "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
					"十一", "十二" ],
			meridiem : [ "上午", "下午" ],
			//suffix:      ["st", "nd", "rd", "th"],  
			today : "今天",
			clear : "清空"
		};

		$(function() {
			$('#start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$('#statistics_start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			})	       

			$('#end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$('#statistics_end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
// 				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2,
				endDate: new Date(new Date().getTime() - 24*60*60*1000)
			})
			
			$("#para_time").val("");
		});
	</script>

	<script type="text/javascript">
		function setTime(time){
    		document.getElementById("para_time").value=time;
    		document.getElementById("queryForm").submit();
		}
		function message(title){
			swal({
				title : title,
				text : "",
				type : "warning",
//					showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			});
		}
	</script>
	
	<form
		action="<%=basePath%>normal/adminUserAllStatisticsAction!exportData.action"
		method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<input type="hidden" name="end_time" value="${end_time}">
		<input type="hidden" name="start_time" value="${start_time}">
		<input type="hidden" name="para_time" value="${para_time}">
		<input type="hidden" name="para_rolename" value="${para_rolename}">
		<input type="hidden" name="para_username" value="${para_username}">
		<input type="hidden" name="para_party_id" value="${para_party_id}">
		<input type="hidden" name="para_agent_view" value="${para_agent_view}">
		<input type="hidden" name="no_agent_recom" value="${no_agent_recom}">
		<!-- <s:hidden name="end_time"></s:hidden>
		<s:hidden name="start_time"></s:hidden>
		<s:hidden name="para_time"></s:hidden>
		<s:hidden name="para_rolename"></s:hidden>
		<s:hidden name="para_username"></s:hidden>
		<s:hidden name="para_party_id"></s:hidden>
		<s:hidden name="para_agent_view"></s:hidden>
		<s:hidden name="no_agent_recom"></s:hidden> -->
		<%-- <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT,ROLE_ADMINGUEST,ROLE_FINANCE,ROLE_DEBUG">
		<s:hidden name="appuser_name_para" id="appuser_name_para"></s:hidden>
		</sec:authorize> --%>
	</form>
	
	<script type="text/javascript">
		function exportData_confirm() {
			swal({
				title : "确认导出订单数据到文件?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				 $('input[name="end_time"]').val($("#end_time").val());
				 $('input[name="start_time"]').val($("#start_time").val());
				 $('input[name="para_time"]').val($("#para_time").val());
				 $('input[name="para_rolename"]').val($("#para_rolename").val());
				 $('input[name="para_username"]').val($("#para_username").val());
				 $('input[name="para_party_id"]').val($("#para_party_id").val());
				 $('input[name="para_agent_view"]').val($("#para_agent_view").val());
				 $('input[name="no_agent_recom"]').val($("#no_agent_recom").val());
				document.getElementById("exportData").submit();				
			});	
		};
	</script>
	
	<script>
		$(function () {
		 if("true"==$("#para_agent_view").val()){
			 $("#para_agent_view_checkbox").prop("checked","checked");
		 }
		 if("true"==$("#no_agent_recom").val()){
			 $("#no_agent_recom_checkbox").prop("checked","checked");
		 }
		});
 		function getNextLvel(id){
 			$("#para_party_id").val(id);
 			$("#pageNo").val(1);
 			$('input[name="para_username"]').val('');
 			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
 			$("#no_agent_recom").val($("#no_agent_recom_checkbox").is(':checked'));
 			$("#queryForm").submit();
 		}

		function getAllNextLvel(id){
			$("#all_para_party_id").val(id);
			$("#pageNo").val(1);
			$('input[name="para_username"]').val('');
			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
			$("#no_agent_recom").val($("#no_agent_recom_checkbox").is(':checked'));
			$("#queryForm").submit();
		}
 		function checkView(){
 			$("#para_agent_view_checkbox").val($("#para_agent_view").is(':checked'));
 			$("#no_agent_recom_checkbox").val($("#no_agent_recom").is(':checked'));
 			formSubmit();
 		}
 		function formSubmit(){
 			$("#para_party_id").val("");
 			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
 			$("#no_agent_recom").val($("#no_agent_recom_checkbox").is(':checked'));
 			$("#queryForm").submit();
 		}       
 		function getAllMoney(id){
 			$("#modal_set").modal("show");
 			var data = {"para_wallet_party_id":id};
 			goAjaxUrl("<%=basePath%>normal/adminUserAllStatisticsAction!walletExtendsAll.action", data);
		}
		function goAjaxUrl(targetUrl, data) {
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				success : function(res) {
					// 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
					// 客户端会主观认为服务端返回的就是 JSON 格式的字符串
					//						    console.log(res)
					//					    $(".loading").hide();
					$("#wallet_get").html(res);
					// 				    $('#quote_currency').val(data.quote_currency);
					// 				    $('#base_currency').val(data.base_currency);

					// 				    if(null==data.quote_currency||''==data.quote_currency||typeof(data.quote_currency) == "undefined"){
					// 					    $('.tr_quote:first').attr('style','background:#39ffff;');
					// 				    }else{
					// 					    $('#tr_'+data.quote_currency).attr('style','background:#39ffff;');
					// 				    }
				}
			});
		}
	</script>
	
</body>

</html>
