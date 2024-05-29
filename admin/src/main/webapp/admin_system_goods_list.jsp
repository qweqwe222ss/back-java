<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>店铺商品</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>/mall/goods/sellerGoodsList.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<input type="hidden" name="messages" id="messages"
									value="${messages}">
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="goodName" name="goodName" class="form-control"
											placeholder="商品名称" value = "${goodName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="goodId" name="goodId" class="form-control"
											placeholder="商品ID" value = "${goodId}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
											placeholder="店铺名称" value = "${sellerName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="categoryId" name="categoryId"
														class="form-control ">
													<option value="0">一级分类</option>
													<c:forEach var = "item" items = "${categoryLevel1Map}">
														<option value="${item.key}" <c:if test="${categoryId == item.key}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="secondaryCategoryId" name="secondaryCategoryId"
														class="form-control ">
													<option value="0">二级分类</option>
													<c:forEach var = "item" items = "${categoryLevel2Map}">
														<option value="${item.key}" <c:if test="${secondaryCategoryId == item.key}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->


			<div class="row">
			
			
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
	
						<div class="panel-title">查询结果</div>
						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 || security.isResourceAccessible('OP_GOODS_OPERATE')}">
							<a href="javascript:shelfBatch(1)"
							   class="btn btn-light" style="margin-bottom: 12px"><i
									class="fa fa-pencil"></i>批量上架</a>
							<a href="javascript:shelfBatch(0)"
							   class="btn btn-light" style="margin-bottom: 12px"><i
									class="fa fa-pencil"></i>批量下架</a>
						</c:if>

						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>
											<input id="selAll" type="checkbox" />
										</td>
										<td>商品ID</td>
										<td>商品名称</td>
										<td>所属店铺</td>
										<td>商品分类</td>
										<td>进货价格</td>
										<td>销售价格</td>
										<td>是否新品</td>
										<td>是否推荐</td>
										<td>是否热销</td>
										<td>是否上架</td>
										<td>销量</td>
<%--										<td>最小购买数量</td>--%>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>
										<tr>
<%--											<td>--%>
<%--												<img width="90px" height="90px" id="show_img" src="<%=basePath%>normal/showImg.action?imagePath=${item.iconImg}"/> 　　--%>
<%--											</td>--%>
										<td>
											<input name="checkbox" type="checkbox" value="${item.id}">
										</td>
									    <td>${item.goodsId}</td>
<%--									    <td>${item.name}</td>--%>
										<td style="width: 160px;">
											<div style="width:120px; margin-left: 20px; text-align: center; overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"  title="${item.name}">

													${item.name}
											</div>
										</td>
									    <td>${item.sellerName}</td>
										<td>${item.PName}</td>
										<td>${item.systemPrice}</td>
										<td>${item.sellingPrice}</td>
										<td>
											<c:choose>
												<c:when test="${item.systemNewTime == '0'}">
													<span class="right label label-danger">非新品</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success">新品</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.systemRecTime == '0'}">
													<span class="right label label-danger">不推荐</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success">推荐</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.sellWellTime == '0'}">
													<span class="right label label-danger">非热销</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success">热销</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.isShelf == '0'}">
													<span class="right label label-danger">否</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success">是</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>${item.soldNum}</td>
<%--										<td>${item.buyMin}</td>--%>
											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_GOODS_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>/mall/goods/evaluation.action?&sellerGoodsId=${item.id}&sellerId=${item.sellerId}">商品评论</a></li>
															<c:choose>
																<c:when test="${item.systemNewTime == '0'}">
																	<li>	<a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=1&type=2&pageNo=${pageNo}">新品</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=0&type=2&pageNo=${pageNo}">非新品</a></li>
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${item.systemRecTime == '0'}">
																	<li><a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=1&type=1&pageNo=${pageNo}">推荐</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=0&type=1&pageNo=${pageNo}">不推荐</a></li>
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${item.sellWellTime == '0'}">
																	<li><a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=1&type=3&pageNo=${pageNo}">热销推荐</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>/mall/goods/updateStatus.action?id=${item.id}&status=0&type=3&pageNo=${pageNo}">非热销</a></li>
																</c:otherwise>
															</c:choose>
															<li><a href="<%=basePath%>/mall/comment//toAdd.action?&sellerGoodsId=${item.id}&sellerId=${item.sellerId}&pageNo=${pageNo}">进店评价</a></li>
<%--															<li><a href="javascript:onsucceeded('${item.id}')">设置商品最小购买数</a></li>--%>
														</ul>
													</div>
												</c:if>
											</td>
											
										</tr>
										
									</c:forEach>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		
		<!-- 模态框 -->
		<div class="form-group">
			<form action=""
					method="post" id="mainform">
				<input type="hidden" name="pageNo" id="pageNo"
					   value="${pageNo}">
				<input type="hidden" name="id" id="id"/>
				<div class="col-sm-1 form-horizontal">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_succeeded" tabindex="-1"
						 role="dialog" aria-labelledby="myModalLabel"
						 aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content" >
								<div class="modal-header">
									<button type="button" class="close"
											data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">确认调整</h4>
								</div>
								<div class="modal-body">
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												   class="login_safeword" placeholder="请输入登录人资金密码" >
										</div>
									</div>
									<!-- <div class="form-group" style="">

                                        <label for="input002" class="col-sm-3 control-label form-label">验证码</label>
                                        <div class="col-sm-4">
                                            <input id="email_code" type="text" name="email_code"
                                            class="login_safeword" placeholder="请输入验证码" >
                                        </div>
                                        <div class="col-sm-4">
                                            <a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                        </div>
                                    </div> -->
