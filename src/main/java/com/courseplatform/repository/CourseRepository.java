package com.courseplatform.repository;

import com.courseplatform.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @EntityGraph(attributePaths = { "topics", "topics.subtopics" })
    @Query("SELECT c FROM Course c WHERE c.id = :courseId")
    Optional<Course> findByIdWithTopicsAndSubtopics(@Param("courseId") String courseId);

    @Query("SELECT c FROM Course c")
    List<Course> findAllCourses();
}
