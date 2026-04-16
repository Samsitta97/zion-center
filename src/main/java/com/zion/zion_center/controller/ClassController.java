package com.zion.zion_center.controller;

import com.zion.zion_center.dto.classdto.ClassRequest;
import com.zion.zion_center.dto.classdto.ClassResponse;
import com.zion.zion_center.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAll(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(classService.getAll(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getById(@PathVariable Long id,
                                                  @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(classService.getById(id, email));
    }

    @PostMapping
    public ResponseEntity<ClassResponse> create(
            @Valid @RequestBody ClassRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classService.create(request, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(classService.update(id, request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal String email) {
        classService.delete(id, email);
        return ResponseEntity.noContent().build();
    }
}
