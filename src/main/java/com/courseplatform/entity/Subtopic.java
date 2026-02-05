package com.courseplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subtopics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtopic {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "subtopic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubtopicProgress> progressList = new ArrayList<>();
}
