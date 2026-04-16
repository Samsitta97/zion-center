package com.zion.zion_center.controller;

import com.zion.zion_center.dto.lesson.LessonRequest;
import com.zion.zion_center.dto.lesson.LessonResponse;
import com.zion.zion_center.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<LessonResponse>> getByClass(@PathVariable Long classId,
                                                            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(lessonService.getByClassId(classId, email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getById(@PathVariable Long id,
                                                   @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(lessonService.getById(id, email));
    }

    @PostMapping
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request,
                                                  @AuthenticationPrincipal String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(request, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody LessonRequest request,
                                                  @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(lessonService.update(id, request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal String email) {
        lessonService.delete(id, email);
        return ResponseEntity.noContent().build();
    }
}
