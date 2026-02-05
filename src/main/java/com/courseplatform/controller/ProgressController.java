package com.courseplatform.controller;

import com.courseplatform.dto.response.ErrorResponse;
import com.courseplatform.dto.response.SubtopicCompletionResponse;
import com.courseplatform.entity.User;
import com.courseplatform.repository.UserRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtopics")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Protected endpoints for tracking learning progress")
@SecurityRequirement(name = "bearerAuth")
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    @Operation(
        summary = "Mark subtopic as completed",
        description = "Marks the specified subtopic as completed for the authenticated user. " +
                      "The user must be enrolled in the course containing this subtopic. " +
                      "This operation is idempotent - calling it multiple times has the same effect."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Subtopic marked as completed",
            content = @Content(schema = @Schema(implementation = SubtopicCompletionResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "JWT token is missing or invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User is not enrolled in the course",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Subtopic not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/{subtopicId}/complete")
    public ResponseEntity<SubtopicCompletionResponse> markComplete(
            @Parameter(description = "Subtopic ID to mark as complete", example = "velocity")
            @PathVariable String subtopicId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        SubtopicCompletionResponse response = progressService.markSubtopicComplete(userId, subtopicId);
        return ResponseEntity.ok(response);
    }

    private Long getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
