package com.courseplatform.controller;

import com.courseplatform.dto.response.EnrollmentListResponse;
import com.courseplatform.dto.response.EnrollmentResponse;
import com.courseplatform.dto.response.ErrorResponse;
import com.courseplatform.dto.response.ProgressResponse;
import com.courseplatform.entity.User;
import com.courseplatform.repository.UserRepository;
import com.courseplatform.service.EnrollmentService;
import com.courseplatform.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Protected endpoints for course enrollment and progress tracking")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final ProgressService progressService;
    private final UserRepository userRepository;

    @Operation(
        summary = "Enroll in a course",
        description = "Enrolls the authenticated user in the specified course. A user cannot enroll in the same course twice."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Successfully enrolled in the course",
            content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "JWT token is missing or invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Already enrolled in this course",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @Parameter(description = "Course ID to enroll in", example = "physics-101")
            @PathVariable String courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        EnrollmentResponse response = enrollmentService.enrollInCourse(userId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get user enrollments",
        description = "Returns a list of all courses the authenticated user is enrolled in"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved enrollments",
            content = @Content(schema = @Schema(implementation = EnrollmentListResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "JWT token is missing or invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/enrollments")
    public ResponseEntity<EnrollmentListResponse> getUserEnrollments(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        EnrollmentListResponse response = enrollmentService.getUserEnrollments(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get enrollment progress",
        description = "Returns detailed progress for a specific enrollment including completion percentage and completed subtopics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved progress",
            content = @Content(schema = @Schema(implementation = ProgressResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "JWT token is missing or invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Enrollment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<ProgressResponse> getProgress(
            @Parameter(description = "Enrollment ID", example = "1")
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        ProgressResponse response = progressService.getProgress(userId, enrollmentId);
        return ResponseEntity.ok(response);
    }

    private Long getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
