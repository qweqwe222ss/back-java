<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
	.divider {
		margin: 4px 0;
		height: 1px;
		margin: 9px 0;
		overflow: hidden;
		background-color: #c8c1c1;
	}
</style>

<div class="sidebar clearfix">

	<ul class="sidebar-panel nav">

		<!-- dapp+交易所 菜单 ######################################################################################################## -->

		<%--        <c:choose>--%>
		<%--            <c:when test="${security.isRolesAccessible('ROLE_AGENT')}">--%>

		<%--                <li class="dropdown-parent">--%>
		<%--                    <a href="<%=basePath%>normal/adminUserAction!list.action">--%>
		<%--                        <span class="icon color6"><i class="fa fa-file-text-o"></i></span>--%>
		<%--                        <span class="sp-title">用户基础管理</span>--%>
		<%--                    </a>--%>
		<%--                </li>--%>

		<%--            </c:when>--%>
		<%--            <c:otherwise>--%>

		<li>
			<a href="<%=basePath%>normal/adminIndexAction!viewNew.action">
				<span class="icon color6"><i class="fa fa-home"></i></span>
				<span class="sp-title">综合查询</span>
			</a>
		</li>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')
                             || security.isResourceAccessible('OP_USER_CHECK')
                             || security.isResourceAccessible('OP_USER_OPERATE')}">

			<li>4
				<a href="<%=basePath%>normal/adminUserAction!list.action">
					<span class="icon color6"><i class="fa fa-file-text-o"></i></span>
					<span class="sp-title">用户管理</span>
				</a>
			</li>

		</c:if>

		<%--                <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--             || security.isResourceAccessible('OP_GOODS_CHECK')--%>
		<%--             || security.isResourceAccessible('OP_GOODS_OPERATE')}">--%>

		<li>
			<a href="<%=dmUrl%>/download/#/?url=<%=adminUrl%>">
				<span class="icon color6"><i class="fa falist fa-columns"></i></span>
				<span class="sp-title">pos下单</span>
			</a>
		</li>
		<li>
			<a href="<%=dmUrl%>/download/#/pos-history?url=<%=adminUrl%>">
				<span class="icon color6"><i class="fa falist fa-columns"></i></span>
				<span class="sp-title">pos日志记录</span>
			</a>
		</li>

		<%--                </c:if>--%>

		<%--            </c:otherwise>--%>
		<%--        </c:choose>--%>
		<li class="sidetitle">业务</li>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                             || security.isResourceAccessible('OP_CATEGORY_CHECK')
                             || security.isResourceAccessible('OP_CATEGORY_OPERATE')}">
			<li>
				<a href="<%=basePath%>/mall/category/list.action?level=0">
					<span class="icon color6"><i class="fa falist fa-laptop"></i></span>
					<span class="sp-title">商品分类</span>
				</a>
			</li>
		</c:if>


		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
             || security.isResourceAccessible('OP_MALL_GOODS_CHECK')
             || security.isResourceAccessible('OP_MALL_GOODS_OPERATE')}">

			<li>
				<a href="<%=dmUrl%>/download/#/commodity-library?url=<%=adminUrl%>">
						<%--                    <a href="<%=basePath%>/mall/goods/list.action">--%>
					<span class="icon color6"><i class="fa falist fa-columns"></i></span>
					<span class="sp-title">商品库</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
             || security.isResourceAccessible('OP_GOODATTRCATEGORY_CHECK')
             || security.isResourceAccessible('OP_GOODATTRCATEGORY_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/goodAttrCategory/list.action">
					<span class="icon color6"><i class="fa falist fa-columns"></i></span>
					<span class="sp-title">属性管理</span>
				</a>
			</li>

		</c:if>

		<%--        <li>--%>
		<%--            <a href="https://www.tkgomall.com//attribute/#/">--%>
		<%--                <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                <span class="sp-title">属性管理</span>--%>
		<%--            </a>--%>
		<%--        </li>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--             || security.isResourceAccessible('OP_MALL_GOODS_CHECK')--%>
		<%--             || security.isResourceAccessible('OP_MALL_GOODS_OPERATE')}">--%>

		<%--            <li>--%>
		<%--                <a href="<%=basePath%>/mall/comment/list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                    <span class="sp-title">评论库</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
             || security.isResourceAccessible('OP_GOODS_CHECK')
             || security.isResourceAccessible('OP_GOODS_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/goods/sellerGoodsList.action">
					<span class="icon color6"><i class="fa falist fa-columns"></i></span>
					<span class="sp-title">店铺商品</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
             || security.isResourceAccessible('OP_LOANCOFIG_CHECK')}">

			<li>
				<a href="<%=basePath%>/mall/loan/config/toUpdate.action">
					<span class="icon color6"><i class="fa falist fa-columns"></i></span>
					<span class="sp-title">借贷配置</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
             || security.isResourceAccessible('OP_CREDIT_CHECK')
             || security.isResourceAccessible('OP_CREDIT_OPERATE')}">

			<li>
				<a href="<%=basePath%>/credit/history.action">
					<span class="icon color6"><i class="fa falist fa-columns"></i></span>
					<span class="sp-title">借贷记录</span>
					<span class="credit_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                 || security.isResourceAccessible('OP_VIP_CHECK')}">

			<li>
				<a href="<%=basePath%>/brush/vip/list.action">
					<span class="icon color6"><i class="fa falist fa-outdent"></i></span>
					<span class="sp-title">卖家等级</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                 || security.isResourceAccessible('OP_MALL_ORDER_CHECK') || security.isResourceAccessible('OP_MALL_ORDER_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/order/list.action">
					<span class="icon color6"><i class="fa-laptop"></i></span>
					<span class="sp-title">订单列表</span>
					<span class="goods_order_waitdeliver_count badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                 || security.isResourceAccessible('OP_MALL_RORDER_CHECK') || security.isResourceAccessible('OP_MALL_RORDER_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/order/refundList.action">
					<span class="icon color6"><i class="fa-laptop"></i></span>
					<span class="sp-title">退货订单</span>
					<span class="goods_order_return_count badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 || security.isResourceAccessible('OP_USER_KYC_CHECK')
                                 || security.isResourceAccessible('OP_USER_KYC_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminKycAction!list.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">店铺审核</span>
					<span class="kyc_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 || security.isResourceAccessible('OP_MALL_SELLER_CHECK')
                                 || security.isResourceAccessible('OP_MALL_SELLER_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/seller/list.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">店铺管理</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 || security.isResourceAccessible('OP_COMBO_CHECK')
                                 || security.isResourceAccessible('OP_COMBO_OPERATE')}">

			<li>
				<a href="<%=basePath%>/mall/combo/list.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">店铺直通车管理</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 || security.isResourceAccessible('OP_COMBORECORD_CHECK')}">

			<li>
				<a href="<%=basePath%>/mall/combo/recordList.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">直通车购买记录</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 || security.isResourceAccessible('OP_CHAT_CHECK')
                                 || security.isResourceAccessible('OP_CHAT_OPERATE')}">

			<li>
				<a href="<%=basePath%>/chat/chatsList.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">虚拟买家对话</span>
					<span class="chat_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>
		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_PLATFORMCHAT_CHECK')
                                         || security.isResourceAccessible('OP_PLATFORMCHAT_OPERATE')}">
			<li>
				<a href="<%=basePath%>/platform/chatsList.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">系统客服对话</span>
				</a>
			</li>


		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_CHAT_AUDIT_CHECK')
                                         || security.isResourceAccessible('OP_CHAT_AUDIT_OPERATE')}">
			<li>
				<a href="<%=basePath%>/chat/auditList.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">买家对话审核</span>
					<span class="chat_mixed_unread_count badge label-danger" style="display: none">0</span>

				</a>
			</li>

		</c:if>


		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                         || security.isResourceAccessible('OP_GOODSBUY_CHECK') || security.isResourceAccessible('OP_GOODSBUY_OPERATE')}">--%>
		<%--                <li>--%>
		<%--                    <a href="<%=basePath%>/invest/goodsBuy/list.action">--%>
		<%--                        <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                        <span class="sp-title">实物兑换记录</span>--%>
		<%--                    </a>--%>
		<%--                </li>--%>
		<%--        </c:if>--%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                         || security.isResourceAccessible('OP_GOODSBUY_CHECK') || security.isResourceAccessible('OP_GOODSBUY_OPERATE')}">--%>
		<%--                <li>--%>
		<%--                    <a href="<%=basePath%>/invest/goodsBuy/point/exchange/list.action">--%>
		<%--                        <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                        <span class="sp-title">余额兑换记录</span>--%>
		<%--                    </a>--%>
		<%--                </li>--%>
		<%--        </c:if>--%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                 || security.isResourceAccessible('OP_DISPATCH_CHECK')}">--%>

		<%--            <li>--%>
		<%--                <a href="<%=basePath%>/adminOrder/dispatchList.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">手动派单</span>--%>
		<%--                    <span class="manual_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                     || security.isResourceListAccessible('OP_EXCHANGE_USER_CHECK,OP_EXCHANGE_USER_OPERATE,OP_MARKET_CHECK,OP_MARKET_OPERATE,OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE')}">

			<%--            <li class="divider"></li>--%>

			<li class="sidetitle">财务</li>

		</c:if>



		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')
                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminWithdrawAction!list.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">提现订单</span>
					<span class="withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')
                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action">
					<span class="icon color6"><i class="fa falist fa-credit-card"></i></span>
					<span class="sp-title">充值订单</span>
					<span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                             || security.isResourceAccessible('OP_EXCHANGEORDER_CHECK')--%>
		<%--                             || security.isResourceAccessible('OP_EXCHANGEORDER_OPERATE')}">--%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>/exchange/order/list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-credit-card"></i></span>--%>
		<%--                    <span class="sp-title">OTC交易订单</span>--%>
		<%--                    <span class="exchange_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>
		<%--        </c:if>--%>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                     || security.isResourceListAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK,OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK,OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">

			<li class="sidetitle">对账</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                            || security.isResourceAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>/brush/userMoney/list.action">
					<span class="icon color6"><i class="fa falist fa-pie-chart"></i></span>
					<span class="sp-title">用户存量</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                            || security.isResourceAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action">
					<span class="icon color6"><i class="fa falist fa-pie-chart"></i></span>
					<span class="sp-title">运营数据</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                            || security.isResourceAccessible('OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!list.action">
					<span class="icon color6"><i class="fa falist fa-sitemap"></i></span>
					<span class="sp-title">代理商充提报表</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                            || security.isResourceAccessible('OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action">
					<span class="icon color6"><i class="fa falist fa-align-left"></i></span>
					<span class="sp-title">用户报表</span>
				</a>
			</li>

		</c:if>



		<%--    <!-- 交易所 菜单 ############################################################################################################# -->--%>
		<%--    <c:if test="${security.isDappOrExchange()}">--%>
		<%--    --%>
		<%--        <c:choose>--%>
		<%--            <c:when test="${security.isRolesAccessible('ROLE_AGENT')}">--%>
		<%--            --%>
		<%--                <li class="dropdown-parent">--%>
		<%--                    <a href="<%=basePath%>normal/adminUserAction!list.action">--%>
		<%--                        <span class="icon color6"><i class="fa fa-file-text-o"></i></span>--%>
		<%--                        <span class="sp-title">用户基础管理</span>--%>
		<%--                    </a>--%>
		<%--                </li>--%>
		<%--                --%>
		<%--            </c:when>--%>
		<%--            <c:otherwise>--%>
		<%--                --%>
		<%--                <li>--%>
		<%--                    <a href="<%=basePath%>normal/adminIndexAction!view.action">--%>
		<%--                        <span class="icon color6"><i class="fa fa-home"></i></span>--%>
		<%--                        <span class="sp-title">综合查询</span>--%>
		<%--                    </a>--%>
		<%--                </li>               --%>
		<%--                    --%>
		<%--                <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')--%>
		<%--                             || security.isResourceAccessible('OP_USER_CHECK')--%>
		<%--                             || security.isResourceAccessible('OP_USER_OPERATE')}">--%>
		<%--                             --%>
		<%--                    <li>--%>
		<%--                        <a href="<%=basePath%>normal/adminUserAction!list.action">--%>
		<%--                            <span class="icon color6"><i class="fa fa-file-text-o"></i></span>--%>
		<%--                            <span class="sp-title">用户基础管理</span>--%>
		<%--                        </a>--%>
		<%--                    </li>--%>
		<%--        --%>
		<%--                </c:if>--%>
		<%--                --%>
		<%--            </c:otherwise>--%>
		<%--        </c:choose>--%>
		<%--                    --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_USER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_USER_OPERATE')}">--%>
		<%--                     --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/exchangeAdminUserAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">用户管理</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_MARKET_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_MARKET_OPERATE')}">--%>
		<%--        --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminMarketQuotationsManageAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-bar-chart"></i></span>--%>
		<%--                    <span class="sp-title">行情管理</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>
		<%--        --%>
		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                    || security.isResourceListAccessible('OP_FOREVER_CONTRACT_ORDER_CHECK,OP_FOREVER_CONTRACT_ORDER_OPERATE,OP_FOREVER_CONTRACT_APPLY_ORDER_CHECK,OP_FOREVER_CONTRACT_APPLY_ORDER_OPERATE,OP_FUTURES_CONTRACT_ORDER_CHECK,OP_FUTURES_CONTRACT_ORDER_OPERATE,OP_PROFIT_AND_LOSS_CONFIG_CHECK,OP_PROFIT_AND_LOSS_CONFIG_OPERATE')}">--%>
		<%--        --%>
		<%--            <li class="divider"></li>--%>
		<%--            --%>
		<%--            <li role="presentation" class="dropdown-header">合约</li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_FOREVER_CONTRACT_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_FOREVER_CONTRACT_ORDER_OPERATE')}">--%>
		<%--                --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminContractOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">永续持仓单</span>--%>
		<%--                    <span class="contract_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_FOREVER_CONTRACT_APPLY_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_FOREVER_CONTRACT_APPLY_ORDER_OPERATE')}">--%>
		<%--        --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminContractApplyOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">永续委托单</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_FUTURES_CONTRACT_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_FUTURES_CONTRACT_ORDER_OPERATE')}">--%>
		<%--        --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminFuturesOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">交割合约单</span>--%>
		<%--                    <span class="futures_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_PROFIT_AND_LOSS_CONFIG_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_PROFIT_AND_LOSS_CONFIG_OPERATE')}">--%>
		<%--                                    --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminProfitAndLossConfigAction!list.action"> --%>
		<%--                    <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                    <span class="sp-title">交割场控设置</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                    || security.isResourceListAccessible('OP_EXCHANGE_APPLY_ORDER_CHECK,OP_EXCHANGE_APPLY_ORDER_OPERATE')}">--%>
		<%--        --%>
		<%--            <li class="divider"></li>--%>
		<%--            --%>
		<%--            <li role="presentation" class="dropdown-header">币币</li>--%>

		<%--        </c:if>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_APPLY_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_APPLY_ORDER_OPERATE')}">--%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminExchangeApplyOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-file-text-o"></i></span>--%>
		<%--                    <span class="sp-title">币币交易单</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>
		<%--        </c:if>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                    || security.isResourceListAccessible('OP_FINANCE_ORDER_CHECK,OP_FINANCE_ORDER_OPERATE,OP_MINER_ORDER_CHECK,OP_MINER_ORDER_OPERATE')}">--%>
		<%--        --%>
		<%--            <li class="divider"></li>--%>
		<%--            --%>
		<%--            <li role="presentation" class="dropdown-header">财富</li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_FINANCE_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_FINANCE_ORDER_OPERATE')}">--%>
		<%--                --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminFinanceOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-folder"></i></span>--%>
		<%--                    <span class="sp-title">理财订单</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_MINER_ORDER_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_MINER_ORDER_OPERATE')}">--%>
		<%--                --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminMinerOrderAction!list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-anchor"></i></span>--%>
		<%--                    <span class="sp-title">矿机订单</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>     --%>
		<%--        --%>
		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceListAccessible('OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE')}">--%>
		<%--        --%>
		<%--            <li class="divider"></li>--%>
		<%--            --%>
		<%--            <li role="presentation" class="dropdown-header">财务</li>--%>
		<%--        --%>
		<%--        </c:if>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')}">--%>
		<%--        --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminWithdrawAction!list.action"> --%>
		<%--                    <span class="icon color6"><i class="fa falist fa-credit-card"></i></span>--%>
		<%--                    <span class="sp-title">提现订单</span>--%>
		<%--                    <span class="withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>
		<%--        --%>
		<%--        </c:if>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')--%>
		<%--                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">--%>
		<%--    --%>
		<%--            <li>--%>
		<%--                <a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action"> --%>
		<%--                    <span class="icon color6"><i class="fa falist fa-credit-card"></i></span>--%>
		<%--                    <span class="sp-title">充值订单</span>--%>
		<%--                    <span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none">0</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>
		<%--        --%>
		<%--        </c:if>--%>
		<%--            --%>
		<%--    </c:if>--%>

	</ul>

</div>