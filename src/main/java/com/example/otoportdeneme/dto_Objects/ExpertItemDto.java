package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Enums.CarPart;
import com.example.otoportdeneme.Enums.PartStatus;

public class ExpertItemDto {
    private CarPart part;
    private PartStatus status;
    private String note;

    public ExpertItemDto() {}

    public ExpertItemDto(CarPart part, PartStatus status, String note) {
        this.part = part;
        this.status = status;
        this.note = note;
    }

    public CarPart getPart() { return part; }
    public void setPart(CarPart part) { this.part = part; }

    public PartStatus getStatus() { return status; }
    public void setStatus(PartStatus status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
