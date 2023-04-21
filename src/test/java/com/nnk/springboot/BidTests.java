package com.nnk.springboot;

import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BidTests {


	@Mock
	private BidListRepository bidListRepository;

	@Mock
	private Model model;

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private BidListController bidListController;


	@Test
	public void bidListTest() {
		BidList bid = new BidList();

		// Save
		bid = bidListRepository.save(bid);
		Assert.assertNotNull(bid.getId());
		assertEquals(bid.getBidQuantity(), 10d, 10d);

		// Update
		bid.setBidQuantity(20d);
		bid = bidListRepository.save(bid);
		assertEquals(bid.getBidQuantity(), 20d, 20d);

		// Find
		List<BidList> listResult = bidListRepository.findAll();
		Assert.assertTrue(listResult.size() > 0);

		// Delete
		Integer id = bid.getId();
		bidListRepository.delete(bid);
		Optional<BidList> bidList = bidListRepository.findById(id);
		Assert.assertFalse(bidList.isPresent());
	}

	@Test
	public void testGetBidList() {
		// Arrange
		when(bidListRepository.findAll()).thenReturn(new ArrayList<BidList>());

		// Act
		String viewName = bidListController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("bidLists"), any());
		assertEquals("bidList/list", viewName);
	}

	@Test
	public void testAddBidForm() throws Exception {
		// Create a mock bid object
		BidList bid = new BidList();
		bid.setId(1);
		bid.setAccount("Test Account");
		bid.setType("Test Type");

		// Perform a GET request to the /bidList/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/bidList/add").flashAttr("bid", bid)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "bidList/add"
		assertEquals("bidList/add", result.getModelAndView().getViewName());
	}



}