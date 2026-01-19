package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.InquiryThreadItemDto;
import java.util.List;

public class StoreInquiryListResponse {
    private List<InquiryThreadItemDto> items;

    public StoreInquiryListResponse() {}
    public StoreInquiryListResponse(List<InquiryThreadItemDto> items) { this.items = items; }

    public List<InquiryThreadItemDto> getItems() { return items; }
    public void setItems(List<InquiryThreadItemDto> items) { this.items = items; }
}
