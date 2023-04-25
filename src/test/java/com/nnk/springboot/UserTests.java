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

    @Mock
    private BindingResult bindingResult;

    @Mock
    private BindingResult bidingResult;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

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

    @Test
    public void testPostUser_Success() throws Exception {

        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("user@user.com");
        user.setFullname("User User");
        user.setPassword("passwordEncrypted");


        //Act
        mockMvc.perform(MockMvcRequestBuilders.post("/user/validate")
                        .flashAttr("user", user))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/list"));

        //Assert
        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(user);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findAll();

    }


    @Test
    public void testGetUpdateForm() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);
        Mockito.when(userRepositoryMock.findById(1)).thenReturn(Optional.of(user));

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/update/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/update"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user));

        // Assert
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findById(1);
    }

    @Test
    public void testPostUpdateFormAndRedirect_SUCCESS() {
        // Arrange
        Integer id = 1;


        User user = new User(1,"user@user.com", "password", "User User" , new ArrayList<>());

        when(bidingResult.hasErrors()).thenReturn(false);
        when(userRepositoryMock.save(user)).thenReturn(null);

        // Act
        String result = userController.updateUser(id, user, bidingResult, model);

        // Assert
        verify(userRepositoryMock).save(user);
        verify(model).addAttribute(eq("users"), anyList());
        assertEquals("redirect:/user/list", result);
    }

    @Test
    public void testUpdateUser_shouldReturnUpdateViewWhenBindingResultHasErrors() {
        // Arrange
        Integer id = 1;
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userController.updateUser(id, user, bindingResult, model);

        // Assert
        verifyNoInteractions(userRepositoryMock);
        assertEquals("user/update", result);
    }


}
