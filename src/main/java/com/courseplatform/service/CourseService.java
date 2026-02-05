package com.courseplatform.service;

import com.courseplatform.dto.response.*;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

        private final CourseRepository courseRepository;

        @Transactional(readOnly = true)
        public CourseListResponse getAllCourses() {
                log.debug("Fetching all courses");

                List<Course> courses = courseRepository.findAll();

                List<CourseSummaryDto> courseSummaries = courses.stream()
                                .map(this::mapToCourseSummary)
                                .collect(Collectors.toList());

                return CourseListResponse.builder()
                                .courses(courseSummaries)
                                .build();
        }

        @Transactional(readOnly = true)
        public CourseDetailResponse getCourseById(String courseId) {
                log.debug("Fetching course with id: {}", courseId);

                Course course = courseRepository.findByIdWithTopicsAndSubtopics(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

                return mapToCourseDetail(course);
        }

        private CourseSummaryDto mapToCourseSummary(Course course) {
                int topicCount = course.getTopics().size();
                int subtopicCount = course.getTopics().stream()
                                .mapToInt(topic -> topic.getSubtopics().size())
                                .sum();

                return CourseSummaryDto.builder()
                                .id(course.getId())
                                .title(course.getTitle())
                                .description(course.getDescription())
                                .topicCount(topicCount)
                                .subtopicCount(subtopicCount)
                                .build();
        }

        private CourseDetailResponse mapToCourseDetail(Course course) {
                List<TopicDto> topicDtos = course.getTopics().stream()
                                .map(this::mapToTopicDto)
                                .collect(Collectors.toList());

                return CourseDetailResponse.builder()
                                .id(course.getId())
                                .title(course.getTitle())
                                .description(course.getDescription())
                                .topics(topicDtos)
                                .build();
        }

        private TopicDto mapToTopicDto(Topic topic) {
                List<SubtopicDto> subtopicDtos = topic.getSubtopics().stream()
                                .map(this::mapToSubtopicDto)
                                .collect(Collectors.toList());

                return TopicDto.builder()
                                .id(topic.getId())
                                .title(topic.getTitle())
                                .subtopics(subtopicDtos)
                                .build();
        }

        private SubtopicDto mapToSubtopicDto(Subtopic subtopic) {
                return SubtopicDto.builder()
                                .id(subtopic.getId())
                                .title(subtopic.getTitle())
                                .content(subtopic.getContent())
                                .build();
        }
}
