package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepoMock = mock(UserRepository.class);
    private OrderRepository orderRepoMock = mock(OrderRepository.class);
    private Item item;
    private User user;
    private Cart cart;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepoMock);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepoMock);

        //create user for each test
        user = new User();
        user.setId(1L);
        user.setUsername("test user");

        //create item for each test
        item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test desc");
        item.setPrice(new BigDecimal(100));

        //create cart for each test
        cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
    }

    @Test
    public void givenNoUser_whenOrderIsSubmitted_thenReturnNotFound() {
        ResponseEntity res = orderController.submit(user.getUsername());
        assertNotNull(res);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenUserWithItemInCart_whenOrderIsSubmitted_thenAddItemTOUserOrder() {
        //mock user repository to return created item
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        cart.addItem(item);
        ResponseEntity<UserOrder> res = orderController.submit(user.getUsername());
        assertNotNull(res);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("test item", res.getBody().getItems().get(0).getName());
        assertEquals("test desc", res.getBody().getItems().get(0).getDescription());
        assertEquals("test user", res.getBody().getUser().getUsername());
        assertTrue(new BigDecimal(100).equals(res.getBody().getItems().get(0).getPrice()));
        assertTrue(new BigDecimal(100).equals(res.getBody().getTotal()));
    }

    @Test
    public void givenNoUser_whenGetOrdersForUsers_thenReturnNotFound() {
        ResponseEntity res = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(res);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenUserWithOrderHistory_whenGetOrdersForUser_thenReturnOK() {
        //mock user repository to return created item
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        cart.addItem(item);
        orderController.submit(user.getUsername());
        ResponseEntity<List<UserOrder>> res = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(res);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
