package com.courseplatform.service;

import com.courseplatform.dto.response.CompletedItemDto;
import com.courseplatform.dto.response.ProgressResponse;
import com.courseplatform.dto.response.SubtopicCompletionResponse;
import com.courseplatform.entity.Enrollment;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.SubtopicProgress;
import com.courseplatform.entity.User;
import com.courseplatform.exception.NotEnrolledException;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final SubtopicProgressRepository progressRepository;
    private final SubtopicRepository subtopicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubtopicCompletionResponse markSubtopicComplete(Long userId, String subtopicId) {
        log.info("User {} marking subtopic {} as complete", userId, subtopicId);

        // Get subtopic with course info
        Subtopic subtopic = subtopicRepository.findByIdWithTopicAndCourse(subtopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Subtopic", "id", subtopicId));

        String courseId = subtopic.getTopic().getCourse().getId();

        // Check if user is enrolled in the course
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new NotEnrolledException("You must be enrolled in this course to mark subtopics as complete");
        }

        // Check if already marked complete (idempotent operation)
        Optional<SubtopicProgress> existingProgress = 
                progressRepository.findByUserIdAndSubtopicId(userId, subtopicId);

        if (existingProgress.isPresent()) {
            // Already complete - return existing progress (idempotent)
            SubtopicProgress progress = existingProgress.get();
            log.debug("Subtopic {} already marked complete for user {}", subtopicId, userId);
            
            return SubtopicCompletionResponse.builder()
                    .subtopicId(subtopicId)
                    .completed(progress.getCompleted())
                    .completedAt(progress.getCompletedAt())
                    .build();
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Create new progress record
        SubtopicProgress progress = SubtopicProgress.builder()
                .user(user)
                .subtopic(subtopic)
                .completed(true)
                .completedAt(Instant.now())
                .build();

        SubtopicProgress savedProgress = progressRepository.save(progress);
        
        log.info("User {} successfully marked subtopic {} as complete", userId, subtopicId);

        return SubtopicCompletionResponse.builder()
                .subtopicId(subtopicId)
                .completed(savedProgress.getCompleted())
                .completedAt(savedProgress.getCompletedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ProgressResponse getProgress(Long userId, Long enrollmentId) {
        log.debug("Fetching progress for user {} enrollment {}", userId, enrollmentId);

        // Get enrollment and verify it belongs to the user
        Enrollment enrollment = enrollmentRepository.findByIdAndUserIdWithCourse(enrollmentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        String courseId = enrollment.getCourse().getId();
        String courseTitle = enrollment.getCourse().getTitle();

        // Get total subtopics in course
        int totalSubtopics = subtopicRepository.countByCourseId(courseId);

        // Get completed subtopics count
        int completedSubtopics = progressRepository.countCompletedByUserIdAndCourseId(userId, courseId);

        // Calculate percentage
        double completionPercentage = 0.0;
        if (totalSubtopics > 0) {
            completionPercentage = BigDecimal.valueOf(completedSubtopics)
                    .divide(BigDecimal.valueOf(totalSubtopics), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // Get completed items with details
        List<SubtopicProgress> completedProgressList = 
                progressRepository.findCompletedByUserIdAndCourseId(userId, courseId);

        List<CompletedItemDto> completedItems = completedProgressList.stream()
                .map(p -> CompletedItemDto.builder()
                        .subtopicId(p.getSubtopic().getId())
                        .subtopicTitle(p.getSubtopic().getTitle())
                        .completedAt(p.getCompletedAt())
                        .build())
                .collect(Collectors.toList());

        return ProgressResponse.builder()
                .enrollmentId(enrollmentId)
                .courseId(courseId)
                .courseTitle(courseTitle)
                .totalSubtopics(totalSubtopics)
                .completedSubtopics(completedSubtopics)
                .completionPercentage(completionPercentage)
                .completedItems(completedItems)
                .build();
    }
}
