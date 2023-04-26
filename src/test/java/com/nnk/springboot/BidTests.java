package com.nnk.springboot;

import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
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


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Double.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class BidTests {

	@Autowired
	BidListRepository bidListRepository;
	@Mock
	private BidListRepository bidListRepositoryMock;

	@Mock
	private Model model;

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private BindingResult bindingResult;

	@Captor
	private ArgumentCaptor<BidList> captor;

	@InjectMocks
	private BidListController bidListController;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(bidListController).build();
	}

	@Test
	public void bidListTest() {
		BidList bid = new BidList(10, "Account Test", "Type Test", valueOf(20), valueOf(20),valueOf(20),valueOf(20),"Benchmark Test", new Timestamp(10), "commentary test", "security test", "status test", "trader test", "book test", "creationName", new Timestamp(20L), "revisionName", new Timestamp(20), " dealName", "dealType", "sourceListId" , "side");

		// Save
		bid = bidListRepository.save(bid);
		Assert.assertNotNull(bid.getId());
		Assert.assertEquals(bid.getBidQuantity(), 10d, 10d);

		// Update
		bid.setBidQuantity(20d);
		bid = bidListRepository.save(bid);
		Assert.assertEquals(bid.getBidQuantity(), 20d, 20d);

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
		when(bidListRepositoryMock.findAll()).thenReturn(new ArrayList<BidList>());

		// Act
		String viewName = bidListController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("bidLists"), any());
		assertEquals("bidList/list", viewName);
	}

	@Test
	public void testGetAddBidForm() throws Exception {
		// Create a mock bid object
		BidList bid = new BidList(10, "Account Test", "Type Test", valueOf(20), valueOf(20),valueOf(20),valueOf(20),"Benchmark Test", new Timestamp(10), "commentary test", "security test", "status test", "trader test", "book test", "creationName", new Timestamp(20L), "revisionName", new Timestamp(20), " dealName", "dealType", "sourceListId" , "side");


		// Perform a GET request to the /bidList/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/bidList/add").flashAttr("bid", bid)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "bidList/add"
		assertEquals("bidList/add", result.getModelAndView().getViewName());
	}

	@Test
	public void testPostValidate_Success() throws Exception {

		//Arrange
		BidList bidList = new BidList();
		bidList.setAccount("Test Account");
		bidList.setType("Test Type");
		bidList.setBidQuantity(1000d);

		//Act
		mockMvc.perform(MockMvcRequestBuilders.post("/bidList/validate")
				.flashAttr("bidList", bidList))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/bidList/list"));

		//Assert
		Mockito.verify(bidListRepositoryMock, Mockito.times(1)).save(bidList);
		Mockito.verify(bidListRepositoryMock, Mockito.times(1)).findAll();

	}


	@Test
	public void testGetUpdateForm() throws Exception {
		// Arrange
		BidList bidList = new BidList();
		bidList.setId(1);
		Mockito.when(bidListRepositoryMock.findById(1)).thenReturn(Optional.of(bidList));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/bidList/update/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("bidList/update"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("bidList"))
				.andExpect(MockMvcResultMatchers.model().attribute("bidList", bidList));

		// Assert
		Mockito.verify(bidListRepositoryMock, Mockito.times(1)).findById(1);
	}


	@Test
	public void testPostUpdateFormAndRedirect_SUCCESS() {
		// Arrange
		Integer id = 1;
		BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(bidListRepositoryMock.save(bidList)).thenReturn(null);

		// Act
		String result = bidListController.updateBid(id, bidList, bindingResult, model);

		// Assert
		verify(bidListRepositoryMock).save(bidList);
		verify(model).addAttribute(eq("bidLists"), anyList());
		assertEquals("redirect:/bidList/list", result);
	}

	@Test
	public void updateBid_shouldReturnUpdateViewWhenBindingResultHasErrors() {
		// Arrange
		Integer id = 1;
		BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(true);

		// Act
		String result = bidListController.updateBid(id, bidList, bindingResult, model);

		// Assert
		verifyNoInteractions(bidListRepositoryMock);
		assertEquals("bidList/update", result);
	}

	@Test
	public void testDeleteBid() {
		// Arrange
		BidList bidList = new BidList();
		bidList.setId(1);
		when(bidListRepositoryMock.findById(1)).thenReturn(Optional.of(bidList));

		// Act
		String result = bidListController.deleteBid(1, model);

		// Assert
		verify(bidListRepositoryMock).delete(captor.capture());
		BidList deletedBidList = captor.getValue();
		assertEquals(bidList.getId(), deletedBidList.getId());
		assertEquals("redirect:/bidList/list", result);
	}


}
