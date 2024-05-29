package project.web.admin.controller.report;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import kernel.util.Arith;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.web.admin.service.report.AdminUserAllStatisticsService;
import project.web.admin.service.user.AdminAgentService;

/**
 * 交易所_用户收益报表
 */
@RestController
public class AdminUserAllStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminUserAllStatisticsController.class);

	@Autowired
	protected AdminUserAllStatisticsService adminUserAllStatisticsService;
	@Autowired
	protected AdminAgentService adminAgentService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected PartyService partyService;

	@Resource
	protected UserRecomService userRecomService;
	@Autowired
	protected SysparaService sysparaService;

	@Autowired
	protected RedisHandler redisHandler;

	private final String action = "normal/adminUserAllStatisticsAction!";

	/**
	 * 获取 用户收益报表 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		boolean no_agent_recom = Boolean.valueOf(request.getParameter("no_agent_recom")).booleanValue();
		String para_rolename = request.getParameter("para_rolename");
		boolean para_agent_view = Boolean.valueOf(request.getParameter("para_agent_view")).booleanValue();
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");
		String para_party_id = request.getParameter("para_party_id");
		String sort_column = request.getParameter("sort_column");
		String sort_type = request.getParameter("sort_type");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistics_user_all_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			this.pageSize = 30;
			
			if (StringUtils.isEmptyString(start_time) && StringUtils.isEmptyString(end_time)) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}
			
			para_rolename = null;
			para_agent_view = false;

			if (no_agent_recom) {
				this.page = this.adminUserAllStatisticsService.pagedQueryNoAgentParent(this.pageNo, this.pageSize, start_time, end_time,
						this.getLoginPartyId(), para_username, para_rolename, para_party_id, para_agent_view, sort_column, sort_type);
			} else {				
				this.page = this.adminUserAllStatisticsService.pagedQuery(this.pageNo, this.pageSize, start_time, end_time,
						this.getLoginPartyId(), para_username, para_rolename, para_party_id, para_agent_view, sort_column, sort_type);				
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}
		
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);		
		modelAndView.addObject("no_agent_recom", no_agent_recom);
		modelAndView.addObject("para_rolename", para_rolename);
		modelAndView.addObject("para_agent_view", para_agent_view);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("para_username", para_username);
		modelAndView.addObject("para_party_id", para_party_id);
		modelAndView.addObject("sort_column", sort_column);
		modelAndView.addObject("sort_type", sort_type);
		return modelAndView;
	}

	/**
	 * 获取 用户收益报表 列表
	 */
	@RequestMapping(action + "exchangeList.action")
	public ModelAndView exchangeList(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String para_rolename = request.getParameter("para_rolename");
		boolean para_agent_view = Boolean.valueOf(request.getParameter("para_agent_view")).booleanValue();
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");
		String para_party_id = request.getParameter("para_party_id");
		String all_para_party_id = request.getParameter("all_para_party_id");
		String sort_column = request.getParameter("sort_column");
		String sort_type = request.getParameter("sort_type");
		String sellerId = request.getParameter("sellerId");
		String sellerName = request.getParameter("sellerName");
		String agentUserCode = request.getParameter("agentUserCode");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistics_user_all_list");
		String loginPartyId = getLoginPartyId();

		try {
			
			this.checkAndSetPageNo(pageNo);
			this.pageSize = 30;

			if (StringUtils.isEmptyString(start_time) && StringUtils.isEmptyString(end_time)) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}
			
			para_rolename = null;
			para_agent_view = false;
			String agentPartyId = null;
			String checkedPartyId = this.getLoginPartyId();
			if (StringUtils.isNotEmpty(checkedPartyId)) {
				agentPartyId = checkedPartyId;
			}
			if (StringUtils.isNullOrEmpty(checkedPartyId) && StringUtils.isNotEmpty(agentUserCode)){
				Party agentParty = partyService.findPartyByUsercode(agentUserCode);
				if (!Objects.isNull(agentParty)){
					agentPartyId = agentParty.getId().toString();
				} else {
					agentPartyId = "0";
				}
			}

			this.page = adminUserAllStatisticsService.exchangePagedQuery(this.pageNo, this.pageSize, start_time, end_time, agentPartyId,
					para_username, para_rolename, para_party_id, para_agent_view, sort_column, sort_type, sellerId, sellerName,all_para_party_id);
			List<Map> list = page.getElements();

			List<String> sellerIds = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {
				Map map=list.get(i);

				int recoNum = 0;
				int allNum = 0;

				List<String> children = this.userRecomService.findDirectlyChildrens(map.get("partyId").toString());
				List<String> childrenAll = this.userRecomService.findChildren(map.get("partyId").toString());
				Double recharge_usdt = (Double) map.get("recharge_usdt");
				Double withdraw = (Double) map.get("withdraw");
//				Long reco_num = (Long) map.get("reco_num") > 3 ? 3 : (Long) map.get("reco_num");
				double difference = Arith.sub(recharge_usdt, withdraw);
				map.put("difference",difference);

				Double rechargeCommission = (Double) map.get("rechargeCommission");
				Double withdrawCommission = (Double) map.get("withdrawCommission");
				map.put("commission", Arith.sub(rechargeCommission,withdrawCommission));

				for (String child : children) {
					Party party = partyService.cachePartyBy(child, false);
					if(null == party){
						logger.info("party 为null id为"+child);
					}
				 	if(Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
						recoNum++;
					}

				}

				for (String child : childrenAll) {
					Party party = partyService.cachePartyBy(child, false);
					if(null == party){
						logger.info("party 为null id为"+child);
					}
					if(Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
						allNum++;
					}
				}
				map.put("reco_num",recoNum);

				map.put("reco_all_num",allNum);

				Party agentParty = userRecomService.getAgentParty((Serializable) map.get("partyId"));
				if (null != agentParty){
					map.put("agentName",agentParty.getUsername());
					map.put("agentCode",agentParty.getUsercode());
				}
				String clerkOpen = this.sysparaService.find(SysParaCode.CLERK_IS_OPEN.getCode()).getValue();
				modelAndView.addObject("isOpen", clerkOpen);
				sellerIds.add(map.get("partyId").toString());

				String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + map.get("partyId").toString());
				if ("1".equals(isBlack)){
					map.put("blank",1);
				} else {
					map.put("blank",0);
				}
			}

			Map<String, Object> willIncomeBySellerIds = adminUserAllStatisticsService.queryWillIncomeBySellerIds(sellerIds,start_time ,end_time);

			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				String querySellerId = (String) map.get("sellerId");
				Object aLong = willIncomeBySellerIds.get(querySellerId);
				map.put("willIncome" , Objects.isNull(aLong) ? 0 : aLong);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);		
		modelAndView.addObject("para_rolename", para_rolename);
		modelAndView.addObject("para_agent_view", para_agent_view);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("para_username", para_username);
		modelAndView.addObject("para_party_id", para_party_id);
		modelAndView.addObject("all_para_party_id", all_para_party_id);
		modelAndView.addObject("sort_column", sort_column);
		modelAndView.addObject("sort_type", sort_type);
		modelAndView.addObject("sellerId", sellerId);
		modelAndView.addObject("sellerName", sellerName);
		modelAndView.addObject("agentUserCode", agentUserCode);
		modelAndView.addObject("loginPartyId", loginPartyId);
		return modelAndView;
	}
	
	
	/**
	 * 导出数据到文件
	 */
	@RequestMapping(action + "exportData.action")
	public ModelAndView exportData(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String para_rolename = request.getParameter("para_rolename");
		boolean para_agent_view = Boolean.valueOf(request.getParameter("para_agent_view")).booleanValue();
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");
		String para_party_id = request.getParameter("para_party_id");
		String sort_column = request.getParameter("sort_column");
		String sort_type = request.getParameter("sort_type");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistics_user_all_list");

		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 30;

//			initTime();
			if (StringUtils.isEmptyString(start_time) && StringUtils.isEmptyString(end_time)) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}
			
			String error = this.adminUserAllStatisticsService.loadExportData(this.getResponse(), this.pageSize, start_time,
					end_time, this.getLoginPartyId(), para_username, para_rolename, para_party_id, para_agent_view,
					sort_column, sort_type);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (IOException e) {
			logger.error("export fail:{}", e);
			modelAndView.addObject("error", "程序错误,导出异常");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		return modelAndView;
	}

	/**
	 * 用户钱包 登录者只能看自己下面的用户钱包
	 */
	@RequestMapping(action + "walletExtendsAll.action")
	public ModelAndView walletExtendsAll(HttpServletRequest request) {
		String para_wallet_party_id = request.getParameter("para_wallet_party_id");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistics_user_all_money");

		try {
			
			List<Map<String, Object>> wallet_data = this.adminUserAllStatisticsService.getWalletExtends(this.getLoginPartyId(), para_wallet_party_id);

			modelAndView.addObject("wallet_data", wallet_data);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		return modelAndView;
	}

	/**
	 * 用户资产 登录者只能看自己下面的用户资产
	 */
	@RequestMapping(action + "assetsAll.action")
	public ModelAndView assetsAll(HttpServletRequest request) {
		String para_wallet_party_id = request.getParameter("para_wallet_party_id");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			List<Map<String, Object>> asset_data = adminUserAllStatisticsService.getAssetsAll(this.getLoginPartyId(), para_wallet_party_id);

			modelAndView.addObject("asset_data", asset_data);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("statistics_user_all_asset");
		return modelAndView;
	}

//	protected void initTime() {
//		if (null == start_time && null == end_time) {
//			this.end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
//			this.start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
//		}
//	}

//	protected void roleMap() {
//		role_map.put(Constants.SECURITY_ROLE_MEMBER, "正式用户");
//		role_map.put(Constants.SECURITY_ROLE_AGENT, "代理商");
//		role_map.put(Constants.SECURITY_ROLE_AGENTLOW, "代理商");
//	}

}
