package com.courseplatform.controller;

import com.courseplatform.dto.response.CourseDetailResponse;
import com.courseplatform.dto.response.CourseListResponse;
import com.courseplatform.dto.response.ErrorResponse;
import com.courseplatform.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Public endpoints for browsing course content")
public class CourseController {

    private final CourseService courseService;

    @Operation(
        summary = "List all courses",
        description = "Returns a list of all available courses with topic and subtopic counts. This endpoint is public and does not require authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved course list",
            content = @Content(schema = @Schema(implementation = CourseListResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<CourseListResponse> getAllCourses() {
        CourseListResponse response = courseService.getAllCourses();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get course by ID",
        description = "Returns detailed information about a specific course including all topics, subtopics, and markdown content. This endpoint is public and does not require authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved course details",
            content = @Content(schema = @Schema(implementation = CourseDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseById(
            @Parameter(description = "Course ID (e.g., physics-101, math-101)", example = "physics-101")
            @PathVariable String courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }
}
