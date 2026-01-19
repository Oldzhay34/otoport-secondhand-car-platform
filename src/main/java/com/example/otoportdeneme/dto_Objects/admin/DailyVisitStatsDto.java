package com.example.otoportdeneme.dto_Objects.admin;

public class DailyVisitStatsDto {
    private long total;
    private long guest;
    private long client;
    private long store;

    public DailyVisitStatsDto() {}

    public DailyVisitStatsDto(long total, long guest, long client, long store) {
        this.total = total;
        this.guest = guest;
        this.client = client;
        this.store = store;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getGuest() { return guest; }
    public void setGuest(long guest) { this.guest = guest; }

    public long getClient() { return client; }
    public void setClient(long client) { this.client = client; }

    public long getStore() { return store; }
    public void setStore(long store) { this.store = store; }
}
