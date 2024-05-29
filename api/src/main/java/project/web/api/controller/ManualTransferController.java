package project.web.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.RedisKeys;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.GoodsStatisticsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.SystemGoods;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 专用于手动迁移历史数据的服务接口。
 * 为方便统一管理，涉及表结构变更，或业务逻辑变更，需要迁移历史数据时，可以将相关接口统一放置此处。
 *
 */
@RestController
@CrossOrigin
@Slf4j
public class ManualTransferController extends BaseAction {

    private final String action = "api/transfer/";

    @Autowired
    private RechargeBlockchainService rechargeBlockchainService;

    @Autowired
    private UserMetricsService userMetricsService;

    @Autowired
    private PartyService partyService;

    @Autowired
    private WalletLogService walletLogService;

    @Autowired
    private AdminMallGoodsService adminMallGoodsService;

    @Autowired
    private SellerGoodsService sellerGoodsService;

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private GoodsStatisticsService goodsStatisticsService;

    @Autowired
    private SysparaService sysparaService;

    @Autowired
    private MallAddressAreaService mallAddressAreaService;

    @Autowired
    private GoodsOrdersService goodsOrdersService;


    private Map<String, SystemGoods> systemGoodsMap = new HashMap<>();

    /**
     * walletLog 表新增字段：usde_amount, 填充该字段值.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "walletlog/fillamount")
    public Object fillWalletLogAmount(HttpServletRequest request) {
        String selectedPartyIds = request.getParameter("partyIds");
        log.info("===========> ManualTransferController fillWalletLogAmount selectedPartyIds:{}", selectedPartyIds);
        ResultObject resultObject = new ResultObject();


        List<WalletLog> allWalletLogList = walletLogService.getAll();
        for (WalletLog oneWalletLog : allWalletLogList) {
            oneWalletLog.setUsdtAmount(oneWalletLog.getAmount());
            if (StrUtil.isBlank(oneWalletLog.getOrder_no())) {
                //
            } else {
                RechargeBlockchain rechargeEntity = rechargeBlockchainService.findByOrderNo(oneWalletLog.getOrder_no());
                if (rechargeEntity != null) {
                    // USDT 单位
                    oneWalletLog.setUsdtAmount(rechargeEntity.getAmount());
                }
            }

            walletLogService.update(oneWalletLog);
        }

        return resultObject;
    }

    /**
     * 迁移用户充值累计金额指标
     *
     * @param request
     * @return
     */
    @GetMapping(action + "usermetrics/rechargeMoneyAcc")
    public Object transferRechargeMoneyAcc(HttpServletRequest request) {
        String selectedPartyIds = request.getParameter("partyIds");

        log.info("===========> ManualTransferController transferRechargeMoneyAcc selectedPartyIds:{}", selectedPartyIds);
        ResultObject resultObject = new ResultObject();

        List<Party> allPartyList = null;
        if (StrUtil.isBlank(selectedPartyIds)) {
            allPartyList = partyService.getAll();
        } else {
            allPartyList = new ArrayList<>();
            String[] partyIdArr = selectedPartyIds.split(",");
            for (String onePartyId : partyIdArr) {
                if (StrUtil.isBlank(onePartyId)) {
                    continue;
                }

                Party oneParty = new Party();
                oneParty.setId(onePartyId.trim());
                allPartyList.add(oneParty);
            }
        }

        for (Party oneParty : allPartyList) {
            String currentPartyId = oneParty.getId().toString();
            // double rechargeAmount = rechargeBlockchainService.computeRechargeAmount(currentPartyId);
            double rechargeAmount = walletLogService.getComputeRechargeAmount(currentPartyId);
            if (rechargeAmount == 0.00) {
                continue;
            }

            Date now = new Date();
            UserMetrics userMetrics = userMetricsService.getByPartyId(currentPartyId);
            if (userMetrics == null) {
                userMetrics = new UserMetrics();

                //userMetrics.setId();
                userMetrics.setAccountBalance(0.0D);
                userMetrics.setMoneyRechargeAcc(0.0D);
                userMetrics.setMoneyWithdrawAcc(0.0D);
                userMetrics.setPartyId(currentPartyId);
                userMetrics.setStatus(1);
                userMetrics.setTotleIncome(0.0D);
                userMetrics.setCreateTime(now);
                userMetrics.setUpdateTime(now);
                userMetrics = userMetricsService.save(userMetrics);
            }

            userMetrics.setMoneyRechargeAcc(rechargeAmount);
            userMetricsService.update(userMetrics);
        }

        return resultObject;
    }


