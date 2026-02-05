package com.courseplatform.repository;

import com.courseplatform.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
    
    List<Topic> findByCourseIdOrderByOrderIndexAsc(String courseId);
    
    @Query("SELECT COUNT(s) FROM Topic t JOIN t.subtopics s WHERE t.course.id = :courseId")
    int countSubtopicsByCourseId(@Param("courseId") String courseId);
}
