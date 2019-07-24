package com.harsha.springsecurity.controller;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.harsha.eshopbackend.daoimpl.UserDaoImpl;
import com.harsha.eshopbackend.model.User;

@Controller
public class UserController {

	@Autowired
	UserDaoImpl userDaoImpl;
	
	@RequestMapping(value="register", method=RequestMethod.GET)
	public ModelAndView goToRegForm()
	{
	
		User user=new User();
		ModelAndView  modelAndView=new ModelAndView("register");
		modelAndView.addObject("user",user);
		return modelAndView;
	}
	
	@RequestMapping(value="register", method=RequestMethod.POST)
	public ModelAndView recieveRegFormData(@ModelAttribute("user") User user)
	{
	
		ModelAndView  modelAndView=new ModelAndView("userhome");
		userDaoImpl.addUser(user);
		return modelAndView;
	}
	
	//
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public ModelAndView goToLoginForm()
	{
	
		User  user=new User();
		
		ModelAndView  modelAndView=new ModelAndView("login");
		modelAndView.addObject("user",user);
		
		return modelAndView;
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public ModelAndView recieveLoginFormData(@ModelAttribute("user") User user)
	{
	boolean result=userDaoImpl.checkLogin(user);
	
	if(result==true)
	{
		ModelAndView  modelAndView=new ModelAndView("loginsuccess");
		return modelAndView;
	}
	else
	{
		ModelAndView  modelAndView=new ModelAndView("login");
		modelAndView.addObject("logininfo","wrong username/password");
		
		return modelAndView;
		
	}
	
		


	}
	
	//-====================
	
	@RequestMapping("/loginerror")
	public ModelAndView afterLoginFailure()
	{
		//go to login.jsp with a message
		
		ModelAndView modelAndView=new ModelAndView("login");
		modelAndView.addObject("user",new User());
		modelAndView.addObject("loginerrormsg","invalid user");
		return  modelAndView;
		
	}
	
	@RequestMapping("/afterlogin")
	public String afterLoginSuccess(HttpSession session)
	{
		String pageName="";
		System.out.println("in controller after login success");
		
		//hold user name for future purpose
		SecurityContext securityContext=SecurityContextHolder.getContext();
		Authentication authentication=securityContext.getAuthentication();
		String userId=authentication.getName();
		
		//push login user name into session
		session.setAttribute("userName",userId);
		
		System.out.println("logged in user"+userId);
		
		//check role
		Collection<GrantedAuthority> authorities=(Collection<GrantedAuthority>)authentication.getAuthorities();
		
		for(GrantedAuthority authority:authorities)
		{
			System.out.println("login user authority"+authority);
			String loginauthority=authority.getAuthority();
			if(loginauthority.equals("ROLE_USER"))
			{
				System.out.println("role is user");
				pageName="userhome";
			}
			else
				if(loginauthority.equals("ROLE_ADMIN"))
				{
					pageName="home";
				}
		}
		//if role is user go userhome
		
		//if role is admin home
		System.out.println(pageName);
		return pageName;
	}
	
}
