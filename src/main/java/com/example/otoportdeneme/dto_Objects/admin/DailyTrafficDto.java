package com.example.otoportdeneme.dto_Objects.admin;

public class DailyTrafficDto {
    public String date; // YYYY-MM-DD
    public long total;
    public long guests;
    public long users;

    public DailyTrafficDto() {}
    public DailyTrafficDto(String date, long total, long guests, long users) {
        this.date = date; this.total = total; this.guests = guests; this.users = users;
    }
}
