package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class RuleNameController {

    Logger logger = LoggerFactory.getLogger(BidListController.class);
    // TODO: Inject RuleName service
    @Autowired
    RuleNameRepository ruleNameRepository;

    @RequestMapping("/ruleName/list")
    public String home(Model model)
    {
        // TODO: find all RuleName, add to model
        model.addAttribute("ruleNames", ruleNameRepository.findAll() );
        logger.info("REQUEST:/ruleName/list");
        return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleForm(RuleName bid) {
        logger.info("GET:/ruleName/add");
        return "ruleName/add";
    }

    @PostMapping("/ruleName/validate")
    public String validate(@Validated RuleName ruleName, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return RuleName list
        if (!result.hasErrors()) {
            ruleNameRepository.save(ruleName);
            model.addAttribute("ruleNames", ruleNameRepository.findAll());
            logger.info("redirect:/ruleName/list");
            return "redirect:/ruleName/list";
        }
        logger.info("POST:/ruleName/add");
        return "ruleName/add";
    }

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get RuleName by Id and to model then show to the form

        RuleName ruleName = ruleNameRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalide item id:" +id));
        model.addAttribute("ruleName", ruleName);
        logger.info("GET:/ruleName/update");
        return "ruleName/update";
    }

    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Validated RuleName ruleName,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update RuleName and return RuleName list
       if (result.hasErrors()) {
           logger.info("POST:/ruleName/update");
           return "ruleName/update";
       }

       ruleName.setId(id);
       ruleNameRepository.save(ruleName);
       model.addAttribute("ruleNames", ruleNameRepository.findAll());
       logger.info("redirect:/ruleName/list");
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model) {
        // TODO: Find RuleName by Id and delete the RuleName, return to Rule list

        RuleName ruleName = ruleNameRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Invalid Rule Name with id:"+ id));
        ruleNameRepository.delete(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        logger.info("GET:/ruleName/delete");
        return "redirect:/ruleName/list";
    }
}
