package com.sjoh.shop.repository;

import com.sjoh.shop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndUserId(Long id, Long userId);

    Page<Item> findPageBy(Pageable page);
}
