package com.nnk.springboot;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.repositories.RatingRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

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
	@Captor
	private ArgumentCaptor<Rating> captor;

	@Mock
	private BindingResult bindingResult;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
	}
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

	@Test
	public void testPostRating_Success() throws Exception {

		//Arrange
		Rating rating = new Rating();
		rating.setId(1);
		rating.setFitchRating("Fitch test");
		rating.setOrderNumber(1);
		rating.setSandPRating("Sand test");

		//Act
		mockMvc.perform(MockMvcRequestBuilders.post("/rating/validate")
						.flashAttr("rating", rating))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/rating/list"));

		//Assert
		Mockito.verify(ratingRepositoryMock, Mockito.times(1)).save(rating);
		Mockito.verify(ratingRepositoryMock, Mockito.times(1)).findAll();

	}

	@Test
	public void testGetUpdateForm() throws Exception {
		// Arrange
		Rating rating = new Rating();
		rating.setId(1);
		Mockito.when(ratingRepositoryMock.findById(1)).thenReturn(Optional.of(rating));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/rating/update/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("rating/update"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("rating"))
				.andExpect(MockMvcResultMatchers.model().attribute("rating", rating));

		// Assert
		Mockito.verify(ratingRepositoryMock, Mockito.times(1)).findById(1);
	}

	@Test
	public void testPostUpdateFormAndRedirect_SUCCESS() {
		// Arrange
		Integer id = 1;
		Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(ratingRepositoryMock.save(rating)).thenReturn(null);

		// Act
		String result = ratingController.updateRating(id, rating, bindingResult, model);

		// Assert
		verify(ratingRepositoryMock).save(rating);
		verify(model).addAttribute(eq("ratings"), anyList());
		assertEquals("redirect:/rating/list", result);
	}
	@Test
	public void updateRating_shouldReturnUpdateViewWhenBindingResultHasErrors() {
		// Arrange
		Integer id = 1;
		Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(true);

		// Act
		String result = ratingController.updateRating(id, rating, bindingResult, model);

		// Assert
		verifyNoInteractions(ratingRepositoryMock);
		assertEquals("rating/update", result);
	}

	@Test
	public void testDeleteRating() {
		// Arrange
		Rating rating = new Rating();
		rating.setId(1);
		when(ratingRepositoryMock.findById(1)).thenReturn(Optional.of(rating));

		// Act
		String result = ratingController.deleteRating(1, model);

		// Assert
		verify(ratingRepositoryMock).delete(captor.capture());
		Rating deletedRating = captor.getValue();
		assertEquals(rating.getId(), deletedRating.getId());
		assertEquals("redirect:/rating/list", result);
	}
}
