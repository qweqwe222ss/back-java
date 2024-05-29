package project.web.api;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.ddos.IpMenuService;
import project.hobi.http.HttpHelper;
import project.hobi.http.HttpMethodType;
import project.log.Log;
import project.log.LogService;
import project.mall.MallRedisKeys;
import project.mall.seller.SellerService;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.UserRedisKeys;
import project.user.UserSafewordApply;
import project.user.UserSafewordApplyService;
import project.user.UserService;
import project.user.googleauth.GoogleAuthService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.internal.OnlineUserService;
import project.user.token.TokenService;
import project.withdraw.Withdraw;
import project.withdraw.WithdrawService;
import security.SecUser;
import security.internal.SecUserService;
import util.Strings;
import util.TokenUtils;

/**
 * API用户相关
 *
 */
@RestController
@CrossOrigin
public class UserController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	@Autowired
	private LogService logService;
	@Autowired
	private UserSafewordApplyService userSafewordApplyService;
	@Autowired
	private GoogleAuthService googleAuthService;
	@Autowired
	private IpMenuService ipMenuService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private RedisHandler redisHandler;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private WithdrawService withdrawService;
	@Autowired
	private RechargeBlockchainService rechargeBlockchainService;
	@Autowired
	private OnlineUserService onlineUserService;

	private final String action = "/api/user!";
	
	/**
	 * 用户名登录接口
	 * 
	 */
	@RequestMapping(action + "login.action")
	public Object login(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if (StringUtils.isEmptyString(username)) {
				throw new BusinessException("用户名不能为空");
			}
			
			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("登录密码不能为空");
			}
			
			if (password.length() < 6 || password.length() > 12) {
				throw new BusinessException("登录密码必须6-12位");
			}

			// 剔除手机号中的空格
			SecUser secUser = this.userService.addLogin(username.replaceAll(" ",""), password);

			String token = tokenService.savePut(secUser.getPartyId());
			userService.online(secUser.getPartyId());
			ipMenuService.saveIpMenuWhite(this.getIp());
			Party party = this.partyService.cachePartyBy(secUser.getPartyId(), false);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("token", token);
			data.put("username", secUser.getUsername());
			data.put("usercode", party.getUsercode());
			String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + secUser.getPartyId());
			if("1".equalsIgnoreCase(isBlack)){
				throw new BusinessException("当前用户已经被管理员禁用");
			}
			Log log = new Log();

			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("用户登录,ip[" + this.getIp(getRequest()) + "]");
			log.setPartyId(secUser.getPartyId());
			log.setUsername(username);
			logService.saveAsyn(log);

			party.setLogin_ip(this.getIp(getRequest()));
			this.partyService.update(party);

			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 用户名登录接口,新接口需要手机号和邮箱都可以登录
	 *
	 */
	@RequestMapping(action + "newlogin.action")
	public Object newlogin(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {

			String username = request.getParameter("username");
			String password = request.getParameter("password");

			if (StringUtils.isEmptyString(username)) {
				throw new BusinessException("用户名不能为空");
			}

			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("登录密码不能为空");
			}

			if (password.length() < 6 || password.length() > 12) {
				throw new BusinessException("登录密码必须6-12位");
			}

			// 剔除手机号中的空格
			String trueUserNameForLogin = username.replaceAll(" ","").replaceAll("/+","");
			Party loginParty = null;
			if (username.contains("@")) {//通过邮箱来查询账号名
				loginParty = Objects.nonNull(this.partyService.findPartyByUsername(username))?
						this.partyService.findPartyByUsername(username):this.partyService.getPartyByEmail(username);
				trueUserNameForLogin = Objects.nonNull(loginParty)?loginParty.getUsername():trueUserNameForLogin;
			}else{//JustShop盘口，phone和用户名会不一致，因此这里如果直接用用户名查找用户名查询不到，就用
				loginParty = Objects.nonNull(this.partyService.findPartyByUsername(trueUserNameForLogin))?
						this.partyService.findPartyByUsername(trueUserNameForLogin):this.partyService.findPartyByVerifiedPhone(username);
				trueUserNameForLogin = Objects.nonNull(loginParty)?loginParty.getUsername():trueUserNameForLogin;
			}
			SecUser secUser = this.userService.addLogin(trueUserNameForLogin, password);

			String token = tokenService.savePut(secUser.getPartyId());
			userService.online(secUser.getPartyId());
			ipMenuService.saveIpMenuWhite(this.getIp());
			Party party = this.partyService.cachePartyBy(secUser.getPartyId(), false);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("token", token);
			data.put("username", secUser.getUsername());
			data.put("usercode", party.getUsercode());
			String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + secUser.getPartyId());
			if("1".equalsIgnoreCase(isBlack)){
				throw new BusinessException("当前用户已经被管理员禁用");
			}
			Log log = new Log();

			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("用户登录,ip[" + this.getIp(getRequest()) + "]");
			log.setPartyId(secUser.getPartyId());
			log.setUsername(username);
			logService.saveAsyn(log);

			party.setLogin_ip(this.getIp(getRequest()));
			this.partyService.update(party);

			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 发送心跳接口
	 * @param request
	 * @return
	 */
	@RequestMapping(action + "heartbeat.action")
	public Object heartbeat(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String token = request.getParameter("token");
		String statusStr = request.getParameter("status");
		String currentPartyId = tokenService.cacheGet(token);
		if (StringUtils.isEmptyString(currentPartyId)) {
			return resultObject;
		}
		/*Date lastConnectTime = onlineUserService.get(currentPartyId);
//		2023-04-22 确定需求10分钟商家不操作算掉线
		long interval = Optional.ofNullable(lastConnectTime).map(s -> DateUtils.calcTimeBetween("s",s,new Date()))
				.orElse(Long.valueOf(10 * 60 + 1));
		if (interval<=300) {
			userService.online(currentPartyId);
		}else{
			userService.offline(currentPartyId);
		}*/
//		2023-04-23 需求修改 不设置用户掉线 ，设置状态 在线1，离开2，离线3 其他端不传设置为默认在线1,
		Integer status =1;
		try {
			status = Optional.ofNullable(statusStr).map(s -> Integer.valueOf(s)).orElse(1);
		} catch (Exception e) {
			logger.error("设置用户在线状态异常参数异常:",e);
			status =1;
		}
		Map<String, Object> statusParams = new ConcurrentHashMap<String, Object>();
		statusParams.put("status",status);
		statusParams.put("operateTime",new Date());
		redisHandler.setSync(UserRedisKeys.ONLINE_USER_STATUS_PARTYID+currentPartyId,statusParams);
		return resultObject;
	}

	/**
	 * 绑定手机号和验证码，发送验证码之前校验手机号和邮箱是否已经被绑定
	 *
	 */
	@RequestMapping(action + "checkAccount.action")
	public Object checkAccount(HttpServletRequest request) {
		String target = request.getParameter("target");
		ResultObject resultObject = new ResultObject();
		try {
			if (target.indexOf("@") == -1) {
				Party existParty = this.partyService.findPartyByVerifiedPhone(target);
				String newUserName = target.replace(" ", "");
				if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(newUserName)))) {
					throw new BusinessException("手机号码或邮箱已被占用");
				}
			} else {
				Party existParty = this.partyService.getPartyByEmail(target);
//				新邮箱必须未在EMAIL和USERNAME字段中使用过
				if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(target)))) {
					throw new BusinessException("手机号码或邮箱已被占用");
				}
			}
			resultObject.setData(true);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
			resultObject.setData(false);
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 用户token免登录接口
	 *
	 */
	@RequestMapping(action + "LoginFree.action")
	public Object LoginFree(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {

			String freeToken = request.getParameter("token");

			if (StringUtils.isEmptyString(freeToken)) {
				throw new BusinessException("免登录token不能为空");
			}

			if (!TokenUtils.verify(freeToken)){
				throw new BusinessException("token已过期，请重新登录");
			}

			String userName = TokenUtils.getUsername(freeToken);

			String redisToken = redisHandler.getString(PartyRedisKeys.LOGIN_PARTY_ID_TOKEN + userName);

			if (null == redisToken || !redisToken.equals(freeToken)){
				throw new BusinessException("token已过期，请重新登录");
			}

			SecUser secUser = secUserService.findUserByLoginName(userName);

			String token = tokenService.platFromSavePut(secUser.getPartyId());
//			userService.online(secUser.getPartyId());
			ipMenuService.saveIpMenuWhite(this.getIp());
			Party party = this.partyService.cachePartyBy(secUser.getPartyId(), false);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("token", token);
			data.put("username", secUser.getUsername());
			data.put("usercode", party.getUsercode());
//			String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + secUser.getPartyId());
			logger.error("免登录账号 " + secUser.getUsername());
			project.log.Log log = new project.log.Log();
			log.setPartyId(secUser.getPartyId());
			log.setExtra(userName);
			log.setUsername(userName);
			log.setOperator(TokenUtils.getLoginUserName(freeToken));
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setLog("管理员登录商家后台，商家名称[" + userName + "]。");
			logService.saveSync(log);

			party.setLogin_ip(this.getIp(getRequest()));
			this.partyService.update(party);

			resultObject.setData(data);

			redisHandler.remove(PartyRedisKeys.LOGIN_PARTY_ID_TOKEN + userName);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}



	public Object online() {

		ResultObject resultObject = new ResultObject();
		try {
			userService.online(this.getLoginPartyId());
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 退出登录
	 */
	@RequestMapping(action + "logout.action")
	public Object logout(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String token = request.getParameter("token");
		try {
			if (null != token && token.length() <= 36){
				//退出登录时候将在线标识移除
				redisHandler.remove(UserRedisKeys.ONLINE_USER_STATUS_PARTYID+this.getLoginPartyId());
				this.userService.logout(this.getLoginPartyId());
				this.tokenService.delete(token);
			} else {
				this.tokenService.removePlatFromToken(token);
			}

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 验证码登录 - 暂时不用
	 */
	@RequestMapping(action + "login_idcode.action")
	public Object login_idcode(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String username = request.getParameter("username");
		String verifcode = request.getParameter("verifcode");
		try {
			SecUser secUser = this.userService.addLogin_idcode(username, verifcode);
			String token = tokenService.savePut(secUser.getPartyId());
			userService.online(secUser.getPartyId());
			ipMenuService.saveIpMenuWhite(this.getIp());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("token", token);
			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 修改登录密码 用验证码
	 */
	@RequestMapping(action + "updatepsw.action")
	public Object updatepsw(HttpServletRequest request) {
		String password = request.getParameter("password");
		String verifcode_type = request.getParameter("verifcode_type");
		String verifcode = request.getParameter("verifcode");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {

			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("密码不能为空");
			}
			if (password.length() < 6 || password.length() > 12) {
				throw new BusinessException("密码必须6-12位");
			}
			if (StringUtils.isEmptyString(verifcode_type)) {
				throw new BusinessException("验证类型不能为空");
			}
			if (StringUtils.isEmptyString(verifcode)) {
				throw new BusinessException("验证码不能为空");
			}

			String loginPartyId = this.getLoginPartyId();
			Party party = this.partyService.cachePartyBy(loginPartyId, false);
			SecUser secUser = this.secUserService.findUserByPartyId(loginPartyId);

			// 根据验证类型获取验证key verifcode_type: 1/手机;2/邮箱;3/谷歌验证器;
			String key = "";
			String errMsg = "";
			if ("1".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone();
				errMsg = "未绑定手机号";
			} else if ("2".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail();
				errMsg = "未绑定邮箱";
			} else if ("3".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret();
				errMsg = "未绑定谷歌验证器";
			}
			if (StringUtils.isEmptyString(key)) {
				throw new BusinessException(errMsg);
			}

			// 验证
			boolean passed = false;
			if ("1".equals(verifcode_type) || "2".equals(verifcode_type)) {
				String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
				if ((null != authcode) && (authcode.equals(verifcode))) {
					passed = true;
					this.identifyingCodeTimeWindowService.delAuthCode(key);
				}
			} else if ("3".equals(verifcode_type)) {
				if (this.googleAuthService.checkCode(key, verifcode)) {
					passed = true;
				}
			}

			// 如果是演示用户，则不判断验证码
			if (!"GUEST".contentEquals(party.getRolename())) {
				if (!passed) {
					throw new BusinessException("验证码不正确");
				}
			}

			// 更新密码
			this.secUserService.updatePassword(secUser.getUsername(), password);

//			更新密码以后记录 操作日志
			Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(secUser.getUsername());
			log.setPartyId(secUser.getPartyId());
			log.setLog("用户[" + secUser.getUsername()+ "]密码修改，调用接口为：api/user!updatepsw.action");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 修改登录密码 用旧密码
	 */
	@RequestMapping(action + "updateOldAndNewPsw.action")
	public Object updateOldAndNewPsw(HttpServletRequest request) {
		String old_password = request.getParameter("old_password");
		String password = request.getParameter("password");
		String re_password = request.getParameter("re_password");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {

			if (StringUtils.isEmptyString(old_password)) {
				throw new BusinessException("旧密码不能为空");
			}
			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("新密码不能为空");
			}
			if (StringUtils.isEmptyString(re_password)) {
				throw new BusinessException("新密码确认不能为空");
			}

			if (old_password.length() < 6 || old_password.length() > 12 || password.length() < 6 || password.length() > 12) {
				throw new BusinessException("密码必须6-12位");
			}

			SecUser secUser = this.secUserService.findUserByPartyId(this.getLoginPartyId());

			if (!this.passwordEncoder.encodePassword(old_password, secUser.getUsername()).equals(secUser.getPassword())) {
				throw new BusinessException("旧密码不正确");
			}

			if (!password.equals(re_password)) {
				throw new BusinessException("新密码不一致");
			}

			this.secUserService.updatePassword(secUser.getUsername(), password);

			Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(secUser.getUsername());
			log.setPartyId(secUser.getPartyId());
			log.setLog("用户[" + secUser.getUsername()+ "]密码修改：调用接口为：api/user!updateOldAndNewPsw.action");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 重置登录密码
	 */
	@RequestMapping(action + "resetpsw.action")
	public Object resetpsw(HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String verifcode_type = request.getParameter("verifcode_type");
		String verifcode = request.getParameter("verifcode");

		ResultObject resultObject = new ResultObject();

		try {

			if (StringUtils.isEmptyString(username)) {
				throw new BusinessException("用户名不能为空");
			}
			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("密码不能为空");
			}
			if (password.length() < 6 || password.length() > 12) {
				throw new BusinessException("密码必须6-12位");
			}
			if (StringUtils.isEmptyString(verifcode_type)) {
				throw new BusinessException("验证类型不能为空");
			}
			if (StringUtils.isEmptyString(verifcode)) {
				throw new BusinessException("验证码不能为空");
			}

			Party party = this.partyService.findPartyByUsername(username);
			if (null == party) {
				throw new BusinessException("用户名不存在");
			}
			SecUser secUser = this.secUserService.findUserByPartyId(party.getId().toString());

			// 根据验证类型获取验证key verifcode_type: 1/手机;2/邮箱;3/谷歌验证器;
			String key = "";
			String errMsg = "";
			if ("1".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone();
				errMsg = "未绑定手机号";
			} else if ("2".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail();
				errMsg = "未绑定邮箱";
			} else if ("3".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret();
				errMsg = "未绑定谷歌验证器";
			}
			if (StringUtils.isEmptyString(key)) {
				throw new BusinessException(errMsg);
			}

			// 验证
			boolean passed = false;
			if ("1".equals(verifcode_type) || "2".equals(verifcode_type)) {
				String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
				if ((null != authcode) && (authcode.equals(verifcode))) {
					passed = true;
					this.identifyingCodeTimeWindowService.delAuthCode(key);
				}
			} else if ("3".equals(verifcode_type)) {
				if (this.googleAuthService.checkCode(key, verifcode)) {
					passed = true;
				}
			}

			// 如果是演示用户，则不判断验证码
			if (!"GUEST".contentEquals(party.getRolename())) {
				if (!passed) {
					throw new BusinessException("验证码不正确");
				}
			}

			// 更新密码
			this.secUserService.updatePassword(username, password);

			Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(secUser.getUsername());
			log.setPartyId(secUser.getPartyId());
			log.setLog("用户[" + secUser.getUsername()+ "]密码修改,调用接口为：api/user!resetpsw.action");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}
	
//	/**
//	 * 三方接口重置登录密码
//	 */
//	@RequestMapping(action + "api_updatepassword.action")
//	public Object api_updatepassword(HttpServletRequest request) {
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//		String sign = request.getParameter("sign");
//
//		ResultObject resultObject = new ResultObject();
//
//		try {
//			
//			String key = this.sysparaService.find("api_rechargedeposit_key").getValue();
//			
//			if (StringUtils.isNullOrEmpty(key)) {
//				throw new BusinessException("三方接口未开放");
//			}
//			if (StringUtils.isEmptyString(username)) {
//				throw new BusinessException("用户名不能为空");
//			}
//			if (StringUtils.isEmptyString(password)) {
//				throw new BusinessException("登录密码不能为空");
//			}
//			if (StringUtils.isEmptyString(sign)) {
//				throw new BusinessException("加密串不能为空");
//			}
//
//			String _sign = project.blockchain.MD5.sign("username=" + username + "&password=" + password + "&key=" + key);
//
//			if (!_sign.equals(sign)) {
//				resultObject.setCode("1");
//				resultObject.setMsg("加密串校验失败");
//				return resultObject;
//			}
//
//			SecUser secUser = this.secUserService.findUserByLoginName(username);
//			if (null == secUser) {
//				throw new BusinessException("用户名不存在");
//			}
//
//			this.secUserService.updatePassword(username, password);
//			
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Throwable t) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", t);
//		}
//		
//		return resultObject;
//	}

	/**
	 * 设置资金密码（注册时）
	 */
	@RequestMapping(action + "setSafewordReg.action")
	public Object setSafewordReg(HttpServletRequest request) {
		String safeword = request.getParameter("safeword");

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			
			if (StringUtils.isEmptyString(safeword)) {
				throw new BusinessException("资金密码不能为空");
			}
			if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
				throw new BusinessException("资金密码不符合设定");
			}

			String loginPartyId = this.getLoginPartyId();
			Party party = this.partyService.cachePartyBy(loginPartyId, false);

			this.partyService.updateSafeword(party, safeword);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 修改资金密码 用验证码
	 */
	@RequestMapping(action + "setSafeword.action")
	public Object setSafeword(HttpServletRequest request) {
		String safeword = request.getParameter("safeword");
		String verifcode_type = request.getParameter("verifcode_type");
		String verifcode = request.getParameter("verifcode");

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			
			if (StringUtils.isEmptyString(safeword)) {
				throw new BusinessException("资金密码不能为空");
			}
			if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
				throw new BusinessException("资金密码不符合设定");
			}
			
			if (StringUtils.isEmptyString(verifcode_type)) {
				throw new BusinessException("验证类型不能为空");
			}
			if (StringUtils.isEmptyString(verifcode)) {
				throw new BusinessException("验证码不能为空");
			}

			String loginPartyId = this.getLoginPartyId();
			Party party = this.partyService.cachePartyBy(loginPartyId, false);
			SecUser secUser = this.secUserService.findUserByPartyId(loginPartyId);

			// 根据验证类型获取验证key verifcode_type: 1/手机;2/邮箱;3/谷歌验证器;
			String key = "";
			String errMsg = "";
			if ("1".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone();
				errMsg = "未绑定手机号";
			} else if ("2".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail();
				errMsg = "未绑定邮箱";
			} else if ("3".equals(verifcode_type)) {
				key = StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret();
				errMsg = "未绑定谷歌验证器";
			}
			if (StringUtils.isEmptyString(key)) {
				throw new BusinessException(errMsg);
			}

			// 验证
			boolean passed = false;
			if ("1".equals(verifcode_type) || "2".equals(verifcode_type)) {
				String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
				if ((null != authcode) && (authcode.equals(verifcode))) {
					passed = true;
					this.identifyingCodeTimeWindowService.delAuthCode(key);
				}
			} else if ("3".equals(verifcode_type)) {
				if (this.googleAuthService.checkCode(key, verifcode)) {
					passed = true;
				}
			}

			// 如果是演示用户，则不判断验证码
			if (!"GUEST".contentEquals(party.getRolename())) {
				if (!passed) {
					throw new BusinessException("验证码不正确");
				}
			}

			// 更新密码
			this.partyService.updateSafeword(party, safeword);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 修改资金密码 用旧密码
	 */
	@RequestMapping(action + "updateOldAndNewSafeword.action")
	public Object updateOldAndNewSafeword(HttpServletRequest request) {
		String old_safeword = request.getParameter("old_safeword");
		String safeword = request.getParameter("safeword");
		String re_safeword = request.getParameter("re_safeword");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {		
			if (StringUtils.isEmptyString(old_safeword)) {
				throw new BusinessException("旧密码不能为空");
			}
			if (StringUtils.isEmptyString(safeword)) {
				throw new BusinessException("新密码不能为空");
			}

			if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
				throw new BusinessException("资金密码不符合设定");
			}
			
			if (StringUtils.isEmptyString(re_safeword)) {
				throw new BusinessException("新密码确认不能为空");
			}

			String loginPartyId = this.getLoginPartyId();

			if (!this.partyService.checkSafeword(old_safeword, loginPartyId)) {
				throw new BusinessException("旧密码不正确");
			}

			if (!safeword.equals(re_safeword)) {
				throw new BusinessException("新密码不一致");
			}

			Party party = this.partyService.cachePartyBy(loginPartyId, false);
			this.partyService.updateSafeword(party, safeword);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

//	/**
//	 * 三方接口重置资金密码
//	 */
//	@RequestMapping(action + "api_updatesafeword.action")
//	public Object api_updatesafeword(HttpServletRequest request) {
//		String username = request.getParameter("username");
//		String safeword = request.getParameter("safeword");
//		String sign = request.getParameter("sign");
//		String password = request.getParameter("password");
//		
//		ResultObject resultObject = new ResultObject();
//
//		try {
//			
//			String key = this.sysparaService.find("api_rechargedeposit_key").getValue();
//			
//			if (StringUtils.isEmptyString(key)) {
//				throw new BusinessException("三方接口未开放");
//			}
//			if (StringUtils.isEmptyString(username)) {
//				throw new BusinessException("用户名不能为空");
//			}
//			if (StringUtils.isEmptyString(safeword)) {
//				throw new BusinessException("资金密码不能为空");
//			}
//			if (StringUtils.isEmptyString(sign)) {
//				throw new BusinessException("加密串不能为空");
//			}
//
//			String _sign = project.blockchain.MD5.sign("username=" + username + "&safeword=" + safeword + "&key=" + key);
//
//			if (!_sign.equals(sign)) {
//				resultObject.setCode("1");
//				resultObject.setMsg("加密串校验失败");
//				return resultObject;
//			}
//
//			SecUser secUser = this.secUserService.findUserByLoginName(username);
//			if (null == secUser) {
//				throw new BusinessException("用户名不存在");
//			}
//
//			this.secUserService.updatePassword(username, password);
//			
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Throwable t) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", t);
//		}
//		
//		return resultObject;
//	}

	/**
	 * 人工重置申请  操作类型 operate:	 0/修改资金密码；1/取消谷歌绑定；2/取消手机绑定；3/取消邮箱绑定；
	 */
	@RequestMapping(action + "set_safeword_apply.action")
	public Object set_safeword_apply(HttpServletRequest request) {
		String idcard_path_front = request.getParameter("idcard_path_front");
		String idcard_path_back = request.getParameter("idcard_path_back");
		String idcard_path_hold = request.getParameter("idcard_path_hold");
		String safeword = request.getParameter("safeword");
		String safeword_confirm = request.getParameter("safeword_confirm");
		String operate = request.getParameter("operate");
		String remark = request.getParameter("remark");
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			if (StringUtils.isNullOrEmpty(operate)) {
				throw new BusinessException("操作类型为空");
			}
			if (!StringUtils.isInteger(operate)) {
				throw new BusinessException("操作类型不是整数");
			}
			if (Integer.valueOf(operate).intValue() < 0) {
				throw new BusinessException("操作类型不能小于0");
			}
			
			Integer operate_int = Integer.valueOf(operate);
			
			this.userSafewordApplyService.saveApply(this.getLoginPartyId(), idcard_path_front, idcard_path_back, idcard_path_hold, safeword, safeword_confirm, operate_int, remark);
			
		} catch (BusinessException e) {
			if (401 == e.getSign()) {
				resultObject.setCode("401");
			} else {
				resultObject.setCode("1");				
			}
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}
	
	/**
	 * 获取 人工重置 信息
	 */
	@RequestMapping(action + "get_safeword_apply.action")
	public Object get_safeword_apply() {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
			
			List<UserSafewordApply> list = this.userSafewordApplyService.findByPartyId(this.getLoginPartyId());
			for (int i = 0; i < list.size(); i++) {
				retList.add(this.userSafewordApplyService.bindOne(list.get(i)));
			}
			
			resultObject.setData(retList);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}
	
	/**
	 * token获取验证方式
	 */
	@RequestMapping(action + "getVerifTarget.action")
	public Object getVerifTarget(HttpServletRequest request) {
		String verifcode_type = request.getParameter("verifcode_type");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		Map<String, Object> data = new HashMap<>();

		try {

			String loginPartyId = this.getLoginPartyId();
			Party party = this.partyService.cachePartyBy(loginPartyId, false);
			SecUser secUser = this.secUserService.findUserByPartyId(loginPartyId);

			// verifcode_type未明确指定，返回所有的方式
			if (StringUtils.isEmptyString(verifcode_type) || !Arrays.asList("1", "2", "3").contains(verifcode_type)) {
				data.put("phone", StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone());
				data.put("phone_filled", StringUtils.isEmptyString(party.getPhone()) ? "" : party.getPhone());
				data.put("phone_authority", party.getPhone_authority());
				data.put("email", StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail());
				data.put("email_filled", StringUtils.isEmptyString(party.getEmail()) ? "" : party.getEmail());
				data.put("email_authority", party.getEmail_authority());
				data.put("google_auth_secret", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret());
				data.put("google_auth_secret_filled", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) ? "" : secUser.getGoogle_auth_secret());
				data.put("google_auth_bind", secUser.isGoogle_auth_bind());
			} else {
				// verifcode_type: 1/手机;2/邮箱;3/谷歌验证器;
				if ("1".equals(verifcode_type)) {
					data.put("phone", StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone());
					data.put("phone_filled", StringUtils.isEmptyString(party.getPhone()) ? "" : party.getPhone());
					data.put("phone_authority", party.getPhone_authority());
				} else if ("2".equals(verifcode_type)) {
					data.put("email", StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail());
					data.put("email_filled", StringUtils.isEmptyString(party.getEmail()) ? "" : party.getEmail());
					data.put("email_authority", party.getEmail_authority());
				} else if ("3".equals(verifcode_type)) {
					data.put("google_auth_secret", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret());
					data.put("google_auth_secret_filled", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) ? "" : secUser.getGoogle_auth_secret());
					data.put("google_auth_bind", secUser.isGoogle_auth_bind());
				}
			}

			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 用户名获取验证方式
	 */
	@RequestMapping(action + "getUserNameVerifTarget.action")
	public Object getUserNameVerifTarget(HttpServletRequest request) {
		String username = request.getParameter("username");
		String verifcode_type = request.getParameter("verifcode_type");

		ResultObject resultObject = new ResultObject();

		try {

			Map<String, Object> data = new HashMap<>();

			if (StringUtils.isEmptyString(username)) {
				throw new BusinessException("用户名参数为空");
			}

			Party party = this.partyService.findPartyByUsername(username);
			if (null == party) {
				throw new BusinessException("用户名不存在");
			}
			
			SecUser secUser = this.secUserService.findUserByPartyId(party.getId().toString());

			// verifcode_type未明确指定，返回所有的方式
			if (StringUtils.isEmptyString(verifcode_type) || !Arrays.asList("1", "2", "3").contains(verifcode_type)) {
				data.put("phone", StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone());
				data.put("phone_filled", StringUtils.isEmptyString(party.getPhone()) ? "" : party.getPhone());
				data.put("phone_authority", party.getPhone_authority());
				data.put("email", StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail());
				data.put("email_filled", StringUtils.isEmptyString(party.getEmail()) ? "" : party.getEmail());
				data.put("email_authority", party.getEmail_authority());
				data.put("google_auth_secret", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret());
				data.put("google_auth_secret_filled", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) ? "" : secUser.getGoogle_auth_secret());
				data.put("google_auth_bind", secUser.isGoogle_auth_bind());
			} else {
				// verifcode_type: 1/手机;2/邮箱;3/谷歌验证器;
				if ("1".equals(verifcode_type)) {
					data.put("phone", StringUtils.isEmptyString(party.getPhone()) || false == party.getPhone_authority() ? "" : party.getPhone());
					data.put("phone_filled", StringUtils.isEmptyString(party.getPhone()) ? "" : party.getPhone());
					data.put("phone_authority", party.getPhone_authority());
				} else if ("2".equals(verifcode_type)) {
					data.put("email", StringUtils.isEmptyString(party.getEmail()) || false == party.getEmail_authority() ? "" : party.getEmail());
					data.put("email_filled", StringUtils.isEmptyString(party.getEmail()) ? "" : party.getEmail());
					data.put("email_authority", party.getEmail_authority());
				} else if ("3".equals(verifcode_type)) {
					data.put("google_auth_secret", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) || false == secUser.isGoogle_auth_bind() ? "" : secUser.getGoogle_auth_secret());
					data.put("google_auth_secret_filled", StringUtils.isEmptyString(secUser.getGoogle_auth_secret()) ? "" : secUser.getGoogle_auth_secret());
					data.put("google_auth_bind", secUser.isGoogle_auth_bind());
				}
			}

			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	@RequestMapping(action + "findNameByUid.action")
	public Object findNameByUid(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			Map<String, Object> map = new HashMap<String, Object>();

			Party party = this.partyService.findPartyByUsercode(usercode);
			if (party != null) {
				
				String username = party.getUsername();
				char[] username_char = username.toCharArray();
				
				StringBuffer sb = new StringBuffer();
				
				int half_2 = (int) (Arith.div(username_char.length, 2) - 2);
				int half_big_2 = (int) (Arith.div(username_char.length, 2) + 2);
				
				for (int i = 0; i < username_char.length; i++) {
					String aa = "";
					if (i >= half_2 && i < half_big_2) {
						aa = String.valueOf(username_char[i]).replaceAll(String.valueOf(username_char[i]), "*");
					} else {
						aa = String.valueOf(username_char[i]);
					}
					sb.append(aa);
				}

				map.put("username", sb.toString());
			}

			resultObject.setData(map);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}
	
	/**
	 * 通讯录
	 */
	@RequestMapping(action + "login_extend.action")
	public Object login_extend(HttpServletRequest request) {
		String login_extend = request.getParameter("login_extend");
		
		ResultObject resultObject = new ResultObject();
		
		try {
			
			String contacts_url = this.sysparaService.find("contacts_url").getValue();
			if (StringUtils.isEmptyString(contacts_url)) {
				throw new BusinessException("系统参数错误");
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("login_extend", login_extend);
			
			String result = HttpHelper.getJSONFromHttp(contacts_url, param, HttpMethodType.POST);
			
			JSONObject resultJson = JSON.parseObject(result);
			if (null == resultJson) {
				logger.error("login_extend.action，通讯录上传失败：login_extend -> " + login_extend);
//				throw new BusinessException("通讯录上传失败");
				throw new BusinessException("程序错误");
			}
			
			Integer code = resultJson.getInteger("code");
			
			if (null != code && 200 == code.intValue()) {
				resultObject.setCode("0");
				resultObject.setMsg("操作成功");
			} else {
				resultObject.setCode("1");
				resultObject.setMsg("程序错误");
				logger.error("login_extend.action，通讯录上传失败：login_extend -> " + login_extend);
			}

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	/**
	 * 检查资金密码
	 */
	@RequestMapping(action + "check-safeword.action")
	public Object checkSafeword(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		Map<String, Object> data = new HashMap<String, Object>();
		String partyId = this.getLoginPartyId();
		Party party = partyService.cachePartyBy(partyId, false);

		String partySafeword = party.getSafeword();
		int safeword = 0;
		if(StringUtils.isNotEmpty(partySafeword)){
			safeword = 1;
		}
		data.put("safeword", safeword);

		resultObject.setData(data);
		return resultObject;
	}


	/**
	 *
	 */
	@PostMapping(action + "logoff.action")
	public Object logoffAccount(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String token = request.getParameter("token");
		String account = request.getParameter("account");
		String reason = request.getParameter("reason");
		// 资金明文密码
		String cashPassword = request.getParameter("cashPassword");
		String lang = this.getLanguage(request);
		String partyId = this.getLoginPartyId();

		String errMsg = "注销成功";
		if (lang.equals("en")) {
			errMsg = "Logoff Account fail";
		}
		resultObject.setMsg(errMsg);
		if (StrUtil.isBlank(partyId)) {
			if (lang.equals("en")) {
				errMsg = "please login first";
			} else {
				errMsg = "请先登录";
			}
			resultObject.setCode("1");
			resultObject.setMsg("请先登录");
			return resultObject;
		}

		try {
			SecUser userEntity = secUserService.findUserByPartyId(partyId);
			Party party = partyService.getById(partyId);
			if (null == userEntity) {
				if (lang.equals("en")) {
					errMsg = "user not exist";
				} else {
					errMsg = "用户不存在";
				}
				throw new BusinessException("用户不存在");
			}
			if ("SROOT".equals(userEntity.getId().toString()) || "SADMIN".equals(userEntity.getId().toString())) {
				throw new BusinessException("该角色账号不允许注销");
			}
			if (StrUtil.isBlank(account) || !Objects.equals(account.trim(), userEntity.getUsername())) {
				errMsg = "当前的用户账号与确认注销的账号不一致";
				throw new BusinessException("当前的用户账号与确认注销的账号不一致");
			}

			// 校验资金密码
			checkCashPassword(party, cashPassword, lang);

			List<Withdraw> unfinishedWithdrawList = withdrawService.selectUnFinishedWithdraw(partyId);
			if (CollectionUtil.isNotEmpty(unfinishedWithdrawList)) {
				errMsg = "当前账号有未完成的提现订单，请完成后再试";
				throw new BusinessException(errMsg);
			}

			List<RechargeBlockchain> unfinishedRechargeList = rechargeBlockchainService.selectUnFinishedRecharge(partyId);
			if (CollectionUtil.isNotEmpty(unfinishedRechargeList)) {
				errMsg = "当前账号有未完成的充值订单，请完成后再试";
				throw new BusinessException(errMsg);
			}

			if (null != token && token.length() <= 36) {
				this.userService.logout(partyId);
				this.tokenService.delete(token);
			} else {
				this.tokenService.removePlatFromToken(token);
			}

			this.userService.updateLogoffAccount(partyId, reason);
			// 优化：放进一个被数据库事务包围的方法里，防止异常情况下数据不一致
//			String logoffSufix = ":off:" + (System.currentTimeMillis() / 1000L);
//			String oriAccount = userEntity.getUsername();
//			String newAccount = oriAccount + logoffSufix;
//			if (newAccount.length() > 64) {
//				newAccount = newAccount.substring(0, 64);
//			}
//
//			String newEmail = party.getEmail();
//			if (StrUtil.isNotBlank(newEmail)) {
//				newEmail = newEmail + ":off";
//				if (newEmail.length() > 64) {
//					newEmail = "";
//				}
//			}
//
//			String newPhone = party.getPhone();
//			if (StrUtil.isNotBlank(newPhone)) {
//				newPhone = newPhone + ":off";
//			}
//
//			//
//			userEntity.setUsername(newAccount);
//			userEntity.setEnabled(false);
//			userEntity.setEmail(newEmail);
//			userEntity.setRemarks(reason);
//			secUserService.update(userEntity);
//
//			party.setUsername(newAccount);
//			party.setPhone(newPhone);
//			party.setEmail(newEmail);
//			party.setEnabled(false);
//			partyService.update(party);
//
//			// 清掉缓存，可用于支持手动改数据库记录恢复账号
//			redisHandler.remove(PartyRedisKeys.PARTY_ID + party.getId());
//			redisHandler.remove(PartyRedisKeys.PARTY_USERNAME + party.getUsername());
//			redisHandler.remove(PartyRedisKeys.PARTY_USERNAME + oriAccount);
//
//			Seller seller = sellerService.getSeller(party.getId().toString());
//			if (seller != null) {
//				seller.setStatus(0);
//				seller.setName(seller.getName() + logoffSufix);
//				sellerService.updateSeller(seller);
//			}
//
//			// 发布事件，可能其他业务也需要修改相关的字段
//			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
//			LogoffAccountInfo info = new LogoffAccountInfo();
//			info.setPartyId(partyId);
//			info.setOriAccount(oriAccount);
//			info.setNewAccount(newAccount);
//			wac.publishEvent(new LogoffAccountEvent(this, info));
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			if (lang.equals("en")) {
				errMsg = "fail";
			} else {
				errMsg = "注销失败";
			}
			resultObject.setMsg("注销失败");
			logger.error("error:", t);
		}

		return resultObject;
	}


	/**
	 *
	 */
	@PostMapping(action + "bindWithdrawAddress.action")
	public Object bindWithdrawAddress(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		// 示例：TRC20
		String chainName = request.getParameter("blockchain_name");
		// 示例：USDT, ETH
		String coin = request.getParameter("coin");
		String withdrawAddress = request.getParameter("channel_address");
		// 资金明文密码
		//String cashPassword = request.getParameter("cashPassword");
		String lang = this.getLanguage(request);
		String partyId = this.getLoginPartyId();
		if (StrUtil.isBlank(partyId)) {
			resultObject.setCode("1");
			resultObject.setMsg("请先登录");
			return resultObject;
		}

		String errMsg = "";
		if (StrUtil.isBlank(withdrawAddress) || Objects.equals(withdrawAddress, "null")) {
			errMsg = "未设置地址";
		}
		if (StrUtil.isBlank(chainName)) {
			errMsg = "未指定币种协议";
		}
		if (StrUtil.isNotBlank(errMsg)) {
			resultObject.setCode("1");
			resultObject.setMsg(errMsg);
			return resultObject;
		}
		withdrawAddress = withdrawAddress.trim();
		chainName = chainName.trim();

		errMsg = "地址格式与协议不匹配";
		if (chainName.equalsIgnoreCase("ERC20")) {
			if (!withdrawAddress.startsWith("0x")) {
				resultObject.setCode("1");
				resultObject.setMsg(errMsg);
				return resultObject;
			}
		} else if (chainName.equalsIgnoreCase("TRC20")) {
			if (!withdrawAddress.startsWith("T")) {
				resultObject.setCode("1");
				resultObject.setMsg(errMsg);
				return resultObject;
			}
		} else if (chainName.equalsIgnoreCase("OMNI")) {
			if (!withdrawAddress.startsWith("1") && !withdrawAddress.startsWith("3")) {
				resultObject.setCode("1");
				resultObject.setMsg(errMsg);
				return resultObject;
			}
		} else {
			resultObject.setCode("1");
			resultObject.setMsg("暂不支持该协议");
			return resultObject;
		}

		Party party = this.partyService.cachePartyBy(partyId, false);
		if (StrUtil.isNotBlank(party.getWithdrawAddress()) && !Objects.equals(party.getWithdrawAddress(), "0")) {
			// 通过其他方式修改已经绑定的提现地址，不能在此处直接修改
			resultObject.setCode("1");
			resultObject.setMsg("用户已绑定过提现地址");
			return resultObject;
		}

		party.setWithdrawAddress(withdrawAddress);
		party.setWithdrawCoinType(coin.trim());
		party.setWithdrawChainName(chainName);
		this.partyService.update(party);
		resultObject.setCode("0");
		resultObject.setMsg("绑定提现地址成功");
		return resultObject;
	}

	/**
	 * 校验资金密码
	 *
	 * @param party
	 * @param inputPassword
	 */
	private void checkCashPassword(Party party, String inputPassword, String lang) {
		String partyId = party.getId().toString();
		String partySafeword = party.getSafeword();
		String errMsg = "fail";
		if (StringUtils.isEmptyString(partySafeword)) {
//			if (lang.equals("en")) {
//				errMsg = "please set cash password";
//			} else {
//				errMsg = "请设置资金密码";
//			}
//			throw new BusinessException(999, errMsg);
			return;
		}

		String errorPassCount = sysparaService.find("number_of_wrong_passwords").getValue();
		if (Objects.isNull(errorPassCount)) {
			logger.error("number_of_wrong_passwords 系统参数未配置！");
			if (lang.equals("en")) {
				errMsg = "invalid input parameters";
			} else {
				errMsg = "参数异常";
			}
			throw new BusinessException("参数异常");
		}
		int maxCount = Integer.parseInt(errorPassCount);

		String lockPassworkErrorKey = MallRedisKeys.MALL_PASSWORD_ERROR_LOCK + partyId;
		int needSeconds = util.DateUtils.getTomorrowStartSeconds();
		boolean exit = redisHandler.exists(lockPassworkErrorKey);//是否已经错误过
		if (exit && ("true".equals(redisHandler.getString(lockPassworkErrorKey)))) {//已经尝试错误过且次数已经超过number_of_wrong_passwords配置的次数
			if (lang.equals("en")) {
				errMsg = "Fail to try cash password, Please try tomorrow";
			} else {
				errMsg = "密码输入错误次数过多，请明天再试";
			}
			throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
		} else if (exit && maxCount <= Integer.parseInt(redisHandler.getString(lockPassworkErrorKey))) {//已经尝试密码错误过且次数刚好等于number_of_wrong_passwords配置的次数
			redisHandler.setSyncStringEx(lockPassworkErrorKey, "true", needSeconds);
			if (lang.equals("en")) {
				errMsg = "Fail to try cash password, Please try tomorrow";
			} else {
				errMsg = "密码输入错误次数过多，请明天再试";
			}
			throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
		} else {//失败次数小于配置次数或者未失败
			boolean checkSafeWord = this.partyService.checkSafeword(inputPassword, partyId);
			if (checkSafeWord) {//交易密码校验成功
				redisHandler.remove(lockPassworkErrorKey);
			} else {//交易密码校验失败
				if (exit) {//已经失败过，执行加1操作
					redisHandler.incr(lockPassworkErrorKey);
				} else {//未失败，set值，并计1
					redisHandler.setSyncStringEx(lockPassworkErrorKey, "1", needSeconds);
				}

				if (lang.equals("en")) {
					errMsg = "Cash password not right";
				} else {
					errMsg = "资金密码错误";
				}
				throw new BusinessException(1, "资金密码错误");
			}
		}
	}

}
