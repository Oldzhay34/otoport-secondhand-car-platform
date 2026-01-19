package com.example.otoportdeneme.dto_Objects.admin;

public class StoreListingActivityDto {
    private Long storeId;
    private String storeName;
    private long creates;
    private long deletes;
    private long updates;

    public StoreListingActivityDto() {}

    public StoreListingActivityDto(Long storeId, String storeName, long creates, long deletes, long updates) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.creates = creates;
        this.deletes = deletes;
        this.updates = updates;
    }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public long getCreates() { return creates; }
    public void setCreates(long creates) { this.creates = creates; }

    public long getDeletes() { return deletes; }
    public void setDeletes(long deletes) { this.deletes = deletes; }

    public long getUpdates() { return updates; }
    public void setUpdates(long updates) { this.updates = updates; }
}
