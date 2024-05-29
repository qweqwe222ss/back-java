package project.web.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.StringUtils;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import security.SecUser;
import security.web.LoginSuccessAction;

@RestController
public class LocalLoginSuccessController extends LoginSuccessAction {

	@Autowired
	LogService logService;

	@RequestMapping(value = "normal/LoginSuccessAction!view.action") 
	public ModelAndView loginSuccess() {
		
		ModelAndView model = new ModelAndView();

		String cookie_username = this.getCookie(COOKIE_USERNAME_NAME);

		if (!StringUtils.isNullOrEmpty(cookie_username) && cookie_username.length() >= 4000) {
			cookie_username = cookie_username.substring(0, 3999);
		}
		// super.view();
		String partyId = this.getLoginPartyId();
		
		if (!"root".equals(this.getUsername_login())) {
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("登录系统，ip[" + this.getIp(getRequest()) + "]");
			log.setPartyId(partyId);
			log.setUsername(this.getUsername_login());
			logService.saveAsyn(log);
		}
		
		model.addObject("username_login", this.getUsername_login());
		model.setViewName("auto_monitor_iframe");
		return model;
	}
	
	/**
	 * 将登录关联信息保存到cookies
	 */
	private void saveLoginCookies(String username) {
		username = username.replaceAll("\\s*", "");
		username = username.toLowerCase();

		String username_cookie = this.getCookie(COOKIE_USERNAME_NAME);
		boolean find = false;
		if (!StringUtils.isNullOrEmpty(username_cookie)) {
			String[] array = username_cookie.split(",");

			for (int i = 0; i < array.length; i++) {
				if (username.equals(array[i])) {
					find = true;
					break;
				}

			}

		}
		if (!find) {

			if (StringUtils.isNullOrEmpty(username_cookie)) {
				addCookie(COOKIE_USERNAME_NAME, username);
			} else {
				username = username_cookie + "," + username;
				addCookie(COOKIE_USERNAME_NAME, username);
			}
		}
	}

}
