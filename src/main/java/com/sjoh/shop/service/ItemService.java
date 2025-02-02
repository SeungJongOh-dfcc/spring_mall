package com.sjoh.shop.service;

import com.sjoh.shop.model.Item;
import com.sjoh.shop.model.User;
import com.sjoh.shop.repository.ItemRepository;
import com.sjoh.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    public ArrayList<Item> getItemAll() {
        return (ArrayList<Item>) itemRepository.findAll();
    }

    public Page<Item> getPageItemList(Pageable page) {
        return itemRepository.findPageBy(page);
    }

    public boolean savedItem(Item formData, String userId) throws Exception {
        if(formData.getTitle() == null || formData.getTitle().isBlank()) {
            throw new IllegalArgumentException("상품명은 필수 입력 항목입니다.");
        }

        if(formData.getPrice() == null || formData.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0보다 작으면 안됩니다.");
        }

        // 로그인한 User_ID 받아오기.
        Optional<User> user = userRepository.findByUserId(userId);
        if(user.isEmpty()) {
            throw new Exception("유저가 없습니다.");
        }

        // 데이터 저장
        Item item = new Item();
        item.setTitle(formData.getTitle());
        item.setPrice(formData.getPrice());
        item.setUser(user.get());

        itemRepository.save(item);
        return true;
    }
    
    public Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + id));
    }

    public void partiallyUpdateItem(Long id, Map<String, Object> updates) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item with ID " + id + " not found."));

        // 업데이트 데이터 적용
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    if(value instanceof String) {
                        item.setTitle((String) value);
                    } else {
                        throw new IllegalArgumentException("Invalid type for title.");
                    }
                    break;
                case "price":
                    try {
                        // 문자열로 넘어온 경우 숫자로 변환
                        if (value instanceof String) {
                            long parsedPrice = Long.parseLong((String) value);
                            item.setPrice(parsedPrice);
                        } else if (value instanceof Number) {
                            item.setPrice(((Number) value).longValue());
                        } else {
                            throw new IllegalArgumentException("Invalid type for price");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid format for price: " + value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field: " + key);
            }
        });

        // DB에 저장
        itemRepository.save(item);
    }

    public boolean deleteItem(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if(item.isPresent()) {
            itemRepository.deleteById(id);
            return true;
        } else {
            return false;
        }

    }
}
