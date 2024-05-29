package project.web.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import jnr.posix.windows.CommonFileInformation;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.banner.MallBannerService;
import project.mall.banner.model.MallBanner;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.MallLevelDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class MallLevelController extends BaseAction {

    private static Log logger = LogFactory.getLog(MallLevelController.class);

    @Resource
    protected MallLevelService mallLevelService;

    @Resource
    protected SellerService sellerService;

    @Resource
    protected SysparaService sysparaService;

    private final String action = "/api/malllevel!";

    @GetMapping( action + "levelList.action")
    public Object mallLevelList(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String currentLevel = "";
        String sellerId = this.getLoginPartyId();
        if (StrUtil.isNotBlank(sellerId) && !Objects.equals(sellerId, "0")) {
            Seller sellerEntity = sellerService.getSeller(sellerId);
            if (StrUtil.isNotBlank(sellerEntity.getMallLevel()) && !Objects.equals(sellerEntity.getMallLevel(), "0")) {
                currentLevel = sellerEntity.getMallLevel().trim();
            }
        }

        List<MallLevel> levelEntityList = this.mallLevelService.listLevel();
        Map<String, Integer> levelSortMap = new HashMap<>();
        levelSortMap.put("C", 1);
        levelSortMap.put("B", 2);
        levelSortMap.put("A", 3);
        levelSortMap.put("S", 4);
        levelSortMap.put("SS",5);
        levelSortMap.put("SSS",6);

        CollUtil.sort(levelEntityList, new Comparator<MallLevel>() {
            @Override
            public int compare(MallLevel o1, MallLevel o2) {
                Integer seq1 = levelSortMap.get(o1.getLevel());
                Integer seq2 = levelSortMap.get(o2.getLevel());
                seq1 = seq1 == null ? 0 : seq1;
                seq2 = seq2 == null ? 0 : seq2;

                return seq1 - seq2;
            }
        });
        List<MallLevelDTO> levelInfoList = new ArrayList();

        for (MallLevel oneLevelEntity : levelEntityList) {
            MallLevelDTO oneDto = new MallLevelDTO();
            BeanUtil.copyProperties(oneLevelEntity, oneDto);
            oneDto.setCreateTime(DateUtil.formatDateTime(oneLevelEntity.getCreateTime()));
            oneDto.setUpdateTime(DateUtil.formatDateTime(oneLevelEntity.getUpdateTime()));
            levelInfoList.add(oneDto);

            if (Objects.equals(oneLevelEntity.getLevel(), currentLevel)) {
                // 当前用户处于该等级
                oneDto.setMyLevel(1);
            }

            String cndJson = oneLevelEntity.getCondExpr();
            if (StrUtil.isNotBlank(cndJson)) {
                MallLevelCondExpr cndObj = JsonUtils.json2Object(cndJson, MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = cndObj.getParams();
                if (CollectionUtil.isNotEmpty(params)) {
                    for (MallLevelCondExpr.Param oneCndParam : params) {
                        UpgradeMallLevelCondParamTypeEnum cndType = UpgradeMallLevelCondParamTypeEnum.codeOf(oneCndParam.getCode().trim());
                        if (cndType == UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT) {
                            oneDto.setRechargeAmountCnd(Integer.parseInt(oneCndParam.getValue().trim()));
                        } else if (cndType == UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER) {
                            oneDto.setPopularizeUserCountCnd(Integer.parseInt(oneCndParam.getValue().trim()));
                        }
                    }
                }
            }
        }

        JSONObject object = new JSONObject();
        object.put("result", levelInfoList);
        resultObject.setData(object);

        return resultObject;
    }


    @GetMapping( action + "config.action")
    public ResultObject getMallLevelConfig(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        // 提取用于店铺升级业务的有效充值用户的充值金额临界值
        double limitRechargeAmount = 100.0;
        Syspara syspara = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE.getCode());
        if (syspara != null) {
            String validRechargeAmountInfo = syspara.getValue().trim();
            if (StrUtil.isNotBlank(validRechargeAmountInfo)) {
                limitRechargeAmount = Double.parseDouble(validRechargeAmountInfo);
            }
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("limitRechargeAmount", limitRechargeAmount);

        resultObject.setMsg("success");
        resultObject.setData(dataMap);
        return resultObject;
    }

}
