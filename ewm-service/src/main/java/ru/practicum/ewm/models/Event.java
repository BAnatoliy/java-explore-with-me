package ru.practicum.ewm.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.converter.DateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Transient
    private Long confirmedRequests;
    @Column(name = "created_on")
    //@Convert(converter = DateTimeConverter.class)
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    //@Convert(converter = DateTimeConverter.class)
    private LocalDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User initiator;
    @Embedded
    private Location location;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;
    @Transient
    private Long views;
    //@ManyToMany(mappedBy = "events")
    //private Set<Compilation> compilations;
}