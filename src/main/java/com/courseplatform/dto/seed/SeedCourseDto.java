package com.courseplatform.dto.seed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeedCourseDto {
    private String id;
    private String title;
    private String description;
    private List<SeedTopicDto> topics;
}
