package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepoMock = mock(ItemRepository.class);
    private Item item;

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepoMock);
        item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test desc");
        item.setPrice(new BigDecimal(100));
    }

    @Test
    public void findItemByName() {
        when(itemRepoMock.findByName("test item")).thenReturn(Arrays.asList(item));
        ResponseEntity<List<Item>> res = itemController.getItemsByName("test item");

        assertNotNull(res);
        assertEquals(HttpStatus.OK,res.getStatusCode());
        assertEquals("test item", res.getBody().get(0).getName());
        assertEquals("test desc", res.getBody().get(0).getDescription());
        assertTrue(new BigDecimal(100).equals(res.getBody().get(0).getPrice()));
    }

    @Test
    public void findItemByNameThatNotExist() {
        ResponseEntity<List<Item>> res = itemController.getItemsByName("test item");

        assertNotNull(res);
        assertEquals(HttpStatus.NOT_FOUND,res.getStatusCode());
    }

    @Test
    public void getItemById() {
        Optional<Item> optItem = Optional.of(item);
        when(itemRepoMock.findById(1L)).thenReturn(optItem);
        ResponseEntity<Item> res = itemController.getItemById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
