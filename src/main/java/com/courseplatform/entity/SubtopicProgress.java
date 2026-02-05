package com.courseplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "subtopic_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "subtopic_id"}, name = "uk_progress_user_subtopic")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;

    @Builder.Default
    private Boolean completed = true;

    @CreationTimestamp
    @Column(name = "completed_at")
    private Instant completedAt;
}
