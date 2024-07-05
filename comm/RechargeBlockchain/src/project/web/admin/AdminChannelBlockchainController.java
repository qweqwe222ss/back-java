package project.web.admin;

import javax.servlet.http.HttpServletRequest;

import kernel.web.Page;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.blockchain.AdminChannelBlockchainService;
import project.blockchain.ChannelBlockchain;
import project.blockchain.ChannelBlockchainService;
import project.blockchain.QRProducerService;

import java.util.HashMap;

/**
 * 区块链充值地址维护
 */
@RestController
public class AdminChannelBlockchainController extends PageActionSupport {

    private Logger logger = LogManager.getLogger(AdminChannelBlockchainController.class);

    @Autowired
    private AdminChannelBlockchainService adminChannelBlockchainService;
    @Autowired
    private ChannelBlockchainService channelBlockchainService;
    @Autowired
    private QRProducerService qRProducerService;

    private final String action = "normal/adminChannelBlockchainAction!";

    /**
     * 获取 区块链充值地址 列表
     * <p>
     * name_para 链名称
     * coin_para 币种名称
     */
    @RequestMapping(action + "list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String name_para = request.getParameter("name_para");
        String coin_para = request.getParameter("coin_para");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("channel_blockchain_list");

        try {

            this.checkAndSetPageNo(pageNo);

            this.pageSize = 300;

            this.page = this.adminChannelBlockchainService.pagedQuery(this.pageNo, this.pageSize, name_para, coin_para);

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
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("name_para", name_para);
        modelAndView.addObject("coin_para", coin_para);
        return modelAndView;
    }

//	/**
//	 * 获取区块链个人充值地址列表
//	 * @param request
//	 * @return
//	 * view:channel_blockchain_list.jsp
//	 */
//	@RequestMapping(action + "personList.action")
//	public ResultObject personList(HttpServletRequest request) {
//		String address = request.getParameter("address");
//		String pageNoStr = request.getParameter("pageNo");
//		String roleName = request.getParameter("roleName");
//		String userName = request.getParameter("userName");
//		String chainName = request.getParameter("chainName");
//		String coinSymbol = request.getParameter("coinSymbol");
//
//		int pageNo=1;
//		Page page=null;
//		int pageSize=300;
//		ResultObject resultObject=new ResultObject();
//
//		try {
////			pageNo=checkAndSetPageNo(pageNoStr);
//			this.checkAndSetPageNo(pageNoStr);
//			this.pageSize = 300;
//			page=adminChannelBlockchainService.pagedPersonQuery(pageNo, pageSize,userName,roleName,chainName,coinSymbol,address);
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//			return resultObject;
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			resultObject.setCode("1");
//			resultObject.setMsg(t.getMessage());
//			return resultObject;
//		}
//
//		HashMap<String,Object> resultDict=new HashMap<String,Object>();
//		resultDict.put("page", page);
//		resultDict.put("pageNo", pageNo);
//		resultDict.put("pageSize", pageSize);
//		resultObject.setCode("0");
//		resultObject.setData(resultDict);
//		resultObject.setMsg("获取数据成功!");
//		return resultObject;
//	}

    @RequestMapping({action + "toAdd.action"})
    public ModelAndView toAdd(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("coins_map", Constants.BLOCKCHAIN_COINS);
        modelAndView.addObject("blockchain_name_map", Constants.BLOCKCHAIN_COINS_NAME);
        modelAndView.setViewName("channel_blockchain_add");
        return modelAndView;
    }

    @RequestMapping({action + "add.action"})
    public ModelAndView add(HttpServletRequest request) {
        String blockchain_name = request.getParameter("blockchain_name");
        String coin = request.getParameter("coin");
        String address = request.getParameter("address");
        String img = request.getParameter("img");
        String safeword = request.getParameter("safeword");
        String email_code = request.getParameter("email_code");
        String super_google_auth_code = request.getParameter("super_google_auth_code");
        ModelAndView modelAndView = new ModelAndView();
        try {
            String error = verif(blockchain_name, coin, address, img);
            if (!StringUtils.isNullOrEmpty(error))
                throw new BusinessException(error);
            ChannelBlockchain channelBlockchain = new ChannelBlockchain();
            channelBlockchain.setBlockchain_name(blockchain_name);
            channelBlockchain.setCoin(coin);
            channelBlockchain.setAddress(address);
            img = this.qRProducerService.generate(address);
            channelBlockchain.setImg(img);
            this.channelBlockchainService.save(channelBlockchain, getUsername_login(), safeword,
                    getIp(getRequest()), email_code, super_google_auth_code);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("coins_map", Constants.BLOCKCHAIN_COINS);
            modelAndView.addObject("blockchain_name_map", Constants.BLOCKCHAIN_COINS_NAME);
            modelAndView.addObject("blockchain_name", blockchain_name);
            modelAndView.addObject("coin", coin);
            modelAndView.addObject("address", address);
            modelAndView.addObject("img", img);
            modelAndView.setViewName("channel_blockchain_add");
            return modelAndView;
        } catch (Throwable t) {
            this.logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.addObject("coins_map", Constants.BLOCKCHAIN_COINS);
            modelAndView.addObject("blockchain_name_map", Constants.BLOCKCHAIN_COINS_NAME);
            modelAndView.addObject("blockchain_name", blockchain_name);
            modelAndView.addObject("coin", coin);
            modelAndView.addObject("address", address);
            modelAndView.addObject("img", img);
            modelAndView.setViewName("channel_blockchain_add");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/normal/adminChannelBlockchainAction!list.action");
        return modelAndView;
    }

    @RequestMapping({action + "toUpdate.action"})
    public ModelAndView toUpdate(HttpServletRequest request) {
        String id = request.getParameter("id");
        ModelAndView modelAndView = new ModelAndView();
        try {
            ChannelBlockchain channelBlockchain = this.channelBlockchainService.findById(id);
            modelAndView.addObject("id", id);
            modelAndView.addObject("address", channelBlockchain.getAddress());
            modelAndView.addObject("coin", channelBlockchain.getCoin());
            modelAndView.addObject("blockchain_name", channelBlockchain.getBlockchain_name());
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/normal/adminChannelBlockchainAction!list.action");
            return modelAndView;
        } catch (Throwable t) {
            this.logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/normal/adminChannelBlockchainAction!list.action");
            return modelAndView;
        }
        modelAndView.setViewName("channel_blockchain_update");
        return modelAndView;
    }

    @RequestMapping({action + "update.action"})
    public ModelAndView update(HttpServletRequest request) {
        String id = request.getParameter("id");
        String blockchain_name = request.getParameter("blockchain_name");
        String coin = request.getParameter("coin");
        String address = request.getParameter("address");
        String img = request.getParameter("img");
        String safeword = request.getParameter("safeword");
        String email_code = request.getParameter("email_code");
        String super_google_auth_code = request.getParameter("super_google_auth_code");
        ModelAndView modelAndView = new ModelAndView();
        try {
            ChannelBlockchain channelBlockchain = this.channelBlockchainService.findById(id);
            ChannelBlockchain old = new ChannelBlockchain();
            BeanUtils.copyProperties(channelBlockchain, old);
            String error = verif(blockchain_name, coin, address, img);
            if (!StringUtils.isNullOrEmpty(error))
                throw new BusinessException(error);
            channelBlockchain.setBlockchain_name(blockchain_name);
            channelBlockchain.setCoin(coin);
            if (!address.equals(channelBlockchain.getAddress())) {
                channelBlockchain.setAddress(address);
                img = this.qRProducerService.generate(address);
                channelBlockchain.setImg(img);
            }
            this.channelBlockchainService.update(old, channelBlockchain, getUsername_login(),
                    getLoginPartyId(), safeword, getIp(getRequest()), email_code, super_google_auth_code);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("id", id);
            modelAndView.addObject("blockchain_name", blockchain_name);
            modelAndView.addObject("coin", coin);
            modelAndView.addObject("address", address);
            modelAndView.addObject("img", img);
            modelAndView.setViewName("channel_blockchain_update");
            return modelAndView;
        } catch (Throwable t) {
            this.logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.addObject("id", id);
            modelAndView.addObject("blockchain_name", blockchain_name);
            modelAndView.addObject("coin", coin);
            modelAndView.addObject("address", address);
            modelAndView.addObject("img", img);
            modelAndView.setViewName("channel_blockchain_update");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/normal/adminChannelBlockchainAction!list.action");
        return modelAndView;
    }

    @RequestMapping({action + "toDelete.action"})
    public ModelAndView toDelete(HttpServletRequest request) {
        String id = request.getParameter("id");
        String safeword = request.getParameter("safeword");
        String email_code = request.getParameter("email_code");
        String super_google_auth_code = request.getParameter("super_google_auth_code");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/normal/adminChannelBlockchainAction!list.action");
        try {
            this.channelBlockchainService.delete(id, safeword, getUsername_login(), getIp(), email_code, super_google_auth_code);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            this.logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }

    private String verif(String blockchain_name, String coin, String address, String img) {
        if (StringUtils.isEmptyString(blockchain_name))
            return "请输入链名称";
        if (StringUtils.isEmptyString(coin))
            return "请输入币种";
        if (StringUtils.isEmptyString(address))
            return "请输入地址";
        return null;
    }

    @RequestMapping({action + "personList.action"})
    public ModelAndView personList(HttpServletRequest request) {
        String address = request.getParameter("address");
        String pageNoStr = request.getParameter("pageNo");
        String roleName = request.getParameter("roleName");
        String userName = request.getParameter("userName");
        String chainName = request.getParameter("chainName");
        String coinSymbol = request.getParameter("coinSymbol");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("channel_blockchain_lists");
        try {
            checkAndSetPageNo(pageNoStr);
            this.pageSize = 20;
            this.page = this.adminChannelBlockchainService.pagedPersonQuery(this.pageNo, this.pageSize, userName, roleName, chainName, coinSymbol, address);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            this.logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("pageNo", Integer.valueOf(this.pageNo));
        modelAndView.addObject("pageSize", Integer.valueOf(this.pageSize));
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", this.message);
        modelAndView.addObject("error", this.error);
        modelAndView.addObject("roleName", roleName);
        modelAndView.addObject("address", address);
        modelAndView.addObject("userName", userName);
        modelAndView.addObject("chainName", chainName);
        modelAndView.addObject("coinSymbol", coinSymbol);
        return modelAndView;
    }

}
