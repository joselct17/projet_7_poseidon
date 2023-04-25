package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.CurvePointRepository;
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
public class CurveController {

    Logger logger = LoggerFactory.getLogger(CurveController.class);

    // TODO: Inject Curve Point service
    @Autowired
    CurvePointRepository curvePointRepository;

    @RequestMapping("/curvePoint/list")
    public String home(Model model)
    {
        // TODO: find all Curve Point, add to model
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        logger.info("GET/curvePoint/list");
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addCurveForm(CurvePoint bid) {
        logger.info("GET/curvePoint/add");
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Validated CurvePoint curvePoint, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Curve list
        if (!result.hasErrors()) {
            curvePointRepository.save(curvePoint);
            model.addAttribute("curvePoints", curvePointRepository.findAll());
            logger.info("redirect:/curvePoint/list");
            return "redirect:/curvePoint/list";
        }
        logger.info("POST:/curvePoint/add");
        return "curvePoint/add";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get CurvePoint by Id and to model then show to the form
        CurvePoint curvePoint = curvePointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item id:" + id));
        model.addAttribute("curvePoint", curvePoint);
        logger.info("GET:/curvePoint/update");
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update/{id}")
    public String updateCurve(@PathVariable("id") Integer id, @Validated CurvePoint curvePoint,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Curve and return Curve list
        if (result.hasErrors()) {
            logger.info("POST:/curvePoint/update");
            return "curvePoint/update";
        }
        curvePoint.setId(id);
        curvePointRepository.save(curvePoint);
        model.addAttribute("curvePoint", curvePointRepository.findAll());
        logger.info("redirect:/curvePoint/list");
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurve(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Curve by Id and delete the Curve, return to Curve list
        CurvePoint curvePoint = curvePointRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Invalid item id:" + id));
        curvePointRepository.delete(curvePoint);
        model.addAttribute("ratings", curvePointRepository.findAll());
        logger.info("GET:/curvePoint/delete");
        return "redirect:/curvePoint/list";
    }
}
