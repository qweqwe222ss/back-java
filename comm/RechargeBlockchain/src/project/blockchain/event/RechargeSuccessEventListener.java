package project.blockchain.event;

import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.wallet.WalletLogService;

import java.util.Date;

/**
 * 用户充值审核通过后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 更新用户累计充值金额指标统计记录：
 * 2. ....
 *
 */
public class RechargeSuccessEventListener implements ApplicationListener<RechargeSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RechargeBlockchainService rechargeBlockchainService;

    private WalletLogService walletLogService;

    private UserMetricsService userMetricsService;

    @Override
    public void onApplicationEvent(RechargeSuccessEvent event) {
        RechargeInfo changeInfo = event.getRechargeInfo();
        logger.info("监听到用户成功充值事件:" + JSON.toJSONString(changeInfo));

        try {
            // double rechargeAcc = rechargeBlockchainService.computeRechargeAmount(changeInfo.getApplyUserId());
            double rechargeAcc = walletLogService.getComputeRechargeAmount(changeInfo.getApplyUserId());

            Date now = new Date();
            UserMetrics userMetrics = userMetricsService.getByPartyId(changeInfo.getApplyUserId());
            if (userMetrics == null) {
                userMetrics = new UserMetrics();

                userMetrics.setAccountBalance(0.0D);
                userMetrics.setMoneyRechargeAcc(0.0D);
                userMetrics.setMoneyWithdrawAcc(0.0D);
                userMetrics.setPartyId(changeInfo.getApplyUserId());
                userMetrics.setStatus(1);
                userMetrics.setTotleIncome(0.0D);
                userMetrics.setCreateTime(now);
                userMetrics.setUpdateTime(now);
                userMetrics = userMetricsService.save(userMetrics);
            }

            userMetrics.setStoreMoneyRechargeAcc(userMetrics.getStoreMoneyRechargeAcc()+changeInfo.getAmount());
            userMetrics.setMoneyRechargeAcc(rechargeAcc);
            userMetricsService.update(userMetrics);

        } catch (Exception e) {
            logger.error("用户充值审核通过后，更新用户的相关指标数据报错，变更信息为:{}", JsonUtils.getJsonString(changeInfo), e);
        }

    }


    public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
        this.rechargeBlockchainService = rechargeBlockchainService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setUserMetricsService(UserMetricsService userMetricsService) {
        this.userMetricsService = userMetricsService;
    }

}
