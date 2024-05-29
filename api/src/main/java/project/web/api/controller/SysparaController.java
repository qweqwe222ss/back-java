package project.web.api.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import kernel.exception.BusinessException;
import kernel.web.ResultObject;
import project.web.api.service.LocalSysparaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SysparaController {

	@Autowired
	private LocalSysparaService localSysparaService;

	/**
	 * 可逗号相隔，查询多个参数值。 exchange_rate_out 兑出货币和汇率; exchange_rate_in
	 * 兑入货币和汇率;withdraw_fee 提现手续费，type=fixed是单笔固定金额，=rate是百分比，结果到小数点2位。
	 * index_top_symbols 首页显示的4个品种。customer_service_url 在线客服URL
	 */

	@RequestMapping("api/syspara!getSyspara.action")
	public Object getSyspara(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			String code = request.getParameter("code");
			Map<String, Object> data = localSysparaService.find(code);
			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());

		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误");
		}
		return resultObject;
	}
}
