package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@ToString(exclude={"restaurant"})
@Table(name="operating_hours")
public class OperatingHours {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private LocalTime openTime;
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private DayOfWeek dayOfWeek;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName="id")
    private Restaurant restaurant;
}
