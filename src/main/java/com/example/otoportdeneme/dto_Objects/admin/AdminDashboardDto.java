package com.example.otoportdeneme.dto_Objects.admin;

import java.util.List;

public class AdminDashboardDto {
    public DailyTrafficDto today;
    public List<DailyTrafficDto> last7Days;
    public List<HourlyTrafficDto> todayHourly;

    public AdminDashboardDto() {}
}
