package com.zion.zion_center.repository;

import com.zion.zion_center.entity.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {
    Optional<SharedLink> findByToken(String token);

    @Query("SELECT l FROM SharedLink l JOIN FETCH l.lesson WHERE l.user.id = :userId")
    List<SharedLink> findByUserId(@Param("userId") Long userId);

    List<SharedLink> findByLessonId(Long lessonId);
}
