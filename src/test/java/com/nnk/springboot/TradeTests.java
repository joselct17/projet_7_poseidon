package com.nnk.springboot;

import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.repositories.TradeRepository;
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

		trade.setId(1);
		trade.setAccount("Account test");
		trade.setBook("Book test");
		trade.setCreationDate(new Timestamp(20));
		trade.setBenchmark("Benchmark test");

		// Perform a GET request to the /ruleName/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/trade/add").flashAttr("trade", trade)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "/ruleName/add"
		assertEquals("trade/add", result.getModelAndView().getViewName());
	}

}
