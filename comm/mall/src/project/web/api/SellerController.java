package project.web.api;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.SellerGoodsService;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerVo;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;
import project.user.kyc.KycService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import project.mall.MallRedisKeys;

@RestController
@CrossOrigin
public class SellerController extends BaseAction {
    private static Log logger = LogFactory.getLog(SellerGoodsController.class);
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerService sellerService;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected EvaluationService evaluationService;
    @Resource
    protected KycService kycService;

    @Resource
    protected CategoryService categoryService;
    @Resource
    FocusSellerService focusSellerService;
    private final String action = "/api/seller!";

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String isRecStr = request.getParameter("isRec");
        Integer isRec = null;
        if (isRecStr != null) {
            isRec = Integer.valueOf(isRecStr);
        }
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = sellerService.listSeller(pageInfo.getPageNum(), pageInfo.getPageSize(), isRec);

        List<Seller> list = mallPageInfo.getElements();
        List<String> sellerIds = list.stream().map(seller -> seller.getId().toString()).collect(Collectors.toList());
        Map<String, Long> goodsNumBySellerIds = sellerGoodsService.getGoodsNumBySellerIds(sellerIds);
        Map<String, Integer> focusCounts = focusSellerService.getFocusCounts(sellerIds);
        Map<String, Long> soldNumsBySellerIds = sellerGoodsService.getSoldNumsBySellerIds(sellerIds);

        for (Seller pl : list) {
//            JSONObject o = new JSONObject();
//            o.put("avatar", pl.getAvatar());
//            o.put("name", pl.getName());
            SellerVo sellerVo = new SellerVo();
            String sellerId = pl.getId().toString();
            BeanUtils.copyProperties(pl, sellerVo);
//            sellerVo.setSellerGoodsNum(sellerGoodsService.getGoodsNumBySellerId(sellerVo.getId().toString()));
            sellerVo.setSellerGoodsNum(Objects.nonNull(goodsNumBySellerIds.get(sellerId))?goodsNumBySellerIds.get(sellerId):0L);
//            Integer focusNum = focusSellerService.getFocusCount(sellerVo.getId().toString());
//            sellerVo.setFocusNum(focusNum == null ? 0 : focusNum);
            sellerVo.setFocusNum(Objects.nonNull(focusCounts.get(sellerId))?focusCounts.get(sellerId):0);
            sellerVo.setFake(sellerVo.getFake() == null ? 0 : sellerVo.getFake());
//            sellerVo.setHighOpinion(evaluationService.getHighOpinionBySellerId(sellerVo.getId().toString()));
            sellerVo.setHighOpinion(evaluationService.getSellerFavorableRate(sellerId));
//            sellerVo.setSoldNum(sellerVo.getSoldNum() + sellerGoodsService.getSoldNumBySellerId(sellerVo.getId().toString()));
            sellerVo.setSoldNum(sellerVo.getSoldNum() + (Objects.nonNull(soldNumsBySellerIds.get(sellerId))?soldNumsBySellerIds.get(sellerVo.getId().toString()):0));
//            sellerVo.setViewsNum(sellerGoodsService.getViewsNumBySellerId(pl.getSellerId()));
            jsonArray.add(sellerVo);
        }
        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 获取店铺分类
     *
     * @param request
     * @return
     */
    @PostMapping(action + "getCategory.action")
    public ResultObject getCategory(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        if (StringUtils.isEmpty(sellerId)) {
            resultObject.setCode("1");
            resultObject.setMsg("参数错误");
            return resultObject;
        }
        String lang = request.getParameter("lang");
        List<String> categoryId = sellerGoodsService.getSellerCategoryList(sellerId, true);
        Set<String> idSet = new HashSet<>();
        for (String id : categoryId) {
            idSet.add(id);
        }
        JSONArray jsonArray = new JSONArray();
        for (Category category : categoryService.listCategory(1, 1000)) {

            if (idSet.contains(category.getId().toString())) {
                JSONObject o = new JSONObject();
                String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
                String js = redisHandler.getString(key);
                if (StringUtils.isEmptyString(js)) {
                    continue;
                }
                CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
                o.put("name", pLang.getName());
                o.put("categoryId", pLang.getCategoryId());
                o.put("iconImg", category.getIconImg());
                o.put("des", pLang.getDes());
                o.put("sort", category.getSort());
                jsonArray.add(o);
            }

        }
        resultObject.setData(jsonArray);
        return resultObject;
    }


    @PostMapping(action + "info.action")
    public Object info(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String userId = getLoginPartyId();
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        Seller seller = sellerService.getSeller(sellerId);
        SellerVo sellerVo = new SellerVo();
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("暂无店铺");
            resultObject.setData(sellerVo);
            return resultObject;
        }
        BeanUtils.copyProperties(seller, sellerVo);
        Long goodsNum = sellerGoodsService.getGoodsNumBySellerId(seller.getId().toString());
        if (goodsNum != null && goodsNum > 0) {
            sellerVo.setOnShelvesFlag("1");
        } else {
            sellerVo.setOnShelvesFlag("0");
        }
        Integer fake = seller.getFake();
        sellerVo.setHighOpinion(evaluationService.getHighOpinionBySellerId(sellerId));
        sellerVo.setSoldNum(seller.getSoldNum() + sellerGoodsService.getSoldNumBySellerId(sellerId));
        sellerVo.setViewsNum(sellerGoodsService.getViewsNumBySellerId(sellerId));
        sellerVo.setSellerGoodsNum(goodsNum);
        sellerVo.setFocusNum(focusSellerService.getFocusCount(seller.getId().toString()));
        sellerVo.setSellerGoodsNum(sellerGoodsService.getGoodsNumBySellerId(seller.getId().toString()));
        sellerVo.setFocusNum(focusSellerService.getFocusCount(seller.getId().toString()) + ((null == fake) ? 0 : fake));
//        店铺详情解码处理(2023-04-13 修改mysql数据库字符集为utf8mb4后不需要解码)
//        sellerVo.setShopRemark(MyEmojiUtil.decodeUnicode(sellerVo.getShopRemark()));
        if (userId != null) {
            sellerVo.setIsFocus(focusSellerService.queryIsFocus(sellerId, userId));
        }

//        JSONArray jsonArray = new JSONArray();
////        for (Seller pl : sellerService.listSeller(pageInfo.getPageNum(), pageInfo.getPageSize(),isRec)) {
////            JSONObject o = new JSONObject();
////            o.put("avatar", pl.getAvatar());
////            o.put("name", pl.getName());
////            jsonArray.add(o);
////        }
////        JSONObject object = new JSONObject();
////        object.put("pageInfo", pageInfo);
////        object.put("pageList", jsonArray);
        resultObject.setData(sellerVo);
        return resultObject;
    }
}
