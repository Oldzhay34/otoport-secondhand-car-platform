package com.example.otoportdeneme.dto_Response;

public class HourlyPointDto {
    public int hour;      // 0..23
    public long count;

    public HourlyPointDto(int hour, long count) {
        this.hour = hour;
        this.count = count;
    }
}
