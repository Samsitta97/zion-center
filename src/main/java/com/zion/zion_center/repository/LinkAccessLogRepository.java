package com.zion.zion_center.repository;

import com.zion.zion_center.entity.LinkAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkAccessLogRepository extends JpaRepository<LinkAccessLog, Long> {
    List<LinkAccessLog> findBySharedLinkId(Long sharedLinkId);
}
