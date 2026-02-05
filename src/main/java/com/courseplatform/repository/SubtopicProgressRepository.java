package com.courseplatform.repository;

import com.courseplatform.entity.SubtopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {
    
    Optional<SubtopicProgress> findByUserIdAndSubtopicId(Long userId, String subtopicId);
    
    boolean existsByUserIdAndSubtopicId(Long userId, String subtopicId);
    
    @Query("SELECT sp FROM SubtopicProgress sp " +
           "JOIN FETCH sp.subtopic s " +
           "WHERE sp.user.id = :userId " +
           "AND s.topic.course.id = :courseId " +
           "AND sp.completed = true " +
           "ORDER BY sp.completedAt ASC")
    List<SubtopicProgress> findCompletedByUserIdAndCourseId(
            @Param("userId") Long userId, 
            @Param("courseId") String courseId);
    
    @Query("SELECT COUNT(sp) FROM SubtopicProgress sp " +
           "JOIN sp.subtopic s " +
           "WHERE sp.user.id = :userId " +
           "AND s.topic.course.id = :courseId " +
           "AND sp.completed = true")
    int countCompletedByUserIdAndCourseId(
            @Param("userId") Long userId, 
            @Param("courseId") String courseId);
}
