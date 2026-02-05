package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMatchDto {
    private String type;         // "course", "topic", "subtopic", or "content"
    private String topicTitle;   // null for course-level matches
    private String subtopicId;   // null for course/topic-level matches
    private String subtopicTitle; // null for course/topic-level matches
    private String snippet;      // Relevant text snippet
}
