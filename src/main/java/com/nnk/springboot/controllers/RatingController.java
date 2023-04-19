package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
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
public class RatingController {

    Logger logger = LoggerFactory.getLogger(BidListController.class);

    // TODO: Inject Rating service
    @Autowired
    RatingRepository ratingRepository;

    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        // TODO: find all Rating, add to model
        model.addAttribute("ratings", ratingRepository.findAll());
        logger.info("REQUEST:/rating/list");
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        logger.info("GET:/rating/add");
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Validated Rating rating, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Rating list
        if (!result.hasErrors()) {
            ratingRepository.save(rating);
            model.addAttribute("ratings", ratingRepository.findAll());
            logger.info("redirect:/rating/list");
            return "redirect:/rating/list";
        }
        logger.info("POST:/rating/add");
        return "rating/add";
    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Rating by Id and to model then show to the form

        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item id:" + id));
        model.addAttribute("rating", rating);
        logger.info("GET:/rating/update");
        return "rating/update";
    }

    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Validated Rating rating,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Rating and return Rating list
        if (result.hasErrors()) {
            logger.info("POST:/rating/update");
            return "rating/update";
        }

        rating.setId(id);
        ratingRepository.save(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        logger.info("redirect:/rating/list");
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Rating by Id and delete the Rating, return to Rating list
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Invalid item id:" + id));
        ratingRepository.delete(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        logger.info("GET:/rating/delete");
        return "redirect:/rating/list";
    }
}
