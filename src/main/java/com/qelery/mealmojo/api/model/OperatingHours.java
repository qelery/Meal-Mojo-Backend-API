package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class OperatingHours {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column
    private LocalTime openTime;
    private LocalTime closeTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_profile_id")
    private MerchantProfile merchantProfile;

    @Override
    public String toString() {
        return "OperatingHours{" +
                "id=" + id +
                ", dayOfWeek=" + dayOfWeek +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", merchantProfile=" + merchantProfile +
                '}';
    }
}
