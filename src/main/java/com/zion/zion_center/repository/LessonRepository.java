package com.zion.zion_center.repository;

import com.zion.zion_center.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l WHERE l.aClass.id = :classId")
    List<Lesson> findByAClassId(@Param("classId") Long classId);
}
