package com.courseplatform.service;

import com.courseplatform.dto.seed.SeedCourseDto;
import com.courseplatform.dto.seed.SeedDataDto;
import com.courseplatform.dto.seed.SeedSubtopicDto;
import com.courseplatform.dto.seed.SeedTopicDto;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedDataService implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:seed_data/courses.json")
    private Resource seedDataResource;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists - PREVENTS DUPLICATION
        if (courseRepository.count() > 0) {
            log.info("Seed data already exists. Skipping seed data loading.");
            return;
        }

        log.info("Loading seed data from courses.json...");

        try {
            // Parse JSON file
            SeedDataDto seedData = objectMapper.readValue(
                    seedDataResource.getInputStream(),
                    SeedDataDto.class
            );

            int totalCourses = 0;
            int totalTopics = 0;
            int totalSubtopics = 0;

            // Process each course
            for (SeedCourseDto courseDto : seedData.getCourses()) {
                Course course = Course.builder()
                        .id(courseDto.getId())
                        .title(courseDto.getTitle())
                        .description(courseDto.getDescription())
                        .build();

                int topicIndex = 0;
                for (SeedTopicDto topicDto : courseDto.getTopics()) {
                    Topic topic = Topic.builder()
                            .id(topicDto.getId())
                            .title(topicDto.getTitle())
                            .orderIndex(topicIndex++)
                            .build();

                    int subtopicIndex = 0;
                    for (SeedSubtopicDto subtopicDto : topicDto.getSubtopics()) {
                        Subtopic subtopic = Subtopic.builder()
                                .id(subtopicDto.getId())
                                .title(subtopicDto.getTitle())
                                .content(subtopicDto.getContent())
                                .orderIndex(subtopicIndex++)
                                .build();

                        topic.addSubtopic(subtopic);
                        totalSubtopics++;
                    }

                    course.addTopic(topic);
                    totalTopics++;
                }

                courseRepository.save(course);
                totalCourses++;
            }

            log.info("Seed data loaded successfully: {} courses, {} topics, {} subtopics",
                    totalCourses, totalTopics, totalSubtopics);

        } catch (Exception e) {
            log.error("Failed to load seed data: {}", e.getMessage(), e);
            throw e;
        }
    }
}
