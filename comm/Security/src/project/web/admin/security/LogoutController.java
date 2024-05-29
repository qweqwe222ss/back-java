package project.web.admin.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.web.BaseAction;

/**
 * 后台管理系统退出登录
 *
 */
@RestController
public class LogoutController extends BaseAction {

	@RequestMapping(value = "public/logout.action") 
	public ModelAndView Logout(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		
		HttpSession session = request.getSession();
		
		session.setAttribute("SPRING_SECURITY_CONTEXT", null);
		
		model.setViewName("login");
		return model;
	}
}
