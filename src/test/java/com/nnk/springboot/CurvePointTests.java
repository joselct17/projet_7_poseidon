package com.nnk.springboot;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
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
public class CurvePointTests {

	@Mock
	private CurvePointRepository curvePointRepositoryMock;
	@Autowired
	private CurvePointRepository curvePointRepository;

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private Model model;
	@InjectMocks
	private CurveController curveController;

	@Mock
	private BindingResult bindingResult;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(curveController).build();
	}

	@Test
	public void curvePointTest() {
		CurvePoint curvePoint = new CurvePoint( 1, 10, new Timestamp(20), valueOf(20), valueOf(30), new Timestamp(20) );

		// Save
		curvePoint = curvePointRepository.save(curvePoint);
		Assert.assertNotNull(curvePoint.getId());
		Assert.assertTrue(curvePoint.getCurveId() == 10);

		// Update
		curvePoint.setCurveId(20);
		curvePoint = curvePointRepository.save(curvePoint);
		Assert.assertTrue(curvePoint.getCurveId() == 20);

		// Find
		List<CurvePoint> listResult = curvePointRepository.findAll();
		Assert.assertTrue(listResult.size() > 0);

		// Delete
		Integer id = curvePoint.getId();
		curvePointRepository.delete(curvePoint);
		Optional<CurvePoint> curvePointList = curvePointRepository.findById(id);
		Assert.assertFalse(curvePointList.isPresent());
	}


	@Test
	public void testCurvePointList() {
		// Arrange
		when(curvePointRepositoryMock.findAll()).thenReturn(new ArrayList<CurvePoint>());

		// Act
		String viewName = curveController.home(model);

		// Assert
		verify(model, times(1)).addAttribute(eq("curvePoints"), any());
		assertEquals("curvePoint/list", viewName);
	}

	@Test
	public void testAddCurvePoint() throws Exception {
		// Create a mock curvePoint object
		CurvePoint curve = new CurvePoint();
		curve.setId(1);
		curve.setTerm(valueOf(20));
		curve.setCurveId(1);
		curve.setValue(valueOf(20));

		// Perform a GET request to the /curvePoint/add endpoint and pass the mock bid object as a parameter
		MvcResult result = mockMvc.perform(get("/curvePoint/add").flashAttr("curve", curve)).andReturn();

		// Assert that the response status is OK
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

		// Assert that the view name is "/curvePoint/add"
		assertEquals("curvePoint/add", result.getModelAndView().getViewName());
	}

	@Test
	public void testPostCurvePoint_Success() throws Exception {

		//Arrange
		CurvePoint curvePoint = new CurvePoint();
		curvePoint.setId(1);
		curvePoint.setTerm(valueOf(20));
		curvePoint.setCurveId(1);
		curvePoint.setValue(valueOf(20));

		//Act
		mockMvc.perform(MockMvcRequestBuilders.post("/curvePoint/validate")
						.flashAttr("curvePoint", curvePoint))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/curvePoint/list"));

		//Assert
		Mockito.verify(curvePointRepositoryMock, Mockito.times(1)).save(curvePoint);
		Mockito.verify(curvePointRepositoryMock, Mockito.times(1)).findAll();

	}

	@Test
	public void testGetUpdateForm() throws Exception {
		// Arrange
		CurvePoint curvePoint = new CurvePoint();
		curvePoint.setId(1);
		Mockito.when(curvePointRepositoryMock.findById(1)).thenReturn(Optional.of(curvePoint));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/curvePoint/update/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("curvePoint/update"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("curvePoint"))
				.andExpect(MockMvcResultMatchers.model().attribute("curvePoint", curvePoint));

		// Assert
		Mockito.verify(curvePointRepositoryMock, Mockito.times(1)).findById(1);
	}

	@Test
	public void testPostUpdateFormAndRedirect_SUCCESS() {
		// Arrange
		Integer id = 1;
		CurvePoint curvePoint = new CurvePoint();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(curvePointRepositoryMock.save(curvePoint)).thenReturn(null);

		// Act
		String result = curveController.updateCurve(id, curvePoint, bindingResult, model);

		// Assert
		verify(curvePointRepositoryMock).save(curvePoint);
		verify(model).addAttribute(eq("curvePoint"), anyList());
		assertEquals("redirect:/curvePoint/list", result);
	}

	@Test
	public void updateCurve_shouldReturnUpdateViewWhenBindingResultHasErrors() {
		// Arrange
		Integer id = 1;
		CurvePoint curvePoint = new CurvePoint();
		when(bindingResult.hasErrors()).thenReturn(true);

		// Act
		String result = curveController.updateCurve(id, curvePoint, bindingResult, model);

		// Assert
		verifyNoInteractions(curvePointRepositoryMock);
		assertEquals("curvePoint/update", result);
	}



}
