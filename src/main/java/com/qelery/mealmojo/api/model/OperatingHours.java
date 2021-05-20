package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter @Setter @ToString(exclude={"restaurant"})
@NoArgsConstructor @AllArgsConstructor
public class OperatingHours {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private DayOfWeek dayOfWeek;

    @Column
    private LocalTime openTime;
    private LocalTime closeTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurante_id")
    private Restaurant restaurant;
}
