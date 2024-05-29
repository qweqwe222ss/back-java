package project.web.api.controller;

import kernel.util.JsonUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.SellerTopNDto;
import project.mall.seller.dto.MallLevelCondExpr;
import project.onlinechat.OnlineChatUserMessageService;
import project.wallet.WalletLogService;
import security.SaltSigureUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不方便测试的服务可以在此处统一调试
 *
 */
@RestController
@CrossOrigin
public class DemoController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(DemoController.class);
	
	@Autowired
    protected OnlineChatUserMessageService onlineChatUserMessageService;

	@Autowired
    protected WalletLogService walletLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SellerGoodsService sellerGoodsService;

	private final String action = "api/demo!";

    public static void main(String[] args) {
        String json = "{\"params\":[{\"code\":\"rechargeAmount\",\"title\":\"运行资金\",\"value\":5000},{\"code\":\"popularizeUserCount\",\"title\":\"分店数\",\"value\":3}],\"expression\":\"popularizeUserCount >= 3 || rechargeAmount >= 5000\"}";
        MallLevelCondExpr cndObj = JsonUtils.json2Object(json, MallLevelCondExpr.class);

        System.out.println("======> cndObj.param1:" + JsonUtils.getJsonString(cndObj.getParams().get(0)));

    }

    @GetMapping(action + "demo1.show")
    public Object listNotificationTemplate(HttpServletRequest request) {
        String buyerId = request.getParameter("buyerId");
        String sellerId = request.getParameter("sellerId");
        String userId = request.getParameter("userId");
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        String password_encoder1 = passwordEncoder.encodePassword(password, account);
        String password_encoder2 = passwordEncoder.encodePassword(password, SaltSigureUtils.saltfigure);

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        OnlineChatUserMessage lastImInfo = onlineChatUserMessageService.lastImMessage(buyerId, sellerId, false, 0L);
//        logger.info("-----> lastImInfo:" + lastImInfo);

//        List<Map<String, Object>> data = this.walletLogService.pagedQueryRecharge(1, 10, userId, "1").getElements();
//        for (Map<String, Object> log : data) {
//            if (null == log.get("coin") || !StringUtils.isNotEmpty(log.get("coin").toString())) {
//                log.put("coin", Constants.WALLET);
//            } else {
//                log.put("coin", log.get("coin").toString().toUpperCase());
//            }
//            String state = log.get("state").toString();
//            Object reviewTime = log.get("reviewTime");
//            log.put("reviewTime", reviewTime);
//        }
//        resultObject.setData(data);

        Map<String, Object> mockData = new HashMap<>();
        resultObject.setData(mockData);

        mockData.put("safePass1", password_encoder1);
        mockData.put("safePass2", password_encoder2);

        return resultObject;
    }


    @GetMapping(action + "demo2.show")
    public Object demo2(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String startTime = "2023-01-01 00:00:00";
        String endTime = "2023-08-01 00:00:00";

        List<SellerTopNDto> top10SellerList = sellerGoodsService.cacheTopNSellers(startTime, endTime, 10);

        return resultObject;
    }

}
