package com.zion.zion_center.controller;

import com.zion.zion_center.dto.sharedlink.SharedLinkRequest;
import com.zion.zion_center.dto.sharedlink.SharedLinkResponse;
import com.zion.zion_center.service.SharedLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class SharedLinkController {

    private final SharedLinkService sharedLinkService;

    @PostMapping
    public ResponseEntity<SharedLinkResponse> generate(
            @Valid @RequestBody SharedLinkRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sharedLinkService.generate(request, email));
    }

    @GetMapping
    public ResponseEntity<List<SharedLinkResponse>> getMyLinks(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(sharedLinkService.getMyLinks(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        sharedLinkService.deactivate(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        sharedLinkService.activate(id, email);
        return ResponseEntity.noContent().build();
    }
}
