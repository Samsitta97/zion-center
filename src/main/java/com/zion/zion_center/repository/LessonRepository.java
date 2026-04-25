package com.zion.zion_center.repository;

import com.zion.zion_center.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l JOIN FETCH l.aClass JOIN FETCH l.category WHERE l.aClass.id = :classId")
    List<Lesson> findByAClassId(@Param("classId") Long classId);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.aClass JOIN FETCH l.category WHERE l.category.id = :categoryId")
    List<Lesson> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.aClass JOIN FETCH l.category WHERE l.id = :id")
    Optional<Lesson> findByIdFetched(@Param("id") Long id);
}
