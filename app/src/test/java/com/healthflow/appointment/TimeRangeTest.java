package com.healthflow.appointment;

import com.healthflow.domain.model.appointment.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeRangeTest {
    @Test
    void shouldCreateValidTimeRange() {
        TimeRange range = new TimeRange(
                LocalTime.of(10, 0),
                LocalTime.of(10, 15)
        );

        assertEquals(LocalTime.of(10, 0), range.start());
        assertEquals(LocalTime.of(10, 15), range.end());
    }
}
