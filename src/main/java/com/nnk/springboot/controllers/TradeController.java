package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TradeController {

    Logger logger = LoggerFactory.getLogger(TradeController.class);
    // TODO: Inject Trade service
    @Autowired
    TradeRepository tradeRepository;

    @RequestMapping("/trade/list")
    public String home(Model model)
    {
        // TODO: find all Trade, add to model
        model.addAttribute("trades", tradeRepository.findAll());
        logger.info("REQUEST:/trade/list");
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Trade bid) {
        logger.info("GET:/trade/add");
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Validated Trade trade, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Trade list
        if (!result.hasErrors()) {
            tradeRepository.save(trade);
            model.addAttribute("trades", tradeRepository.findAll());
            logger.info("redirect:/trade/list");
            return "redirect:/trade/list";
        }
        logger.info("POST:/trade/validate");
        return "trade/add";
    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Trade by Id and to model then show to the form
        Trade trade = tradeRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Invalid element id:" + id));
        model.addAttribute("trade", trade);
        logger.info("GET:/trade/update");
        return "trade/update";
    }

    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Validated Trade trade,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Trade and return Trade list
       if (result.hasErrors()) {
           logger.info("POST:/trade/update");
           return "trade/update";
       }
       trade.setId(id);
       tradeRepository.save(trade);
       model.addAttribute("trades", tradeRepository.findAll());
       logger.info("redirect:/trade/list");
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Trade by Id and delete the Trade, return to Trade list
        Trade trade = tradeRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid trade Id:" + id) );
        tradeRepository.delete(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        logger.info("GET:/trade/delete");
        return "redirect:/trade/list";
    }
}
