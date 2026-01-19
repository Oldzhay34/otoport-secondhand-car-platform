package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FuelType;
import com.example.otoportdeneme.Enums.Transmission;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars", indexes = {
        @Index(name = "ix_car_trim_year", columnList = "trim_id,year"),
        @Index(name = "ix_car_year_km", columnList = "year,kilometer"),
        @Index(name = "ix_car_fuel_trans", columnList = "fuelType,transmission"),
        @Index(name = "ix_car_body", columnList = "bodyType")
})
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trim_id", nullable = false)
    private Trim trim;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer kilometer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BodyType bodyType;

    private Integer engineVolumeCc;
    private Integer enginePowerHp;

    @Column(length = 60)
    private String color;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private ExpertReport expertReport;

    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarFeature> carFeatures = new ArrayList<>();

    public Car() {}

    public Long getId() { return id; }

    public Trim getTrim() { return trim; }
    public void setTrim(Trim trim) { this.trim = trim; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public Transmission getTransmission() { return transmission; }
    public void setTransmission(Transmission transmission) { this.transmission = transmission; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    public Integer getEngineVolumeCc() { return engineVolumeCc; }
    public void setEngineVolumeCc(Integer engineVolumeCc) { this.engineVolumeCc = engineVolumeCc; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public ExpertReport getExpertReport() { return expertReport; }
    public void setExpertReport(ExpertReport expertReport) { this.expertReport = expertReport; }

    public List<CarFeature> getCarFeatures() { return carFeatures; }
    public void setCarFeatures(List<CarFeature> carFeatures) { this.carFeatures = carFeatures; }

    @Transient
    public Brand getBrand() {
        if (trim == null) return null;
        if (trim.getModel() == null) return null;
        return trim.getModel().getBrand();
    }

    @Transient
    public CarModel getModel() {
        return (trim != null) ? trim.getModel() : null;
    }

    @Transient
    public String getTrimName() {
        return (trim != null) ? trim.getName() : null;
    }

    /**
     * ✅ Engine bilgisini nasıl tutuyorsun?
     * Şu an Car üzerinde engine string alanı yok.
     * Bu yüzden "engine" = Trim adı (ör: "2.0 TDI Design" gibi) olarak döndürüyoruz.
     * Eğer Trim içinde ayrı engine alanın varsa burada onu döndür.
     */
    @Transient
    public String getEngine() {
        return getTrimName();
    }
}
