package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepoMock = mock(UserRepository.class);
    private CartRepository cartRepoMock = mock(CartRepository.class);
    private ItemRepository itemRepoMock = mock(ItemRepository.class);

    private Cart cart;
    private Item item;
    private User user;

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepoMock);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepoMock);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepoMock);

        //create user for each test
        user = new User();
        user.setId(1L);
        user.setUsername("test user");

        //create an item for each test
        item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test desc");
        item.setPrice(new BigDecimal(100));

        //create cart and link it to the user
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
    }

    @Test
    public void givenUser_whenItemIsNotFoundWhenAddingToTheCart_thenReturnNotFound() {
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.addTocart(modifyCartRequest);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenNoUser_whenAddingItemToTheCart_thenReturnNotFound() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.addTocart(modifyCartRequest);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenUser_whenItemIsAddedToTheCart_thenItemIsSavedToTheCart() {
        Optional<Item> optionalItem = Optional.of(item);
        //mock the user repository returns a correct user
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        //mock that item is found and return from item repository
        when(itemRepoMock.findById(1L)).thenReturn(optionalItem);
        //create modify cart request
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.addTocart(modifyCartRequest);
        assertNotNull(res);

        //check status code
        assertEquals(HttpStatus.OK, res.getStatusCode());

        //check the saved item match the item returned from mock item repo
        assertEquals("test item", res.getBody().getItems().get(0).getName());
        assertEquals("test desc", res.getBody().getItems().get(0).getDescription());
        assertTrue(new BigDecimal(100).equals(res.getBody().getItems().get(0).getPrice()));
    }

    @Test
    public void givenUser_whenItemToBeRemovedFromCartIsNotFound_thenReturnNotFound() {
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.removeFromcart(modifyCartRequest);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenNoUser_whenItemIsToBeRemovedFromCart_thenReturnNotFound() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.removeFromcart(modifyCartRequest);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void givenUserWithItemInCart_whenItemIsRemovedFromCart_thenCartIsEmpty() {
        Optional<Item> optionalItem = Optional.of(item);
        //mock the user repository returns a correct user
        when(userRepoMock.findByUsername("test user")).thenReturn(user);
        //mock that item is found and return from item repository
        when(itemRepoMock.findById(1L)).thenReturn(optionalItem);
        //create modify cart request
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        cartController.addTocart(modifyCartRequest);


        ResponseEntity<Cart> res = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(res);
        //check status code
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("test user", res.getBody().getUser().getUsername());
        assertEquals(0, res.getBody().getItems().size());

    }

}
