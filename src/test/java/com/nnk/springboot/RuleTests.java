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
public class RuleTests {

	@Mock
	private RuleNameRepository ruleNameRepository;

	@Mock
	private Model model;

	@InjectMocks
	private RuleNameController ruleNameController;

	@Test
	public void ruleTest() {
		RuleName rule = new RuleName();

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
	public void testGetBidList() {
		// Arrange
		when(ruleNameRepository.findAll()).thenReturn(new ArrayList<RuleName>());

		// Act
		String viewName = ruleNameController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("ruleNames"), any());
		assertEquals("ruleName/list", viewName);
	}
}
