package com.example.otoportdeneme.dto_Objects.admin;

import java.util.ArrayList;
import java.util.List;

public class HourlyTrafficDto {

    public static class HourCount {
        private int hour;     // 0-23
        private long count;

        public HourCount() {}
        public HourCount(int hour, long count) { this.hour = hour; this.count = count; }

        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    private List<HourCount> hours = new ArrayList<>();

    public HourlyTrafficDto() {}

    public HourlyTrafficDto(List<HourCount> hours) { this.hours = hours; }

    public List<HourCount> getHours() { return hours; }
    public void setHours(List<HourCount> hours) { this.hours = hours; }
}
