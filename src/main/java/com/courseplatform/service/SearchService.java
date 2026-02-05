package com.courseplatform.service;

import com.courseplatform.dto.response.SearchMatchDto;
import com.courseplatform.dto.response.SearchResponse;
import com.courseplatform.dto.response.SearchResultDto;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final CourseRepository courseRepository;

    private static final int SNIPPET_LENGTH = 100;

    @Transactional(readOnly = true)
    public SearchResponse search(String query) {
        log.debug("Searching for: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return SearchResponse.builder()
                    .query(query)
                    .results(Collections.emptyList())
                    .build();
        }

        String searchTerm = query.toLowerCase().trim();
        List<Course> allCourses = courseRepository.findAll();

        // Map to collect results by course
        Map<String, SearchResultDto> resultMap = new LinkedHashMap<>();

        for (Course course : allCourses) {
            List<SearchMatchDto> matches = new ArrayList<>();

            // Search in course title
            if (containsIgnoreCase(course.getTitle(), searchTerm)) {
                matches.add(SearchMatchDto.builder()
                        .type("course")
                        .snippet(course.getTitle())
                        .build());
            }

            // Search in course description
            if (containsIgnoreCase(course.getDescription(), searchTerm)) {
                matches.add(SearchMatchDto.builder()
                        .type("course")
                        .snippet(createSnippet(course.getDescription(), searchTerm))
                        .build());
            }

            // Search in topics
            for (Topic topic : course.getTopics()) {
                // Search in topic title
                if (containsIgnoreCase(topic.getTitle(), searchTerm)) {
                    matches.add(SearchMatchDto.builder()
                            .type("topic")
                            .topicTitle(topic.getTitle())
                            .snippet(topic.getTitle())
                            .build());
                }

                // Search in subtopics
                for (Subtopic subtopic : topic.getSubtopics()) {
                    // Search in subtopic title
                    if (containsIgnoreCase(subtopic.getTitle(), searchTerm)) {
                        matches.add(SearchMatchDto.builder()
                                .type("subtopic")
                                .topicTitle(topic.getTitle())
                                .subtopicId(subtopic.getId())
                                .subtopicTitle(subtopic.getTitle())
                                .snippet(subtopic.getTitle())
                                .build());
                    }

                    // Search in subtopic content
                    if (containsIgnoreCase(subtopic.getContent(), searchTerm)) {
                        matches.add(SearchMatchDto.builder()
                                .type("content")
                                .topicTitle(topic.getTitle())
                                .subtopicId(subtopic.getId())
                                .subtopicTitle(subtopic.getTitle())
                                .snippet(createSnippet(subtopic.getContent(), searchTerm))
                                .build());
                    }
                }
            }

            // If we found matches, add to results
            if (!matches.isEmpty()) {
                resultMap.put(course.getId(), SearchResultDto.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getTitle())
                        .matches(matches)
                        .build());
            }
        }

        return SearchResponse.builder()
                .query(query)
                .results(new ArrayList<>(resultMap.values()))
                .build();
    }

    /**
     * Case-insensitive contains check (equivalent to ILIKE %query%)
     */
    private boolean containsIgnoreCase(String text, String searchTerm) {
        if (text == null)
            return false;
        return text.toLowerCase().contains(searchTerm);
    }

    /**
     * Creates a snippet around the matched text
     */
    private String createSnippet(String text, String searchTerm) {
        if (text == null)
            return "";

        String lowerText = text.toLowerCase();
        int index = lowerText.indexOf(searchTerm);

        if (index == -1) {
            // Return the beginning of the text if term not found (shouldn't happen)
            return text.substring(0, Math.min(text.length(), SNIPPET_LENGTH)) +
                    (text.length() > SNIPPET_LENGTH ? "..." : "");
        }

        // Calculate start and end positions for the snippet
        int start = Math.max(0, index - SNIPPET_LENGTH / 2);
        int end = Math.min(text.length(), index + searchTerm.length() + SNIPPET_LENGTH / 2);

        String snippet = text.substring(start, end);

        // Add ellipses if we're not at the boundaries
        String prefix = start > 0 ? "..." : "";
        String suffix = end < text.length() ? "..." : "";

        return prefix + snippet.trim() + suffix;
    }
}