    /**
     * 将折扣商品的截止日期加载到 redis 缓存.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "goods/cacheSellerGoodsDiscountInfo")
    public Object cacheSellerGoodsDiscountInfo(HttpServletRequest request) {
        String sellerGoodsIds = request.getParameter("sellerGoodsIds");
        log.info("===========> ManualTransferController cacheSellerGoodsDiscountInfo, sellerGoodsIds:{}", sellerGoodsIds);
        ResultObject resultObject = new ResultObject();

        List<SellerGoods> allSellerGoodsList = null;
        if (StrUtil.isBlank(sellerGoodsIds)) {
            allSellerGoodsList = sellerGoodsService.listDiscountSellerGoods(1, Integer.MAX_VALUE);
        } else {
            String[] goodsIdArr = sellerGoodsIds.split(",");
            List<String> goodsIdList = new ArrayList<>();
            for (String oneId : goodsIdArr) {
                if (StrUtil.isBlank(oneId)) {
                    continue;
                }

                goodsIdList.add(oneId.trim());
            }

            allSellerGoodsList = sellerGoodsService.getSellerGoodsBatch(goodsIdList);
        }

        Date now = new Date();
        for (SellerGoods oneGoods : allSellerGoodsList) {
            if (oneGoods == null || oneGoods.getIsValid() == 0 || oneGoods.getIsShelf() == 0) {
                redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, oneGoods.getId().toString());
                continue;
            }
            String currentGoodsId = oneGoods.getId().toString();

            if (oneGoods.getDiscountRatio() == null) {
                oneGoods.setDiscountRatio(0.0);
            }
            if (oneGoods.getDiscountRatio() > 0.0 && oneGoods.getDiscountRatio() < 1.0) {
                Date discountStartTime = oneGoods.getDiscountStartTime();
                Date discountEndTime = oneGoods.getDiscountEndTime();

                // 简单粗暴地处理方式：直接相对于上一次状态有变更
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, 1, currentGoodsId);

                if (discountEndTime != null && discountEndTime.after(now)) {
                    // 折扣时间未结束，此处先不管是否已开始折扣
                    redisHandler.zadd(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, discountEndTime.getTime(), currentGoodsId);
                } else {
                    // 折扣活动已过期
                    redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, currentGoodsId);
                }
            } else {
                // 无效折扣比例
                redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, currentGoodsId);
            }
        }

        return resultObject;
    }


    @GetMapping(action + "goods/refreshShowWeight2")
    public Object refreshShowWeight2(HttpServletRequest request) {
        String sellerGoodsIds = request.getParameter("sellerGoodsIds");
        log.info("===========> ManualTransferController refreshShowWeight2, sellerGoodsIds:{}", sellerGoodsIds);
        ResultObject resultObject = new ResultObject();

        if (StrUtil.isNotBlank(sellerGoodsIds)) {
            String[] goodsIdArr = sellerGoodsIds.split(",");
            List<String> goodsIdList = new ArrayList<>();
            for (String oneId : goodsIdArr) {
                if (StrUtil.isBlank(oneId)) {
                    continue;
                }

                goodsIdList.add(oneId.trim());
            }

            goodsStatisticsService.updateRefreshSellerGoodsShowWeight(goodsIdList);

            return resultObject;
        }

        int pageNum = 1;
        int pageSize = 50;
        List<SellerGoods> allSellerGoodsList = null;
        while (true) {
            allSellerGoodsList = sellerGoodsService.pagedAllSellerGoods(pageNum, pageSize);
            pageNum++;
            if (CollectionUtil.isEmpty(allSellerGoodsList)) {
                break;
            }

            List<String> sellerGoodsIdList = new ArrayList<>();
            for (SellerGoods oneGoods : allSellerGoodsList) {
                if (oneGoods == null || oneGoods.getIsValid() == 0 || oneGoods.getIsShelf() == 0) {
                    continue;
                }

                String currentGoodsId = oneGoods.getId().toString();
                sellerGoodsIdList.add(currentGoodsId);
            }

            goodsStatisticsService.updateRefreshSellerGoodsShowWeight(sellerGoodsIdList);
        }

        return resultObject;
    }

    /**
     * 将折扣商品的截止日期加载到 redis 缓存.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "goods/cacheSellerGoodsFirstDiscountTime")
    public Object cacheSellerGoodsFirstDiscountTime(HttpServletRequest request) {
        String sellerGoodsIds = request.getParameter("sellerGoodsIds");
        log.info("===========> ManualTransferController cacheSellerGoodsFirstDiscountTime, sellerGoodsIds:{}", sellerGoodsIds);
        ResultObject resultObject = new ResultObject();

        Date now = new Date();
        List<SellerGoods> allSellerGoodsList = null;
        if (StrUtil.isBlank(sellerGoodsIds)) {
            int newSellerGoodsDayLimit = 7;
            Syspara newSellerGoodsDayLimitParam = sysparaService.find(SysParaCode.NEW_SELLER_GOODS_DAY_LIMIT.getCode());
            if (newSellerGoodsDayLimitParam != null) {
                String value = newSellerGoodsDayLimitParam.getValue().trim();
                newSellerGoodsDayLimit = Integer.parseInt(value);
            }

            long limitTime = now.getTime() - (long)newSellerGoodsDayLimit * 24L * 3600L * 1000L;
            allSellerGoodsList = sellerGoodsService.pagedNewSellerGoods(limitTime,1, Integer.MAX_VALUE);
        } else {
            String[] goodsIdArr = sellerGoodsIds.split(",");
            List<String> goodsIdList = new ArrayList<>();
            for (String oneId : goodsIdArr) {
                if (StrUtil.isBlank(oneId)) {
                    continue;
                }

                goodsIdList.add(oneId.trim());
            }

            allSellerGoodsList = sellerGoodsService.getSellerGoodsBatch(goodsIdList);
        }

        for (SellerGoods oneGoods : allSellerGoodsList) {
            String currentGoodsId = oneGoods.getId().toString();

            redisHandler.zadd(RedisKeys.SELLER_GOODS_FIRST_SHELF_TIME, oneGoods.getFirstShelfTime(), currentGoodsId);
        }

        return resultObject;
    }


    @GetMapping(action + "goods/refreshCategory")
    public Object refreshCategory(HttpServletRequest request) {
        String systemGoodsIds = request.getParameter("systemGoodsIds");
        String action = request.getParameter("action");

        log.info("===========> ManualTransferController refreshCategory, systemGoodsIds:{}", systemGoodsIds);
        ResultObject resultObject = new ResultObject();

        if (StrUtil.isBlank(action)) {
            action = "";
        }
        if (action.equalsIgnoreCase("clearCache")) {
            this.systemGoodsMap.clear();
            return resultObject;
        }

        List<SellerGoods> allSellerGoodsList = null;
        if (StrUtil.isBlank(systemGoodsIds)) {
            allSellerGoodsList = sellerGoodsService.pagedAllSellerGoods(1, Integer.MAX_VALUE);
        } else {
            String[] goodsIdArr = systemGoodsIds.split(",");
            List<String> goodsIdList = new ArrayList<>();
            for (String oneId : goodsIdArr) {
                if (StrUtil.isBlank(oneId)) {
                    continue;
                }
                goodsIdList.add(oneId.trim());
            }

            allSellerGoodsList = sellerGoodsService.listBySystemGoodsIds(goodsIdList);
        }
        log.info("========> ManualTransferController refreshCategory 需要更新的商品记录数量为:{}", allSellerGoodsList.size());

        for (SellerGoods oneGoods : allSellerGoodsList) {
            String systemGoodsId = oneGoods.getGoodsId();
            SystemGoods systemGoods = systemGoodsMap.get(systemGoodsId);
            if (systemGoods == null) {
                systemGoods = adminMallGoodsService.findById(systemGoodsId);
                if (systemGoods == null) {
                    systemGoods = new SystemGoods();
                }
                systemGoodsMap.put(systemGoodsId, systemGoods);
            }
            if (systemGoods.getId() == null) {
                log.error("========> ManualTransferController refreshCategory 当前店铺商品:{} 没有对应的平台商品记录", oneGoods.getId());
                continue;
            }

            String categoryId = systemGoods.getCategoryId();
            String secondaryCategoryId = systemGoods.getSecondaryCategoryId();
            if (Objects.equals(oneGoods.getCategoryId(), categoryId)
                    && Objects.equals(oneGoods.getSecondaryCategoryId(), secondaryCategoryId)) {
                // 无需调整
                continue;
            }

            oneGoods.setCategoryId(categoryId);
            oneGoods.setSecondaryCategoryId(secondaryCategoryId);

            sellerGoodsService.updateSellerGoods(oneGoods);
            // 无需刷新店铺商品相关的缓存

        }

        return resultObject;
    }


    @GetMapping("/api/demo/listCountry")
    public Object listCountry(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        List<MallCountry> allCountry = mallAddressAreaService.listAllCountry();

        resultObject.setData(allCountry);
        return resultObject;
    }

    @GetMapping("/api/demo/listState")
    public Object listState(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String countryId = request.getParameter("countryId");

        List<MallState> stateList = mallAddressAreaService.listAllState(Long.parseLong(countryId));

        resultObject.setData(stateList);
        return resultObject;
    }

    @GetMapping("/api/demo/listCity")
    public Object listCity(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String stateId = request.getParameter("stateId");

        List<MallCity> cityList = mallAddressAreaService.listAllCity(Long.parseLong(stateId));

        resultObject.setData(cityList);
        return resultObject;
    }

    @GetMapping(action + "order/fillFlag")
    public Object fillOrderFlag(HttpServletRequest request) {
        String orderIds = request.getParameter("orderIds");

        log.info("===========> ManualTransferController fillOrderFlag, orderIds:{}", orderIds);
        ResultObject resultObject = new ResultObject();

        if (StrUtil.isBlank(orderIds)) {
            int offset = 0;
            int pageSize = 50;
            int count = 0;
            MallPageInfo pageInfo = goodsOrdersService.pagedListNoneFlagOrder(offset, pageSize);
            int max = pageInfo.getTotalElements();
            log.info("========> ManualTransferController fillOrderFlag 需要更新的商品记录数量为:{}", max);

            while (count < max) {
                List<MallOrdersPrize> pageList = pageInfo.getElements();
                count += pageList.size();

                goodsOrdersService.updateOrderFlag(pageList);
                //refreshOrderFlag(pageList);
                try {
                    Thread.sleep(50L);
                } catch (Exception e) {

                }

                // 更新状态后，关系到查询条件的字段值发生了变更，需要重新从 0 开始查询
                pageInfo = goodsOrdersService.pagedListNoneFlagOrder(offset, pageSize);
                log.info("========> ManualTransferController fillOrderFlag 分页需要更新的商品记录数量为:{}, 剩余总量为:{}", pageInfo.getElements().size(), pageInfo.getTotalElements());
            }
        } else {
            String[] orderIdArr = orderIds.split(",");
            List<String> orderIdList = new ArrayList<>();
            for (String oneId : orderIdArr) {
                if (StrUtil.isBlank(oneId)) {
                    continue;
                }
                orderIdList.add(oneId.trim());
            }

            List<MallOrdersPrize> orderEntityList = goodsOrdersService.ListBatchOrder(orderIdList);
            log.info("========> ManualTransferController fillOrderFlag 需要更新的商品记录数量为:{}", orderEntityList.size());

            goodsOrdersService.updateOrderFlag(orderEntityList);
            // refreshOrderFlag(orderEntityList);
        }


        return resultObject;
    }

    private void refreshOrderFlag(List<MallOrdersPrize> orderEntityList) {
        for (MallOrdersPrize oneOrderEntity : orderEntityList) {
            Party sellerParty = partyService.cachePartyBy(oneOrderEntity.getSellerId(), false);
            Party buyerParty = partyService.cachePartyBy(oneOrderEntity.getPartyId(), false);

            // 订单特殊标记：1-买家是演示账号，2-卖家是演示账号，3-买家和卖家都是演示账号
            oneOrderEntity.setFlag(0);
            // 左侧为高位，右侧为低位
            // 买家用户账号类型标记
            int buyerFlag = 0B00;
            // 卖家用户账号类型标记
            int sellerFlag = 0B00;

            if (sellerParty != null && sellerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                // 卖家是演示账号
                sellerFlag = 0B10;
            }
            if (buyerParty != null && buyerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                // 买家是演示账号
                buyerFlag = 0B01;
            }
            oneOrderEntity.setFlag(buyerFlag | sellerFlag);

            goodsOrdersService.updateOrder(oneOrderEntity);
        }
    }


}
