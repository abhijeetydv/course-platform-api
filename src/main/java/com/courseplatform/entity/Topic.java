package com.courseplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Subtopic> subtopics = new ArrayList<>();

    /**
     * Helper method to add a subtopic and maintain bidirectional relationship
     */
    public void addSubtopic(Subtopic subtopic) {
        subtopics.add(subtopic);
        subtopic.setTopic(this);
    }
}
