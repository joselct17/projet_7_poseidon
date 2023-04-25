package com.nnk.springboot;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.repositories.RuleNameRepository;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RuleTests {

	@Mock
	private RuleNameRepository ruleNameRepositoryMock;

	@Autowired
	private RuleNameRepository ruleNameRepository;

	@Autowired

	private MockMvc mockMvc;
	@Mock
	private Model model;

	@InjectMocks
	private RuleNameController ruleNameController;

	@Test
	public void ruleTest() {
		RuleName rule = new RuleName(1, "Rule Name", "description", "json","template", "sqlStr", "sqlPart"  );

		// Save
		rule = ruleNameRepository.save(rule);
		Assert.assertNotNull(rule.getId());
		Assert.assertTrue(rule.getName().equals("Rule Name"));

		// Update
		rule.setName("Rule Name Update");
		rule = ruleNameRepository.save(rule);
		Assert.assertTrue(rule.getName().equals("Rule Name Update"));

		// Find
		List<RuleName> listResult = ruleNameRepository.findAll();
		Assert.assertTrue(listResult.size() > 0);

		// Delete
		Integer id = rule.getId();
		ruleNameRepository.delete(rule);
		Optional<RuleName> ruleList = ruleNameRepository.findById(id);
		Assert.assertFalse(ruleList.isPresent());
	}


	@Test
	public void testGetRuleList() {
		// Arrange
		when(ruleNameRepositoryMock.findAll()).thenReturn(new ArrayList<RuleName>());

		// Act
		String viewName = ruleNameController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("ruleNames"), any());
		assertEquals("ruleName/list", viewName);
	}


	@Test
	public void testAddRule() throws Exception {
		// Create a mock rule object
		RuleName rule = new RuleName();

		rule.setId(1);
		rule.setName("Name test");
		rule.setJson("Json test");
		rule.setTemplate("Template test");
		rule.setSqlStr("SqlStr test");

		// Perform a GET request to the /ruleName/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/ruleName/add").flashAttr("rule", rule)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "/ruleName/add"
		assertEquals("ruleName/add", result.getModelAndView().getViewName());
	}
}
