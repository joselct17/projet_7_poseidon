package com.nnk.springboot;

import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.repositories.TradeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TradeTests {

	@Autowired
	private TradeRepository tradeRepository;

	@Mock
	private TradeRepository tradeRepositoryMock;

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private Model model;

	@InjectMocks
	private TradeController tradeController;

	@Captor
	private ArgumentCaptor<Trade> captor;

	@Mock
	private BindingResult bindingResult;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
	}

	@Test
	public void tradeTest() {
		Trade trade = new Trade(1, "Trade Account", "type", valueOf(20), valueOf(20), valueOf(20), valueOf(10), "benchmark", new Timestamp(10), "security", "status", "trader", "book", "creationName", new Timestamp(50), "revisionName", new Timestamp(40), "dealName", "dealType", "sourceListId", "site" );

		// Save
		trade = tradeRepository.save(trade);
		Assert.assertNotNull(trade.getId());
		Assert.assertTrue(trade.getAccount().equals("Trade Account"));

		// Update
		trade.setAccount("Trade Account Update");
		trade = tradeRepository.save(trade);
		Assert.assertTrue(trade.getAccount().equals("Trade Account Update"));

		// Find
		List<Trade> listResult = tradeRepository.findAll();
		Assert.assertTrue(listResult.size() > 0);

		// Delete
		Integer id = trade.getId();
		tradeRepository.delete(trade);
		Optional<Trade> tradeList = tradeRepository.findById(id);
		Assert.assertFalse(tradeList.isPresent());
	}

	@Test
	public void testGetTradeList() {
		// Arrange
		when(tradeRepositoryMock.findAll()).thenReturn(new ArrayList<Trade>());

		// Act
		String viewName = tradeController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("trades"), any());
		assertEquals("trade/list", viewName);
	}

	@Test
	public void testAddTrade() throws Exception {
		// Create a mock rule object
		Trade trade = new Trade();


		// Perform a GET request to the /ruleName/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/trade/add").flashAttr("trade", trade)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "/ruleName/add"
		assertEquals("trade/add", result.getModelAndView().getViewName());
	}


	@Test
	public void testPostTrade_Success() throws Exception {

		// Arrange
		Trade trade = new Trade();

		trade.setId(1);
		trade.setAccount("Account test");
		trade.setBook("Book test");
		trade.setCreationDate(new Timestamp(20));
		trade.setBenchmark("Benchmark test");


		//Act
		mockMvc.perform(MockMvcRequestBuilders.post("/trade/validate")
						.flashAttr("trade", trade))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/trade/list"));

		//Assert
		Mockito.verify(tradeRepositoryMock, Mockito.times(1)).save(trade);
		Mockito.verify(tradeRepositoryMock, Mockito.times(1)).findAll();

	}

	@Test
	public void testGetUpdateForm() throws Exception {
		// Arrange
		Trade trade = new Trade();
		trade.setId(1);
		Mockito.when(tradeRepositoryMock.findById(1)).thenReturn(Optional.of(trade));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/trade/update/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("trade/update"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("trade"))
				.andExpect(MockMvcResultMatchers.model().attribute("trade", trade));

		// Assert
		Mockito.verify(tradeRepositoryMock, Mockito.times(1)).findById(1);
	}

	@Test
	public void testPostUpdateFormAndRedirect_SUCCESS() {
		// Arrange
		Integer id = 1;
		Trade trade = new Trade();

		when(bindingResult.hasErrors()).thenReturn(false);
		when(tradeRepositoryMock.save(trade)).thenReturn(null);

		// Act
		String result = tradeController.updateTrade(id, trade, bindingResult, model);

		// Assert
		verify(tradeRepositoryMock).save(trade);
		verify(model).addAttribute(eq("trades"), anyList());
		assertEquals("redirect:/trade/list", result);
	}

	@Test
	public void updateTrade_shouldReturnUpdateViewWhenBindingResultHasErrors() {
		// Arrange
		Integer id = 1;
		Trade trade = new Trade();
		when(bindingResult.hasErrors()).thenReturn(true);

		// Act
		String result = tradeController.updateTrade(id, trade, bindingResult, model);

		// Assert
		verifyNoInteractions(tradeRepositoryMock);
		assertEquals("trade/update", result);
	}

	@Test
	public void testDeleteTrade() {
		// Arrange
		Trade trade = new Trade();
		trade.setId(1);
		when(tradeRepositoryMock.findById(1)).thenReturn(Optional.of(trade));

		// Act
		String result = tradeController.deleteTrade(1, model);

		// Assert
		verify(tradeRepositoryMock).delete(captor.capture());
		Trade deletedTrade = captor.getValue();
		assertEquals(trade.getId(), deletedTrade.getId());
		assertEquals("redirect:/trade/list", result);
	}

}
