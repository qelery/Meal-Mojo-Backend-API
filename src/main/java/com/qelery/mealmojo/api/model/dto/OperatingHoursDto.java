package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class OperatingHoursDto {

    private LocalTime openTime;
    private LocalTime closeTime;
    private DayOfWeek dayOfWeek;
}
