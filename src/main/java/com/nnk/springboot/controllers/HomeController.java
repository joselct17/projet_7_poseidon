package com.nnk.springboot.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HomeController
{
	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping("/")
	public String home(Model model, Principal user)
	{
		logger.info("GET:/home");
		return "home";
	}

	@RequestMapping("/admin/home")
	public String adminHome(Model model)
	{
		logger.info("REQUEST:/admin/home");
		return "redirect:/bidList/list";
	}


}
