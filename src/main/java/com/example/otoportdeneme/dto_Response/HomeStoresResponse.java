package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.StoreCardDto;
import java.util.List;

public class HomeStoresResponse {
    private List<StoreCardDto> stores;

    public HomeStoresResponse() {}

    public HomeStoresResponse(List<StoreCardDto> stores) {
        this.stores = stores;
    }

    public List<StoreCardDto> getStores() { return stores; }
    public void setStores(List<StoreCardDto> stores) { this.stores = stores; }
}
