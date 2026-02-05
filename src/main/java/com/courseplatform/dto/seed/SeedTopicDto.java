package com.courseplatform.dto.seed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeedTopicDto {
    private String id;
    private String title;
    private List<SeedSubtopicDto> subtopics;
}
