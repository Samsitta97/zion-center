package com.zion.zion_center.service;

import com.zion.zion_center.dto.watch.WatchResponse;
import com.zion.zion_center.entity.LinkAccessLog;
import com.zion.zion_center.entity.SharedLink;
import com.zion.zion_center.exception.AccessDeniedException;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.repository.LinkAccessLogRepository;
import com.zion.zion_center.repository.SharedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WatchService {

    private final SharedLinkRepository sharedLinkRepository;
    private final LinkAccessLogRepository linkAccessLogRepository;

    @Transactional
    public WatchResponse watch(String token, String ipAddress, String userAgent) {
        SharedLink link = sharedLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid link"));

        if (!link.isActive()) {
            throw new AccessDeniedException("This link has been deactivated");
        }

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("This link has expired");
        }

        if (link.getMaxViews() != null && link.getViewCount() >= link.getMaxViews()) {
            throw new AccessDeniedException("This link has reached its maximum view count");
        }

        // Increment view count
        link.setViewCount(link.getViewCount() + 1);
        sharedLinkRepository.save(link);

        // Log the access
        LinkAccessLog log = LinkAccessLog.builder()
                .sharedLink(link)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        linkAccessLogRepository.save(log);

        return new WatchResponse(
                link.getLesson().getTitle(),
                link.getLesson().getAClass().getTitle(),
                link.getLesson().getYoutubeUrl(),
                link.getLesson().getYoutubeVideoId(),
                link.getLesson().getDescription()
        );
    }
}
