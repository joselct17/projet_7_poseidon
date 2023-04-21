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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RatingTests {



	@Mock
	private RatingRepository ratingRepository;

	@Mock
	private Model model;
	@InjectMocks
	private RatingController ratingController;

	@Test
	public void ratingTest() {
		Rating rating = new Rating();

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
	public void testGetBidList() {
		// Arrange
		when(ratingRepository.findAll()).thenReturn(new ArrayList<Rating>());

		// Act
		String viewName = ratingController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("ratings"), any());
		assertEquals("rating/list", viewName);
	}
}
