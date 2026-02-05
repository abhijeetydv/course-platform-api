package com.courseplatform.repository;

import com.courseplatform.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.topics t " +
           "LEFT JOIN FETCH t.subtopics " +
           "WHERE c.id = :courseId")
    Course findByIdWithTopicsAndSubtopics(@Param("courseId") String courseId);
    
    @Query("SELECT c FROM Course c")
    List<Course> findAllCourses();
}
