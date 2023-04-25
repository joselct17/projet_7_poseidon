package com.nnk.springboot;


import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.Role;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.RolesRepository;
import com.nnk.springboot.repositories.UserRepository;
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
public class UserTests {

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private RolesRepository rolesRepositoryMock;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private Model model;

    @Test
    public void userTest() {
        User user = new User(1, "user@user.com", "passwordEncrypted", "User User", new ArrayList<>());
        // Save
        user = userRepository.save(user);
        Assert.assertNotNull(user.getId());
        Assert.assertTrue(user.getUsername().equals("user@user.com"));

        // Update
        user.setUsername("updateduser@user.com");
        user = userRepository.save(user);
        Assert.assertTrue(user.getUsername().equals("updateduser@user.com"));

        // Find
        List<User> listResult = userRepository.findAll();
        Assert.assertTrue(listResult.size() > 0);

        // Delete
        Integer id = user.getId();
        userRepository.delete(user);
        Optional<User> userList = userRepository.findById(id);
        Assert.assertFalse(userList.isPresent());
    }

    @Test
    public void testGetUserList() {
        // Arrange
        when(userRepositoryMock.findAll()).thenReturn(new ArrayList<User>());

        when(rolesRepositoryMock.findAll()).thenReturn(new ArrayList<Role>());
        // Act
        String viewName = userController.home(model);

        // Assert
        verify(model, times(1)).addAttribute(eq("users"), any());
        assertEquals("user/list", viewName);
    }

    @Test
    public void testGetAddUser() throws Exception {
        // Create a mock bid object
        User user = new User();
        user.setId(1);
        user.setUsername("user@user.com");
        user.setFullname("User User");
        user.setPassword("passwordEncrypted");

        // Perform a GET request to the /bidList/add endpoint and pass the mock bid object as a parameter
        MvcResult result = mockMvc.perform(get("/user/add").flashAttr("user", user)).andReturn();

        // Assert that the response status is OK
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        // Assert that the view name is "bidList/add"
        assertEquals("user/add", result.getModelAndView().getViewName());

    }
}
