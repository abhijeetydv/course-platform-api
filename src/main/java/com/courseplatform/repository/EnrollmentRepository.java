package com.courseplatform.repository;

import com.courseplatform.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    boolean existsByUserIdAndCourseId(Long userId, String courseId);
    
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, String courseId);
    
    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(Long userId);
    
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.id = :enrollmentId AND e.user.id = :userId")
    Optional<Enrollment> findByIdAndUserIdWithCourse(@Param("enrollmentId") Long enrollmentId, @Param("userId") Long userId);
}
