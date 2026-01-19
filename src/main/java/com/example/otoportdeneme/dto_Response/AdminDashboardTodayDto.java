package com.example.otoportdeneme.dto_Response;

import java.util.List;

public class AdminDashboardTodayDto {

    public long totalVisits;
    public long guestVisits;
    public long clientVisits;
    public long storeVisits;

    public List<HourCount> hourly; // 0-23
    public long listingCreatesToday;
    public long listingDeletesToday;

    public List<StoreActivity> storeActivities; // store bazlı create/delete
    public List<StoreMsgSummary> storeMessages;  // store bazlı inquiry/message

    public static class HourCount {
        public int hour;
        public long count;
        public HourCount(int hour, long count){ this.hour = hour; this.count = count; }
    }

    public static class StoreActivity {
        public Long storeId;
        public String storeName;
        public long creates;
        public long deletes;

        public StoreActivity(Long storeId, String storeName, long creates, long deletes) {
            this.storeId = storeId;
            this.storeName = storeName;
            this.creates = creates;
            this.deletes = deletes;
        }
    }

    public static class StoreMsgSummary {
        public Long storeId;
        public String storeName;
        public long inquiries;
        public long messages;

        public StoreMsgSummary(Long storeId, String storeName, long inquiries, long messages) {
            this.storeId = storeId;
            this.storeName = storeName;
            this.inquiries = inquiries;
            this.messages = messages;
        }
    }
}
