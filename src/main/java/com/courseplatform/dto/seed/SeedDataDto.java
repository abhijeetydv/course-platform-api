package com.courseplatform.dto.seed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeedDataDto {
    private List<SeedCourseDto> courses;
}
