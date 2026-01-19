package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Enums.ExpertResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpertReportDto {

    private Long id;
    private Long carId;

    private String companyName;
    private LocalDate reportDate;
    private String reportNo;
    private ExpertResult result;
    private String notes;

    private List<ExpertItemDto> items = new ArrayList<>();

    public ExpertReportDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }

    public ExpertResult getResult() { return result; }
    public void setResult(ExpertResult result) { this.result = result; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<ExpertItemDto> getItems() { return items; }
    public void setItems(List<ExpertItemDto> items) { this.items = items; }

    public void setSummary(String summary) {

    }
}
