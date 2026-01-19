package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.CarPart;
import com.example.otoportdeneme.Enums.PartStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "expert_items",
        indexes = {
                @Index(name = "ix_expert_item_report", columnList = "report_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_expert_item_report_part", columnNames = {"report_id", "part"})
        }
)
public class ExpertItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private ExpertReport report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CarPart part;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PartStatus status = PartStatus.UNKNOWN;

    @Column(length = 255)
    private String note; // "Çizik var" gibi

    public ExpertItem() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExpertReport getReport() { return report; }
    public void setReport(ExpertReport report) { this.report = report; }

    public CarPart getPart() { return part; }
    public void setPart(CarPart part) { this.part = part; }

    public PartStatus getStatus() { return status; }
    public void setStatus(PartStatus status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
