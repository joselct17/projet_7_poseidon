package com.nnk.springboot;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
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



}
