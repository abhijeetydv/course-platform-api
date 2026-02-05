package com.courseplatform.repository;

import com.courseplatform.entity.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
    
    List<Subtopic> findByTopicIdOrderByOrderIndexAsc(String topicId);
    
    @Query("SELECT s FROM Subtopic s JOIN FETCH s.topic t JOIN FETCH t.course WHERE s.id = :subtopicId")
    Optional<Subtopic> findByIdWithTopicAndCourse(@Param("subtopicId") String subtopicId);
    
    @Query("SELECT COUNT(s) FROM Subtopic s WHERE s.topic.course.id = :courseId")
    int countByCourseId(@Param("courseId") String courseId);
    
    @Query("SELECT s.id FROM Subtopic s WHERE s.topic.course.id = :courseId")
    List<String> findIdsByCourseId(@Param("courseId") String courseId);
}
