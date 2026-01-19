package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ExpertResult;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expert_reports",
        indexes = {
                @Index(name = "ux_expert_report_car", columnList = "car_id", unique = true)
        }
)
public class ExpertReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1-1: her car için tek rapor
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(length = 120)
    private String companyName;

    private LocalDate reportDate;

    @Column(length = 60)
    private String reportNo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ExpertResult result;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // parça kayıtları
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpertItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt = java.time.Instant.now();

    public java.time.Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }

    public ExpertReport() {}


    public void addItem(ExpertItem item) {
        item.setReport(this);
        items.add(item);
    }

    public void removeItem(ExpertItem item) {
        items.remove(item);
        item.setReport(null);
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

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

    public List<ExpertItem> getItems() { return items; }
    public void setItems(List<ExpertItem> items) { this.items = items; }

    public String getSummary() {
        return notes;
    }


}
