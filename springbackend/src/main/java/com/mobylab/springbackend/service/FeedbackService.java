package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreateFeedbackDto;
import com.mobylab.springbackend.dto.FeedbackResponseDto;
import com.mobylab.springbackend.entity.Feedback;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.FeedbackRepository;
import com.mobylab.springbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public void createFeedback(CreateFeedbackDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));

        Feedback feedback = new Feedback()
                .setUser(user)
                .setCategory(dto.getCategory())
                .setRating(dto.getRating())
                .setDetails(dto.getDetails())
                .setAllowContact(dto.getAllowContact() != null ? dto.getAllowContact() : false);

        feedbackRepository.save(feedback);
    }

    public List<FeedbackResponseDto> getAllFeedback() {
        return feedbackRepository.findAll()
                .stream()
                .map(f -> new FeedbackResponseDto()
                        .setId(f.getId())
                        .setUserEmail(f.getUser().getEmail())
                        .setCategory(f.getCategory())
                        .setRating(f.getRating())
                        .setDetails(f.getDetails())
                        .setAllowContact(f.getAllowContact())
                        .setCreatedAt(f.getCreatedAt()))
                .collect(Collectors.toList());
    }
}