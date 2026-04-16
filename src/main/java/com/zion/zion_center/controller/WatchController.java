package com.zion.zion_center.controller;

import com.zion.zion_center.dto.watch.WatchResponse;
import com.zion.zion_center.service.WatchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch")
@RequiredArgsConstructor
public class WatchController {

    private final WatchService watchService;

    @GetMapping("/{token}")
    public ResponseEntity<WatchResponse> watch(
            @PathVariable String token,
            HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) ipAddress = request.getRemoteAddr();

        String userAgent = request.getHeader("User-Agent");

        return ResponseEntity.ok(watchService.watch(token, ipAddress, userAgent));
    }
}
