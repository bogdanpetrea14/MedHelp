package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.CreateFeedbackDto;
import com.mobylab.springbackend.dto.FeedbackResponseDto;
import com.mobylab.springbackend.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<String> createFeedback(@Valid @RequestBody CreateFeedbackDto dto) {
        feedbackService.createFeedback(dto);
        return new ResponseEntity<>("Feedback-ul a fost trimis cu succes!", HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }
}