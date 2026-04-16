package com.zion.zion_center.repository;

import com.zion.zion_center.entity.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {
    Optional<SharedLink> findByToken(String token);
    List<SharedLink> findByUserId(Long userId);
    List<SharedLink> findByLessonId(Long lessonId);
}
