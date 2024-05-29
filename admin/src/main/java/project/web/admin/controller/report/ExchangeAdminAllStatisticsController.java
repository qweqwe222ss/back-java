package project.web.admin.controller.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
import project.syspara.SysParaCode;
import project.syspara.SysparaService;
import project.web.admin.service.report.AdminAllStatisticsService;

/**
 * 总充提报表
 */
@RestController
public class ExchangeAdminAllStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(ExchangeAdminAllStatisticsController.class);

	@Autowired
	private AdminAllStatisticsService adminAllStatisticsService;


	@Autowired
	protected SysparaService sysparaService;
	private final String action = "normal/exchangeAdminAllStatisticsAction!";

	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_time = request.getParameter("para_time");

		ModelAndView modelAndView = new ModelAndView();
				
		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			if (null == start_time && null == end_time && null == para_time) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}
			if ("day".equals(para_time)) {
				// 当天
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = end_time;
			} else if ("week".equals(para_time)) {
				// 往前推7天
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.addDate(new Date(), -7), DateUtils.DF_yyyyMMdd);
			} else if ("month".equals(para_time)) {
				// 往前推一月
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1), DateUtils.DF_yyyyMMdd);
			} else if ("all".equals(para_time)) {
				// 所有数据
				end_time = null;
				start_time = null;
			}
			String clerkOpen = this.sysparaService.find(SysParaCode.CLERK_IS_OPEN.getCode()).getValue();
			this.page = this.adminAllStatisticsService.pagedQuery(this.pageNo, this.pageSize, start_time, end_time,
					this.getLoginPartyId());

			List<Map> list = page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map=list.get(i);
				Double recharge_usdt = (Double) map.get("recharge_usdt");
				Double withdraw = (Double) map.get("withdraw");
				double difference = Arith.sub(recharge_usdt, withdraw);

				Double rechargeCommission = (Double) map.get("rechargeCommission");
				Double withdrawCommission = (Double) map.get("withdrawCommission");
				double commission = Arith.sub(rechargeCommission, withdrawCommission);
				map.put("difference",difference);
				map.put("commission",commission);
			}
			Map<String, Object> sumdata = this.adminAllStatisticsService.sumDatas(start_time, end_time, this.getLoginPartyId());
			if (!Objects.isNull(sumdata) && sumdata.size() >0){
				Double recharge_usdt = (Double)sumdata.get("recharge_usdt");
				Double withdraw = (Double) sumdata.get("withdraw");
				sumdata.put("difference", Arith.sub(recharge_usdt,withdraw));
				Double rechargeCommission = (Double) sumdata.get("rechargeCommission");
				Double withdrawCommission = (Double) sumdata.get("withdrawCommission");
				sumdata.put("commission", Arith.sub(rechargeCommission,withdrawCommission));
				sumdata.put("recharge_usdt", new BigDecimal(recharge_usdt).setScale(4, RoundingMode.FLOOR).toPlainString());
			} else {
				sumdata = new HashMap<>();
				sumdata.put("recharge_usdt", 0.0000);
				sumdata.put("difference", 0.0000);
				sumdata.put("commission", 0.0000);
				sumdata.put("recharge_usdt", 0.0000);
				sumdata.put("withdraw", 0.0000);
				sumdata.put("gift_money", 0.0000);
				sumdata.put("sellerTotalSales", 0.0000);
				sumdata.put("translate", 0.0000);
			}
			modelAndView.addObject("sumdata", sumdata);
			modelAndView.addObject("isOpen", clerkOpen);

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
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("para_time", para_time);
		modelAndView.setViewName("statistics_all_list");
		return modelAndView;
	}

	/**
	 * 导出订单数据到文件
	 */
	@RequestMapping(action + "exportData.action")
	public ModelAndView exportData(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_time = request.getParameter("para_time");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			if (null == start_time && null == end_time && null == para_time) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}
			if ("day".equals(para_time)) {
				// 当天
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = end_time;
			} else if ("week".equals(para_time)) {
				// 往前推7天
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.addDate(new Date(), -7), DateUtils.DF_yyyyMMdd);
			} else if ("month".equals(para_time)) {
				// 往前推一月
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1), DateUtils.DF_yyyyMMdd);
			} else if ("all".equals(para_time)) {
				// 所有数据
				end_time = null;
				start_time = null;
			}
			
			String error = this.adminAllStatisticsService.loadExportData(this.getResponse(), this.pageSize, start_time,
					end_time, this.getLoginPartyId());
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

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("para_time", para_time);
		modelAndView.setViewName("statistics_all_list");
		return modelAndView;
	}

}
