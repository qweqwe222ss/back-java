package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.SellerGoodsService;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerService;
import project.mall.seller.model.FocusSeller;
import project.mall.seller.model.FocusSellerVo;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerVo;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class FocusSellerController extends BaseAction {

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected FocusSellerService focusSellerService;

    @Resource
    protected EvaluationService evaluationService;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected SellerService sellerService;
    private final String action = "/api/focusSeller!";

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);
        String partyId = getLoginPartyId();

        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = focusSellerService.listFocusSeller(pageInfo.getPageNum(), pageInfo.getPageSize(), partyId);
        List<FocusSeller> list = mallPageInfo.getElements();

        for (FocusSeller pl : list) {
            FocusSellerVo focusSellerVo = new FocusSellerVo();
            BeanUtils.copyProperties(pl, focusSellerVo);
            Seller seller = sellerService.getSeller(pl.getSellerId());
            SellerVo sellerVo = new SellerVo();
            BeanUtils.copyProperties(seller, sellerVo);
//            sellerVo.setHighOpinion(evaluationService.getHighOpinionBySellerId(pl.getSellerId()));
            sellerVo.setHighOpinion(evaluationService.getSellerFavorableRate(pl.getSellerId()));
            sellerVo.setSoldNum(seller.getSoldNum() + sellerGoodsService.getSoldNumBySellerId(pl.getSellerId()));
            sellerVo.setViewsNum(sellerGoodsService.getViewsNumBySellerId(pl.getSellerId()));
            focusSellerVo.setSellerVo(sellerVo);
            jsonArray.add(focusSellerVo);
        }
        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "count.action")
    public Object focusSellerCount() {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = getLoginPartyId();
        Integer focusSellerCount = this.focusSellerService.getFocusSellerCount(partyId);
        focusSellerCount = null == focusSellerCount ? 0 : focusSellerCount;
        resultObject.setData(focusSellerCount);
        return resultObject;
    }

    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (sellerId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("店家id不能为空");
            return resultObject;
        }
        Seller seller = sellerService.getSeller(sellerId);
        if (seller == null) {
            resultObject.setCode("1");
            resultObject.setMsg("店家id错误");
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        if (focusSellerService.queryIsFocus(sellerId, partyId) == 1) {
            resultObject.setCode("1");
            resultObject.setMsg("请勿重复添加");
            return resultObject;
        }

        focusSellerService.addFocus(partyId, seller);


        resultObject.setMsg("操作成功");
        return resultObject;
    }

    @PostMapping(action + "del.action")
    public Object del(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (sellerId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("关注id不能为空");
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        focusSellerService.deleteFocus(sellerId, partyId);


        resultObject.setMsg("操作成功");
        return resultObject;
    }
}
