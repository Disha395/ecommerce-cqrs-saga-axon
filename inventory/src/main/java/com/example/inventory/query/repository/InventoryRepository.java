package com.example.inventory.query.repository;

import com.example.inventory.model.enity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByProductId(String productId);

}