<%--									<div class="form-group" >--%>
<%--										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
<%--										<div class="col-sm-4">--%>
<%--											<input id="google_auth_code"  name="google_auth_code"--%>
<%--												   placeholder="请输入谷歌验证码" >--%>
<%--										</div>--%>
<%--									</div>--%>
								</div>
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn "
											data-dismiss="modal">关闭</button>
									<button id="sub" type="submit"
											class="btn btn-default">确认</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
			</form>
		</div>

		<!-- 模态框 -->
		<div class="form-group">

			<form action="<%=basePath%>/mall/goods/buyMinUpdate.action"
				  method="post" id="succeededForm">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="sellerGoodsId" id="sellerGoodsId" value="${sellerGoodsId}">

				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set" tabindex="-1"
						 role="dialog" aria-labelledby="myModalLabel"
						 aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">

								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">请输入最小购买数量</h4>
								</div>

								<div class="modal-body">
									<div class="">
										<input id="buyMin"  name="buyMin"
											   class="form-control" placeholder="请输入最小购买数量" oninput="value=value.replace(/[^\d]/g,'')">
									</div>
								</div>

								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default">确认</button>
								</div>

							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>

			</form>

		</div>
		
		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	<script>
	</script>


	<script type="text/javascript">
		<%--setTimeout(function() {--%>
		<%--	start();--%>
		<%--}, 100);--%>

		<%--function start(){--%>
		<%--	var img = $("#iconImg").val();--%>
		<%--	var show_img = document.getElementById('show_img');--%>
		<%--	show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;--%>
		<%--}--%>

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");

			$('#modal_succeeded').modal("show");

		}

		function addFakeComment(sellerId,creditScore){
			$("#sellerId1").val(sellerId);
			$("#NowCreditScore").val(creditScore);
			$('#modal_set2').modal("show");
		}


		function onsucceeded(id) {
			// var session_token = $("#session_token").val();
			// $("#session_token_success").val(session_token);
			$("#sellerGoodsId").val(id);
			$('#modal_set').modal("show");
		}

		$(function() {
			$('#startTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});
			$('#endTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});

		});

		function shelfBatch(isShelf) {
			let $checkbox = $(":checkbox:checked");
			if ($checkbox.length === 0) {
				return;
			}
			let num = $checkbox.length;
			let arr = new Array($checkbox.length);
			for (let i = 0; i < arr.length; i++) {
				arr[i] = $($checkbox[i]).val();
				if (arr[i] == "on"){
					num = $checkbox.length - 1;
				}
			}

			if (isShelf === 1){
				swal({
					title : "确定 批量上架" + num + "个商品吗",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					let arr = new Array($checkbox.length);
					for (let i = 0; i < $checkbox.length; i++) {
						arr[i] = $($checkbox[i]).val();
					}
					$.ajax({
						url: "<%=basePath%>/mall/goods/shelfBatch.action",
						type: 'POST',
						// contentType: "application/json",
						traditional: true,
						data: {
							'ids': arr,
							'isShelf': isShelf
						},
						success: function (data) {
							if (data.code === 200) {
								let queryForm = document.getElementById("queryForm");
								document.getElementById("messages").value = data.message;
								queryForm.submit();
								return;
							}
							if (data.code == 500){
								swal({
									title: data.error,
									timer: 2000,
									showConfirmButton: false
								})

							}

						}
					});
				});
			} else if (isShelf === 0){
				swal({
					title : "确定 批量下架" + num + "个商品吗",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					let arr = new Array($checkbox.length);
					for (let i = 0; i < $checkbox.length; i++) {
						arr[i] = $($checkbox[i]).val();
					}
					$.ajax({
						url: "<%=basePath%>/mall/goods/shelfBatch.action",
						type: 'POST',
						// contentType: "application/json",
						traditional: true,
						data: {
							'ids': arr,
							'isShelf': isShelf
						},
						success: function (data) {
							if (data.code === 200) {
								let queryForm = document.getElementById("queryForm");
								document.getElementById("messages").value = data.message;
								queryForm.submit();
								return;
							}
							if (data.code == 500){
								swal({
									title: data.error,
									timer: 2000,
									showConfirmButton: false
								})

							}

						}
					});
				});
			}

		}

		$("#selAll").on("click", function(){
			var che=$("#selAll").prop('checked');
			if(che){
				$("input[name='checkbox']").prop('checked',true);
			} else {
				$("input[name='checkbox']").prop('checked',false);
			}
		})


		$("input[name='checkbox']").on("click", function(){
			var setFalse=false;// 默认不给全选按钮设置false
			$.each($("input[name='checkbox']"),function(index,item){
				// 如果在普通多选框的循环中发现有false,就需要将全选按钮设置为false
				if(item.checked==false){
					setFalse=true;
				}
			})
			if(setFalse){
				$("#selAll").prop('checked',false);
			} else {// 如果普通按钮都为true, 则全选按钮也赋值为true
				$("#selAll").prop('checked',true);
			}
		})

	</script>
</body>
</html>