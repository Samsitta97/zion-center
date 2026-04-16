package com.zion.zion_center.repository;

import com.zion.zion_center.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class> findByUserId(Long userId);
    List<Class> findByCategoryId(Long categoryId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
