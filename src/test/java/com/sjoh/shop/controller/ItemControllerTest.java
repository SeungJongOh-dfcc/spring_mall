package com.sjoh.shop.controller;

import com.sjoh.shop.model.Item;
import com.sjoh.shop.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockitoBean
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    private ArrayList<Item> mockItems;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        mockItems = new ArrayList<>();
        Item itemA = new Item();
        Item itemB = new Item();
        itemA.setTitle("item 1");
        itemA.setPrice(1000.0);
        itemB.setTitle("item 2");
        itemB.setPrice(423210.0);
        mockItems.add(itemA);
        mockItems.add(itemB);

        System.out.println("BeforeEach: 테스트 데이터 초기화 완료");
    }

    @AfterEach
    void tearDown() {
        // Mockito Mock 상태 초기화
        reset(itemService);
        System.out.println("AfterEach: Mock 상태 초기화 완료");
    }

    @Test
    @DisplayName("GET /list - 성공적으로 아이템 목록을 가져옴")
    void list() throws Exception{
        when(itemService.getItemAll()).thenReturn(mockItems);

        // 요청 및 검증
        mockMvc.perform(get("/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("itemAll"))
                .andExpect(model().attribute("itemAll", mockItems));
    }

    @Test
    void write() {
//        when(itemService.)
    }

    @Test
    @DisplayName("POST /add - 아이템 저장 성공")
    void savedItem_success() throws Exception {
        when(itemService.savedItem(any(Item.class))).thenReturn(true);

        // 요청 및 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/add")
                        .param("title", "Item 1")
                        .param("price", "1000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));

    }

    @Test
    @DisplayName("POST /add - 아이템 저장 실패(유효성 검사 실패)")
    void savedItem_failed() throws Exception {
        when(itemService.savedItem(any(Item.class)))
                .thenThrow(new IllegalArgumentException("상품명은 필수 입력 항목입니다."));

        mockMvc.perform(MockMvcRequestBuilders.post("/add")
                .param("title", "")
                .param("price", "-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "상품명은 필수 입력 항목입니다."));
    }

    @Test
    @DisplayName("GET /detail{id} - 아이템 상세 조회 성공")
    void detail() throws Exception {
        when(itemService.findItemById(1L)).thenReturn(mockItems.get(0));

        // 요청 및 검증
        mockMvc.perform(get("/detail/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", mockItems.get(0)));
    }

    @Test
    @DisplayName("GET /detail/{id} - 아이템 상세 조회 실패")
    void detail_failed() throws Exception {
        when(itemService.findItemById(5L)).thenThrow(new NoSuchElementException("Item not found with id: " + 5L));

        mockMvc.perform(get("/detail/{id}", 5))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Item not found with id: " + 5L));
    }

}