package com.courseplatform.controller;

import com.courseplatform.dto.response.SearchResponse;
import com.courseplatform.service.SearchService;
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
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Public endpoint for searching course content")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Search courses and content", description = "Searches across course titles, descriptions, topic titles, subtopic titles, and subtopic content. "
            +
            "Search is case-insensitive and supports partial matching (e.g., 'velo' matches 'velocity'). " +
            "This endpoint is public and does not require authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = SearchResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "Search query string (e.g., 'velocity', 'Newton', 'rate of change')", example = "velocity", required = true) @RequestParam("q") String query) {
        SearchResponse response = searchService.search(query);
        return ResponseEntity.ok(response);
    }
}
