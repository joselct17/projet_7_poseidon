package com.nnk.springboot;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.repositories.RatingRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Double.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RatingTests {


	@Mock
	private RatingRepository ratingRepositoryMock;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private Model model;
	@InjectMocks
	private RatingController ratingController;

	@Test
	public void ratingTest() {
		Rating rating = new Rating(1,"moodysRating", "sandPRating", "fitchRating", 10 );

		// Save
		rating = ratingRepository.save(rating);
		Assert.assertNotNull(rating.getId());
		Assert.assertTrue(rating.getOrderNumber() == 10);

		// Update
		rating.setOrderNumber(20);
		rating = ratingRepository.save(rating);
		Assert.assertTrue(rating.getOrderNumber() == 20);

		// Find
		List<Rating> listResult = ratingRepository.findAll();
		Assert.assertTrue(listResult.size() > 0);

		// Delete
		Integer id = rating.getId();
		ratingRepository.delete(rating);
		Optional<Rating> ratingList = ratingRepository.findById(id);
		Assert.assertFalse(ratingList.isPresent());
	}

	@Test
	public void testRatingList() {
		// Arrange
		when(ratingRepositoryMock.findAll()).thenReturn(new ArrayList<Rating>());

		// Act
		String viewName = ratingController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("ratings"), any());
		assertEquals("rating/list", viewName);
	}


	@Test
	public void testAddRating() throws Exception {
		// Create a mock rating object
		Rating rating = new Rating();
		rating.setId(1);
		rating.setFitchRating("Fitch test");
		rating.setOrderNumber(1);
		rating.setSandPRating("Sand test");

		// Perform a GET request to the /rating/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/rating/add").flashAttr("rating", rating)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "/rating/add"
		assertEquals("rating/add", result.getModelAndView().getViewName());
	}
}
