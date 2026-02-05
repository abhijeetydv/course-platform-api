package com.courseplatform.service;

import com.courseplatform.dto.response.EnrollmentListResponse;
import com.courseplatform.dto.response.EnrollmentResponse;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Enrollment;
import com.courseplatform.entity.User;
import com.courseplatform.exception.DuplicateEnrollmentException;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.EnrollmentRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnrollmentResponse enrollInCourse(Long userId, String courseId) {
        log.info("User {} attempting to enroll in course {}", userId, courseId);

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new DuplicateEnrollmentException("You are already enrolled in this course");
        }

        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        log.info("User {} successfully enrolled in course {} with enrollment id {}", 
                userId, courseId, savedEnrollment.getId());

        return mapToEnrollmentResponse(savedEnrollment, course);
    }

    @Transactional(readOnly = true)
    public EnrollmentListResponse getUserEnrollments(Long userId) {
        log.debug("Fetching enrollments for user {}", userId);

        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);

        List<EnrollmentResponse> enrollmentResponses = enrollments.stream()
                .map(e -> mapToEnrollmentResponse(e, e.getCourse()))
                .collect(Collectors.toList());

        return EnrollmentListResponse.builder()
                .enrollments(enrollmentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isUserEnrolledInCourse(Long userId, String courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment, Course course) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
