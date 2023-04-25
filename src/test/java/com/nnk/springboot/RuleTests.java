package com.nnk.springboot;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

	@Mock
	private BindingResult bindingResult;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(ruleNameController).build();
	}

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

	@Test
	public void testPostRule_Success() throws Exception {

		// Arrange
		RuleName ruleName = new RuleName();

		ruleName.setId(1);
		ruleName.setName("Name test");
		ruleName.setJson("Json test");
		ruleName.setTemplate("Template test");
		ruleName.setSqlStr("SqlStr test");

		//Act
		mockMvc.perform(MockMvcRequestBuilders.post("/ruleName/validate")
						.flashAttr("ruleName", ruleName))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/ruleName/list"));

		//Assert
		Mockito.verify(ruleNameRepositoryMock, Mockito.times(1)).save(ruleName);
		Mockito.verify(ruleNameRepositoryMock, Mockito.times(1)).findAll();

	}

	@Test
	public void testGetUpdateForm() throws Exception {
		// Arrange
		RuleName ruleName = new RuleName();
		ruleName.setId(1);
		Mockito.when(ruleNameRepositoryMock.findById(1)).thenReturn(Optional.of(ruleName));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/ruleName/update/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("ruleName/update"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("ruleName"))
				.andExpect(MockMvcResultMatchers.model().attribute("ruleName", ruleName));

		// Assert
		Mockito.verify(ruleNameRepositoryMock, Mockito.times(1)).findById(1);
	}

	@Test
	public void testPostUpdateFormAndRedirect_SUCCESS() {
		// Arrange
		Integer id = 1;
		RuleName ruleName = new RuleName();

		when(bindingResult.hasErrors()).thenReturn(false);
		when(ruleNameRepositoryMock.save(ruleName)).thenReturn(null);

		// Act
		String result = ruleNameController.updateRuleName(id, ruleName, bindingResult, model);

		// Assert
		verify(ruleNameRepositoryMock).save(ruleName);
		verify(model).addAttribute(eq("ruleNames"), anyList());
		assertEquals("redirect:/ruleName/list", result);
	}

	@Test
	public void updateRuleName_shouldReturnUpdateViewWhenBindingResultHasErrors() {
		// Arrange
		Integer id = 1;
		RuleName ruleName = new RuleName();
		when(bindingResult.hasErrors()).thenReturn(true);

		// Act
		String result = ruleNameController.updateRuleName(id, ruleName, bindingResult, model);

		// Assert
		verifyNoInteractions(ruleNameRepositoryMock);
		assertEquals("ruleName/update", result);
	}
}
