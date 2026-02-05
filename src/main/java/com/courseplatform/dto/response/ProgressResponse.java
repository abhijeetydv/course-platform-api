package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private Integer totalSubtopics;
    private Integer completedSubtopics;
    private Double completionPercentage;
    private List<CompletedItemDto> completedItems;
}
